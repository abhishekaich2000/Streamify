package com.abhishek.streamify.encodingservice.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.abhishek.streamify.encodingservice.event.VideoEncodedEvent;
import com.abhishek.streamify.encodingservice.event.VideoUploadedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service 
@Slf4j 
@RequiredArgsConstructor
public class EncodingService {

    private final S3Client s3Client;
    private final KafkaTemplate<String, VideoEncodedEvent> kafkaTemplate;;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${ffmpeg.path}")
    private String ffmpegPath;

    @Value("${encoding.base-path}")
    private String basePath;

    private static final String VIDEO_ENCODING_TOPIC = "video.encoded";

    private static final List<int[]> VIDEO_QUALITIES = Arrays.asList(
        new int[]{1920,5000,1080},  // 1080p- 500k bitrate
        new int[]{1280,2800,720},   // 720p- 2800k bitrate
        new int[]{640,1000,360},    // 360p- 100k bitrate
        new int[]{320,500,240}     // 240p- 50k bitrate
    );


    public void encodeVideo(VideoUploadedEvent videoUploadedEvent){
        log.info("Encoding video: {}", videoUploadedEvent.getMovieId());
        
        String jobPath=basePath + "/" + videoUploadedEvent.getMovieId();
        
        try{
            Files.createDirectories(Paths.get(jobPath));
            Files.createDirectories(Paths.get(jobPath + "/encoded"));
        
            
            String localVideoPath = jobPath + "/" + "/rawVideo.mp4";

            downloadVideoFromS3(videoUploadedEvent.getVideoKey(), localVideoPath);
            
            for(int[] quality : VIDEO_QUALITIES){
                int width = quality[0];
                int bitrate = quality[1];
                int height = quality[2];

                String encodedVideoPath = jobPath + "/encoded/" + width + "p.mp4";
                Files.createDirectories(Paths.get(encodedVideoPath));

                encodeToHLS(localVideoPath, encodedVideoPath, width, height, bitrate);
                
                log.info("Encoded video to {}p with bitrate {}k", width, bitrate);
            }

            String playlistPath = jobPath + "/encoded/playlist.m3u8";
            generatePlaylist(playlistPath);
            log.info("Generated playlist: {}", playlistPath);

            String encodedPrefix = "encoded/" + videoUploadedEvent.getMovieId() + "/";
            uploadEncodedVideoToS3( jobPath + "/encoded" , encodedPrefix);

            log.info("All videos encoded and uploaded to S3");

            String masterPlayListKey=encodedPrefix + "master.m3u8";
            String hlsUrl = "https://" + bucketName + ".s3.amazonaws.com/" + masterPlayListKey;
            
            VideoEncodedEvent event = new VideoEncodedEvent(
                videoUploadedEvent.getMovieId(),
                hlsUrl,
                hlsUrl,
                true,
                null
            );
            kafkaTemplate.send(VIDEO_ENCODING_TOPIC, videoUploadedEvent.getMovieId(), event);
            
            log.info("Video encoded event sent to Kafka for movie: {}", videoUploadedEvent.getMovieId());
        }catch(Exception e){
            log.error("Encoding failed for movie: {}", videoUploadedEvent.getMovieId(), e);
            VideoEncodedEvent event = new VideoEncodedEvent(
                videoUploadedEvent.getMovieId(),
                null,
                null,
                false,
                e.getMessage()
            );
            kafkaTemplate.send(VIDEO_ENCODING_TOPIC, videoUploadedEvent.getMovieId(), event);
            log.error("Video encoded event sent to Kafka for movie: {}", videoUploadedEvent.getMovieId());
        }finally{
            cleanupPaths(jobPath);
        }
    }

    private void downloadVideoFromS3(String s3VideoKey, String localVideoPath){
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
            .bucket(bucketName)
            .key(s3VideoKey)
            .build();
        
        s3Client.getObject(getObjectRequest, Paths.get(localVideoPath));
        log.info("Downloaded video from S3 to: {}", localVideoPath);
    }

    private void encodeToHLS(String inputPath, String outputPath, int width, int height, int bitrate) throws IOException , InterruptedException {
        String playlistPath = outputPath + "/playlist.m3u8";

        String segmentPattern = outputPath + "/segment_%05d.ts";

        List<String> commands = Arrays.asList(
            ffmpegPath,
            "-i", inputPath,
            "-vf", "scale=" + width + ":" + height,
            "-c:v", "libx264",
            "-b:v", bitrate + "k",
            "-c:a", "aac",
            "-b:a", "128k",
            "-hls_time", "10",
            "-hls_list_size", "0",
            "-hls_segment_filename", segmentPattern,
            "-f", "hls",
            playlistPath
        );
        
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.redirectErrorStream(true);
        Process process = processBuilder.start();

        int exitCode = process.waitFor();
        if(exitCode != 0){
            throw new RuntimeException("FFmpeg encoding failed with exit code: " + exitCode);
        }
    }

    private void generatePlaylist(String masterPlaylistPath) throws IOException {
        StringBuilder playlist = new StringBuilder();
        playlist.append("#EXTM3U\n");
        playlist.append("#EXT-X-VERSION:3\n");

        int [][] qualities = {{1920,5000,1080},{1280,2800,720}, 
            {640,1000,360}, {320,500,240}};
        
        for(int[] quality : qualities){
            int width = quality[0];
            int bitrate = quality[1];
            int height = quality[2];

            playlist.append("#EXT-X-STREAM-INF:BANDWIDTH=")
                .append(bitrate*1000)
                .append(",RESOLUTION=")
                .append(width)
                .append("x")
                .append(height)
                .append(",CODECS=\"avc1.42E01E,mp4a.40.2\"\n");
            playlist.append(height).append("p/playlist.m3u8\n\n");
        }

        Files.writeString(Paths.get(masterPlaylistPath), playlist.toString());
    }

    private void uploadEncodedVideoToS3(String localDirectory, String s3Prefix) throws IOException {
        File directory = new File(localDirectory);
        uploadDirectoryToS3(directory, s3Prefix);
    }
    
    private void uploadDirectoryToS3(File directory, String s3Prefix) throws IOException {
        for(File file : directory.listFiles()){
            if(file.isDirectory()){
                uploadDirectoryToS3(file, s3Prefix + file.getName() + "/");
            }else{
                String relativePath = file.getAbsolutePath()
                    .substring(basePath.length()+1)
                    .replace("\\", "/");
                
                String s3Key = s3Prefix + relativePath;

                String contentType = file.getName().endsWith(".m3u8") ? 
                    "application/x-mpegURL" : "video/MP2T";

                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(s3Key)
                    .contentType(contentType)
                    .build();
                s3Client.putObject(putObjectRequest, RequestBody.fromFile(file));
                log.debug("Uploaded file to S3: {}", s3Key);
            }
        }
    }

    private void cleanupPaths(String jobDir){
        try{
            Path jobPath = Paths.get(jobDir);
            if(Files.exists(jobPath)){
                Files.walk(jobPath)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
            }
            log.info("Cleaned up job directory: {}", jobDir);
        }catch(IOException e){
            log.warn("Failed to cleanup job directory: {}", jobDir, e);
        }
    }
}
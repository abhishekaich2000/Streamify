package com.abhishek.streamify.videoservice.service;

import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.abhishek.streamify.videoservice.config.KafkaConfig;
import com.abhishek.streamify.videoservice.event.VideoUploadedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service 
@RequiredArgsConstructor
@Slf4j 
public class VideoService {

    private final S3Client s3Client;
    private final KafkaTemplate<String, VideoUploadedEvent> kafkaTemplate;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    public String uploadVideo(String movieId, MultipartFile file) throws IOException {
        log.info("Starting video upload for movieId: {} and filename: {}", movieId, file.getOriginalFilename());
    
        String videoKey = "raw/" + movieId + "/" + 
            UUID.randomUUID() + "_" + file.getOriginalFilename();
        
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(videoKey)
            .contentType(file.getContentType())
            .contentLength(file.getSize())
            .build();
        
        s3Client.putObject(putObjectRequest, 
            RequestBody.fromBytes(file.getBytes()));

        log.info("Video uploaded successfully to S3 with key: {}", videoKey);
        
        VideoUploadedEvent event = new VideoUploadedEvent(
            movieId,
            videoKey,
            bucketName,
            file.getOriginalFilename(),
            String.valueOf(file.getSize())
        );
        
        kafkaTemplate.send(KafkaConfig.VIDEO_UPLOADED_TOPIC, movieId, event);
        log.info("Video uploaded event sent to Kafka topic for movieId: {}  ", movieId);

        return videoKey;
    }
}

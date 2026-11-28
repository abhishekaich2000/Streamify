package com.abhishek.streamify.encodingservice.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.abhishek.streamify.encodingservice.event.VideoUploadedEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class VideoEventConsumer {

    private final EncodingService encodingService;

    @KafkaListener (
        topics = "video.uploaded",
        groupId = "encoding-service-group"
    )
    public void consumeVideoUploadEvent(VideoUploadedEvent videoUploadedEvent) {
        log.info("Received video uploaded event: {}", videoUploadedEvent);
        
        try{
            encodingService.encodeVideo(videoUploadedEvent);
        }catch(Exception e){
            log.error("Error processing video encoded event: {} with error: {}", videoUploadedEvent.getMovieId(), e.getMessage());
            
        }
    }
}

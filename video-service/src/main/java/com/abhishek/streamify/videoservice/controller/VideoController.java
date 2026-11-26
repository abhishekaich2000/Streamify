package com.abhishek.streamify.videoservice.controller;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.abhishek.streamify.videoservice.service.VideoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping("/api/videos")
@RequiredArgsConstructor
@Slf4j
public class VideoController {

    private final VideoService videoService;

    @PostMapping("/upload/{movieId}")
    public ResponseEntity<String> uploadVideo (
        @PathVariable String movieId,
        @RequestParam ("file") MultipartFile file
    ) throws IOException {
        log.info("Upload request received for movieId: {} and file size: {} bytes", movieId, file.getSize());
        
        if(file.isEmpty()){
            return ResponseEntity.badRequest().body("File is empty");
        }

        String videoKey = videoService.uploadVideo(movieId, file);
        
        return ResponseEntity.ok("Video uploaded successfully with key: " + videoKey);
    }
}

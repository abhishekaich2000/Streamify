package com.abhishek.streamify.encodingservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
public class VideoUploadedEvent {
    private String movieId;
    private String videoKey;
    private String bucketName;
    private String fileName;
    private String fileSize;
}

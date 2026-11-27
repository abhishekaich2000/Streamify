package com.abhishek.streamify.encodingservice.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class VideoEncodedEvent {
    private String movieId;
    private String hlsPlaylistUrl;
    private String masterPlaylistUrl;
    private boolean isSuccess;
    private String errorMessage;
}

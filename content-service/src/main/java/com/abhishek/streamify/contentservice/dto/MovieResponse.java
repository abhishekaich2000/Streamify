package com.abhishek.streamify.contentservice.dto;

import java.time.LocalDateTime;

import com.abhishek.streamify.contentservice.model.Genre;
import com.abhishek.streamify.contentservice.model.VideoStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor 
@NoArgsConstructor 
public class MovieResponse {

    private String id;
    private String title;
    private String description;
    private Genre genre;
    private String director;
    private String cast;
    private int releaseYear;
    private int rating;
    private String thumbnailUrl;
    private int movieDurationMinutes;
    private String videoKey;
    private String hlsUrl;
    private VideoStatus videoStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

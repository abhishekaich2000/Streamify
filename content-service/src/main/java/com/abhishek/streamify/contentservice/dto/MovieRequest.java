package com.abhishek.streamify.contentservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor 
@NoArgsConstructor
@Getter 
@Setter 
public class MovieRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull (message = "Genre is required")
    private String genre;

    private String director;

    private String cast;

    private int releaseYear;

    private int rating;

    private String thumbnailUrl;

    private int movieDurationMinutes;
}

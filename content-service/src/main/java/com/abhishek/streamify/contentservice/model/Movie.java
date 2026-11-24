package com.abhishek.streamify.contentservice.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Table(name = "movie")
@NoArgsConstructor 
@AllArgsConstructor 
@Getter 
@Setter 
public class Movie {
    
    @Id 
    @GeneratedValue (strategy=GenerationType.UUID)
    private String id;

    @Column (nullable = false)
    private String title;

    @Column (length = 500)
    private String description;

    @Enumerated (EnumType.STRING)
    private Genre genre;

    private String director;

    private String cast;

    private int releaseYear;

    private int rating;

    private String thumbnailUrl;

    private int movieDurationMinutes;

    private String videoKey;       // S3 key for the video file

    private String hlsUrl;

    @Enumerated (EnumType.STRING)
    private VideoStatus videoStatus;

    @CreationTimestamp 
    private LocalDateTime createdAt;

    @UpdateTimestamp 
    private LocalDateTime updatedAt;
}

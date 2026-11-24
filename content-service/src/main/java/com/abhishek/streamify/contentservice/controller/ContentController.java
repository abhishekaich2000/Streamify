package com.abhishek.streamify.contentservice.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.abhishek.streamify.contentservice.dto.MovieRequest;
import com.abhishek.streamify.contentservice.dto.MovieResponse;
import com.abhishek.streamify.contentservice.service.ContentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController 
@RequestMapping ("/api/content")
@Slf4j 
@RequiredArgsConstructor 
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ResponseEntity<MovieResponse> addMovie(
        @Valid @RequestBody MovieRequest movieRequest
    ){
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(contentService.addMovie(movieRequest));
    }

    @GetMapping 
    public ResponseEntity<List<MovieResponse>> getAllMovies(){
        return ResponseEntity.ok(contentService.getAllMovies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<MovieResponse> getMovieById(@PathVariable String id){
        return ResponseEntity.ok(contentService.getMovieById(id));
    }

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieResponse>> getMovieByGenre(@PathVariable String genre){
        return ResponseEntity.ok(contentService.getMovieByGenre(genre));
    }

    @GetMapping("/search")
    public ResponseEntity<List<MovieResponse>> searchMoviesByTitle(@RequestParam String title){
        return ResponseEntity.ok(contentService.searchMoviesByTitle(title));
    }
}

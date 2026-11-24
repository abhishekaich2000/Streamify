package com.abhishek.streamify.contentservice.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.abhishek.streamify.contentservice.dto.MovieRequest;
import com.abhishek.streamify.contentservice.dto.MovieResponse;
import com.abhishek.streamify.contentservice.model.Movie;
import com.abhishek.streamify.contentservice.model.VideoStatus;
import com.abhishek.streamify.contentservice.repository.ContentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service 
@Slf4j 
@RequiredArgsConstructor 
public class ContentService {

    private final ContentRepository contentRepository;

    public MovieResponse addMovie(MovieRequest movieRequest) {
        log.info("Adding new movie : {}", movieRequest.getTitle());

        Movie movie = new Movie();
        movie.setTitle(movieRequest.getTitle());
        movie.setDescription(movieRequest.getDescription());
        movie.setGenre(movieRequest.getGenre());
        movie.setDirector(movieRequest.getDirector());
        movie.setCast(movieRequest.getCast());
        movie.setReleaseYear(movieRequest.getReleaseYear());
        movie.setRating(movieRequest.getRating());
        movie.setThumbnailUrl(movieRequest.getThumbnailUrl());
        movie.setMovieDurationMinutes(movieRequest.getMovieDurationMinutes());
        
        Movie savedMovie = contentRepository.save(movie);

        log.info("Movie saved {}", savedMovie.getId());
        return convertToResponse(savedMovie);
    }

    public List<MovieResponse> getAllMovies() {
        return contentRepository.findAll().stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public MovieResponse getMovieById(String id) {
        Movie movie = contentRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return convertToResponse(movie);
    }

    public List<MovieResponse> getMovieByGenre(String genre){
        return contentRepository.findByGenre(genre).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public List<MovieResponse> searchMoviesByTitle(String title){
        return contentRepository.findByTitleContainingIgnoreCase(title).stream()
            .map(this::convertToResponse)
            .collect(Collectors.toList());
    }

    public void updateVideoKey(String movieId, String videoKey){
        log.info("Updating video key for movie {}", movieId);
        Movie movie = contentRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
        
        movie.setVideoKey(videoKey);
        movie.setVideoStatus(VideoStatus.UPLOADED);
        contentRepository.save(movie);
    }

    public void updateHlsUrl(String movieId, String hlsUrl){
        log.info("Updating HLS URL for movie {}", movieId);
        Movie movie = contentRepository.findById(movieId)
            .orElseThrow(() -> new RuntimeException("Movie not found with id: " + movieId));
        
        movie.setHlsUrl(hlsUrl);
        movie.setVideoStatus(VideoStatus.READY);
        contentRepository.save(movie);
        log.info("Movie is ready to play {}", movieId);
    }

    private MovieResponse convertToResponse(Movie movie) {
        MovieResponse response = new MovieResponse();
        response.setId(movie.getId());
        response.setTitle(movie.getTitle());
        response.setDescription(movie.getDescription());
        response.setGenre(movie.getGenre());
        response.setDirector(movie.getDirector());
        response.setCast(movie.getCast());
        response.setReleaseYear(movie.getReleaseYear());
        response.setRating(movie.getRating());
        response.setThumbnailUrl(movie.getThumbnailUrl());
        response.setVideoKey(movie.getVideoKey());
        response.setHlsUrl(movie.getHlsUrl());
        response.setVideoStatus(movie.getVideoStatus());
        response.setMovieDurationMinutes(movie.getMovieDurationMinutes());
        return response;
    }
}

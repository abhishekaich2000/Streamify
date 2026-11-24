package com.abhishek.streamify.contentservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.abhishek.streamify.contentservice.model.Movie;
    
public interface ContentRepository extends JpaRepository<Movie, String> {

    public Optional<Movie> findById(String id);

    public List<Movie> findByGenre(String genre);

    public List<Movie> findByTitleContainingIgnoreCase(String title);
}

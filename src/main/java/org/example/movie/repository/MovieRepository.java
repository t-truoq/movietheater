package org.example.movie.repository;

import org.example.movie.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByMovieNameVnContainingIgnoreCase(String movieNameVn);
    List<Movie> findByMovieNameVnContainingIgnoreCaseOrMovieNameEnglishContainingIgnoreCase(String movieNameVn, String movieNameEnglish);
}
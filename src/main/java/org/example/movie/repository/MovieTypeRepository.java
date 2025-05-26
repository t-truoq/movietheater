package org.example.movie.repository;

import org.example.movie.entity.MovieType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MovieTypeRepository extends JpaRepository<MovieType, Long> {
    void deleteByMovieMovieId(Long movieId);
    List<MovieType> findByMovieMovieId(Long movieId);
}
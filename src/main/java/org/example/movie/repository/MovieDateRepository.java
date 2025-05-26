package org.example.movie.repository;

import org.example.movie.entity.MovieDate;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieDateRepository extends JpaRepository<MovieDate, Long> {
    List<MovieDate> findByMovie_MovieId(Long movieId);
}
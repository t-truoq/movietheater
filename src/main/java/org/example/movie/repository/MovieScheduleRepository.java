package org.example.movie.repository;

import org.example.movie.entity.MovieSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MovieScheduleRepository extends JpaRepository<MovieSchedule, Long> {
    List<MovieSchedule> findByMovie_MovieId(Long movieId);
    void deleteByMovieMovieId(Long movieId);
    List<MovieSchedule> findByMovieMovieId(Long movieId);
}
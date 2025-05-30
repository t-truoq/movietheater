package org.example.movie.repository;



import org.example.movie.entity.ScheduleSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ScheduleSeatRepository extends JpaRepository<ScheduleSeat, Long> {
    List<ScheduleSeat> findAllById(Iterable<Long> ids);
    List<ScheduleSeat> findBySchedule_ScheduleIdAndMovie_MovieId(Long scheduleId, Long movieId);
    List<ScheduleSeat> findByMovieMovieId(Long movieId);
    Optional<ScheduleSeat> findFirstBySchedule_ScheduleId(Long scheduleId);
}
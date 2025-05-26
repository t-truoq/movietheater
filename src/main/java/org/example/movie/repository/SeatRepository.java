package org.example.movie.repository;

import org.example.movie.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findByCinemaRoomCinemaRoomId(Long cinemaRoomId);
}
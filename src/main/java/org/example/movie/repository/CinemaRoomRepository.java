package org.example.movie.repository;

import org.example.movie.entity.CinemaRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CinemaRoomRepository extends JpaRepository<CinemaRoom, Long> {
    Optional<CinemaRoom> findById(Long id);

}
package org.example.movie.repository;

import org.example.movie.entity.ShowDate;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowDateRepository extends JpaRepository<ShowDate, Long> {
}
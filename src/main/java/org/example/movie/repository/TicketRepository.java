package org.example.movie.repository;

import org.example.movie.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    List<Ticket> findByScheduleSeat_ScheduleSeatId(Long scheduleSeatId);

}
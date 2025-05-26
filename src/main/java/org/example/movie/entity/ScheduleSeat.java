package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.movie.enums.SeatStatus;

import java.util.List;
@Entity
@Table(name = "MOVIETHEATER_SCHEDULE_SEAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleSeat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_SEAT_ID")
    private Long scheduleSeatId;

    @ManyToOne
    @JoinColumn(name = "schedule_id", referencedColumnName = "SCHEDULE_ID")
    private Schedule schedule;

    @ManyToOne
    @JoinColumn(name = "movie_id", referencedColumnName = "MOVIE_ID")
    private Movie movie;

    @Column(name = "SEAT_COLUMN")
    private String seatColumn;

    @Column(name = "SEAT_ROW")
    private Integer seatRow;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEAT_STATUS")
    private SeatStatus seatStatus;

    @Column(name = "SEAT_TYPE")
    private Integer seatType;

    @ManyToOne
    @JoinColumn(name = "SEAT_ID", referencedColumnName = "SEAT_ID")
    private Seat seat;

    @OneToMany(mappedBy = "scheduleSeat")
    private List<Ticket> tickets;
}

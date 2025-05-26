package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;
import org.example.movie.enums.SeatStatus;
import org.example.movie.enums.SeatType;

import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_SEAT")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Seat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SEAT_ID")
    private Long seatId;

    @Column(name = "SEAT_COLUMN")
    private String seatColumn;

    @Column(name = "SEAT_ROW")
    private Integer seatRow;


    @Enumerated(EnumType.STRING)
    @Column(name = "SEAT_STATUS")
    private SeatStatus seatStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "SEAT_TYPE")
    private SeatType seatType;

    @ManyToOne
    @JoinColumn(name = "CINEMA_ROOM_ID", referencedColumnName = "CINEMA_ROOM_ID")
    private CinemaRoom cinemaRoom;

    @OneToMany(mappedBy = "seat")
    private List<ScheduleSeat> scheduleSeats;
}
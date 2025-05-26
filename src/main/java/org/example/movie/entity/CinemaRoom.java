package org.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_CINEMA_ROOM")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CinemaRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CINEMA_ROOM_ID")
    private Long cinemaRoomId;

    @Column(name = "CINEMA_ROOM_NAME")
    private String cinemaRoomName;

    @Column(name = "SEAT_QUANTITY")
    private Integer seatQuantity;

    @OneToMany(mappedBy = "cinemaRoom")
    private List<Seat> seats;

    @OneToMany(mappedBy = "cinemaRoom")
    @JsonIgnore
    private List<Movie> movies;
}
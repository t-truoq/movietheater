package org.example.movie.dto.response;


import lombok.Data;

@Data
public class CinemaRoomResponse {
    private Long cinemaRoomId;
    private String cinemaRoomName;
    private Integer seatQuantity;
}
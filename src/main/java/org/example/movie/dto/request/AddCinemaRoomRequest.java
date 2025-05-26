package org.example.movie.dto.request;

import lombok.Data;

@Data
public class AddCinemaRoomRequest {
    private String cinemaRoomName;
    private Integer seatQuantity;
}
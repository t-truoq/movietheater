package org.example.movie.dto.response;

import lombok.Data;
import org.example.movie.enums.SeatStatus;
import org.example.movie.enums.SeatType;

@Data
public class SeatResponse {
    private Long scheduleSeatId;
    private String seatColumn;
    private Integer seatRow;
    private SeatStatus seatStatus; // 0: available, 1: booked
    private SeatType seatType;
}
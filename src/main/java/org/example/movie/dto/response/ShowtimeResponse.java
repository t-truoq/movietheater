package org.example.movie.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class ShowtimeResponse {
    private Long scheduleId;
    private LocalDate showDate;
    private LocalTime showTime;
    private String cinemaRoomName;
}
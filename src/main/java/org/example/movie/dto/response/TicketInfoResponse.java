package org.example.movie.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TicketInfoResponse {
    private Long bookingId;
    private String movieName;
    private String cinemaRoomName;
    private LocalDateTime date;
    private LocalDateTime time;
    private List<String> seat;
    private Integer price;
    private Integer scoreForTicketConverting;
    private Integer total;
    private Long memberId;
    private String fullName;
    private String identityCard;
    private String phoneNumber;
}
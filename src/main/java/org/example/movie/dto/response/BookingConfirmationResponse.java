package org.example.movie.dto.response;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingConfirmationResponse {
    private Long bookingId;
    private String movieName;
    private String screen;
    private LocalDateTime date;
    private LocalDateTime time;
    private List<String> seat;
    private Integer price;
    private Integer total;
    private Long memberId;
    private String fullName;
    private Integer memberScore;
    private String identityCard;
    private String phoneNumber;
    private Boolean convertedToTicket;
    private Integer scoreUsed;
}
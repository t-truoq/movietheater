package org.example.movie.dto.response;

import lombok.Data;

@Data
public class BookingListResponse {
    private Long bookingId;
    private String identityCard;
    private String phoneNumber;
    private String movie;
    private String time;
}
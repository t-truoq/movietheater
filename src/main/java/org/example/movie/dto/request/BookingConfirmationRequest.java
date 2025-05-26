package org.example.movie.dto.request;

import lombok.Data;

@Data
public class BookingConfirmationRequest {
    private Long bookingId;
    private Boolean convertToTicket; // Radio option
    private Integer useScore; // Điểm muốn sử dụng
}
package org.example.movie.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookedTicketResponse {
    private String movieName;
    private LocalDate bookingDate;
    private Integer totalAmount;
    private Integer status;
    private Long invoiceId;
}
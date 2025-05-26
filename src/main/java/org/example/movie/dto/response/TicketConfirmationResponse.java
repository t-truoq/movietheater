package org.example.movie.dto.response;

import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
public class    TicketConfirmationResponse {
    private Long invoiceId;
    private String movieName;
    private LocalDate scheduleShowDate;
    private String scheduleShowTime;
    private List<String> seatNumbers; // seatColumn + seatRow
    private Integer totalPrice;
}
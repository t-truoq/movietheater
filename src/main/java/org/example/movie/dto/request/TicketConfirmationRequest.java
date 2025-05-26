package org.example.movie.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TicketConfirmationRequest {

    @NotNull(message = "Invoice ID Not Null")
    private Long invoiceId;

    @NotNull(message = "Schedule ID Not Null")
    private Long scheduleId;

    @Min(value = 0, message = "Must be > 0")
    private Integer useScore;

    private Long promotionId;
}
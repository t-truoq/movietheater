package org.example.movie.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateSeatTypeRequest {
    private List<Long> seatIds;
    private Integer newSeatType; // 0 = Normal, 1 = VIP
}
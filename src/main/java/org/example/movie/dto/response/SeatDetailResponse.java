package org.example.movie.dto.response;



import lombok.Data;
import org.example.movie.enums.SeatType;

@Data
public class SeatDetailResponse {
    private Long seatId;
    private String seatColumn;
    private Integer seatRow;
    private SeatType seatType; // 0 = Normal, 1 = VIP
}
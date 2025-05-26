package org.example.movie.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class SelectSeatsRequest {
    private Long scheduleId;
    private List<Long> scheduleSeatIds;
}
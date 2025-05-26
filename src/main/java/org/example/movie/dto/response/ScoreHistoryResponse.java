package org.example.movie.dto.response;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ScoreHistoryResponse {
    private LocalDate date;
    private String movieName;
    private Integer amount;
    private String type;
}
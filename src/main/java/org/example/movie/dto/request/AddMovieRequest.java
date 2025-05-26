package org.example.movie.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddMovieRequest {
    private String movieNameVn;
    private String movieNameEnglish;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String actor;
    private String movieProductionCompany;
    private String director;
    private Integer duration;
    private String version;
    private List<Long> typeIds;
    private Long cinemaRoom;
    private String content;
    private String largeImage;
    private List<LocalDateTime> scheduleTimes;
}
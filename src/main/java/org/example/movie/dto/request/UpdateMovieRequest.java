package org.example.movie.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class UpdateMovieRequest {
    private Long movieId;
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
    private Long cinemaRoomId;
    private String content;
    private String largeImage;
}
package org.example.movie.dto.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieResponse {
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
    private String content;
    private String largeImage;
    private Long cinemaRoomId;
}
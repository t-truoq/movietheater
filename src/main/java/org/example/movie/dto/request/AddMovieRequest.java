package org.example.movie.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class AddMovieRequest {
    private String movieNameVn;
    private String movieNameEnglish;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate fromDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private List<LocalDateTime> scheduleTimes;
}
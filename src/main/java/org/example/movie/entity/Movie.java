package org.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_MOVIE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "MOVIE_ID")
    private Long movieId;

    @Column(name = "ACTOR")
    private String actor;

    @Column(name = "CONTENT")
    private String content;

    @Column(name = "DIRECTOR")
    private String director;

    @Column(name = "DURATION")
    private Integer duration;

    @Column(name = "FROM_DATE")
    private LocalDate fromDate;

    @Column(name = "LARGE_IMAGE")
    private String largeImage;

    @Column(name = "MOVIE_NAME_ENGLISH")
    private String movieNameEnglish;

    @Column(name = "MOVIE_NAME_VN")
    private String movieNameVn;

    @Column(name = "MOVIE_PRODUCTION_COMPANY")
    private String movieProductionCompany;

    @Column(name = "TO_DATE")
    private LocalDate toDate;

    @Column(name = "VERSION")
    private String version;

    @ManyToOne
    @JoinColumn(name = "CINEMA_ROOM_ID", referencedColumnName = "CINEMA_ROOM_ID")
    @JsonIgnore
    private CinemaRoom cinemaRoom;

    @OneToMany(mappedBy = "movie")
    private List<MovieDate> movieDates;

    @OneToMany(mappedBy = "movie")
    private List<MovieSchedule> movieSchedules;

    @OneToMany(mappedBy = "movie")
    private List<MovieType> movieTypes;
}
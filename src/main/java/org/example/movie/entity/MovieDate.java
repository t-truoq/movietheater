package org.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
@Entity
@Table(name = "MOVIETHEATER_MOVIE_DATE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovieDate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MOVIE_ID", insertable = false, updatable = false)
    private Long movieId;

    @Column(name = "SHOW_DATE_ID", insertable = false, updatable = false)
    @JsonIgnore
    private Long showDateId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MOVIE_ID", referencedColumnName = "MOVIE_ID")
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SHOW_DATE_ID", referencedColumnName = "SHOW_DATE_ID")
    private ShowDate showDate;
}

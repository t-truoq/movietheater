package org.example.movie.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "MOVIETHEATER_SCHEDULE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SCHEDULE_ID")
    private Long scheduleId;

    @Column(name = "SCHEDULE_TIME")
    private String scheduleTime;

    @OneToMany(mappedBy = "schedule")
    @JsonIgnore
    private List<MovieSchedule> movieSchedules;
}
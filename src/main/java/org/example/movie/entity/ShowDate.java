    package org.example.movie.entity;

    import jakarta.persistence.*;
    import lombok.*;

    import java.time.LocalDate;
    import java.util.List;

    @Entity
    @Table(name = "MOVIETHEATER_SHOW_DATES")
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public class ShowDate {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "SHOW_DATE_ID")
        private Long showDateId;

        @Column(name = "SHOW_DATE")
        private LocalDate showDate;

        @Column(name = "DATE_NAME")
        private String dateName;

        @OneToMany(mappedBy = "showDate")
        private List<MovieDate> movieDates;
    }
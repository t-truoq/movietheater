package org.example.movie.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "MOVIETHEATER_PROMOTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Promotion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PROMOTION_ID")
    private Long promotionId;

    @Column(name = "DETAIL")
    private String detail;

    @Column(name = "DISCOUNT_LEVEL")
    private Integer discountLevel;

    @Column(name = "END_TIME")
    private LocalDateTime endTime;

    @Column(name = "IMAGE")
    private String image;

    @Column(name = "START_TIME")
    private LocalDateTime startTime;

    @Column(name = "TITLE")
    private String title;
}
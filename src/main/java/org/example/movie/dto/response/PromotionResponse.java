package org.example.movie.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PromotionResponse {
    private Long promotionId;
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer discountLevel;
    private String detail;
    private String image;
}
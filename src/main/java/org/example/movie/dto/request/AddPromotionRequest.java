package org.example.movie.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPromotionRequest {
    private String title;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer discountLevel;
    private String detail;
    private String image;
}
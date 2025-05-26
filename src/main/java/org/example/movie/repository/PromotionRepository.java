package org.example.movie.repository;

import org.example.movie.entity.Promotion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromotionRepository extends JpaRepository<Promotion, Long> {
    Optional<Promotion> findByPromotionIdAndEndTimeAfter(Long promotionId, java.time.LocalDateTime now);
    List<Promotion> findByTitleContainingIgnoreCase(String title);
}
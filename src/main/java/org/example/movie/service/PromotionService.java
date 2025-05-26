package org.example.movie.service;

import org.example.movie.dto.request.AddPromotionRequest;
import org.example.movie.dto.request.UpdatePromotionRequest;
import org.example.movie.dto.response.PromotionResponse;
import org.example.movie.entity.Promotion;
import org.example.movie.exception.AppException;
import org.example.movie.exception.ErrorCode;
import org.example.movie.mapper.PromotionMapper;
import org.example.movie.repository.PromotionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PromotionService {

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired
    private PromotionMapper promotionMapper;

    @PreAuthorize("hasRole('ADMIN')")
    public List<PromotionResponse> getPromotionList(String searchKeyword) {
        List<Promotion> promotions = searchKeyword == null || searchKeyword.isEmpty()
                ? promotionRepository.findAll()
                : promotionRepository.findByTitleContainingIgnoreCase(searchKeyword);
        return promotions.stream()
                .map(promotionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String addPromotion(AddPromotionRequest request) {
        // Validation: startTime < endTime
        if (request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_PROMOTION, "Start time must be before end time");
        }

        Promotion promotion = promotionMapper.toEntity(request);
        promotionRepository.save(promotion);
        return "Successfully add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String updatePromotion(Long promotionId, UpdatePromotionRequest request) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        // Validation: startTime < endTime
        if (request.getStartTime() != null && request.getEndTime() != null && request.getStartTime().isAfter(request.getEndTime())) {
            throw new AppException(ErrorCode.INVALID_PROMOTION, "Start time must be before end time");
        }

        promotionMapper.updateEntityFromRequest(request, promotion);
        promotionRepository.save(promotion);
        return "Successfully add";
    }

    @PreAuthorize("hasRole('ADMIN')")
    @Transactional
    public String deletePromotion(Long promotionId) {
        Promotion promotion = promotionRepository.findById(promotionId)
                .orElseThrow(() -> new AppException(ErrorCode.PROMOTION_NOT_FOUND));

        promotionRepository.delete(promotion);
        return "Promotion deleted successfully";
    }
}
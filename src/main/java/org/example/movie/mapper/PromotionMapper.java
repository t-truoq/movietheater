package org.example.movie.mapper;

import org.example.movie.dto.request.AddPromotionRequest;
import org.example.movie.dto.request.UpdatePromotionRequest;
import org.example.movie.dto.response.PromotionResponse;
import org.example.movie.entity.Promotion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface PromotionMapper {

    PromotionResponse toResponse(Promotion promotion);

    Promotion toEntity(AddPromotionRequest request);

    void updateEntityFromRequest(UpdatePromotionRequest request, @MappingTarget Promotion promotion);
}
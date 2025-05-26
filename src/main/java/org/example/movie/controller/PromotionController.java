package org.example.movie.controller;

import org.example.movie.dto.request.AddPromotionRequest;
import org.example.movie.dto.request.UpdatePromotionRequest;
import org.example.movie.dto.response.PromotionResponse;
import org.example.movie.service.PromotionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/promotions")
public class PromotionController {

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public ResponseEntity<List<PromotionResponse>> getPromotionList(
            @RequestParam(value = "search", required = false) String searchKeyword) {
        List<PromotionResponse> promotions = promotionService.getPromotionList(searchKeyword);
        return ResponseEntity.ok(promotions);
    }

    @PostMapping
    public ResponseEntity<String> addPromotion(@RequestBody AddPromotionRequest request) {
        String response = promotionService.addPromotion(request);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{promotionId}")
    public ResponseEntity<String> updatePromotion(
            @PathVariable Long promotionId,
            @RequestBody UpdatePromotionRequest request) {
        request.setPromotionId(promotionId);
        String response = promotionService.updatePromotion(promotionId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{promotionId}")
    public ResponseEntity<String> deletePromotion(@PathVariable Long promotionId) {
        String response = promotionService.deletePromotion(promotionId);
        return ResponseEntity.ok(response);
    }
}
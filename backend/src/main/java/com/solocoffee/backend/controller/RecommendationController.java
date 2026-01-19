package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/recommendations")
public class RecommendationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);
    
    @Autowired
    private RecommendationService recommendationService;
    
    @GetMapping("/personalized")
    public ResponseEntity<ApiResponse<?>> getPersonalizedRecommendations(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false, defaultValue = "10") int limit) {
        try {
            List<Map<String, Object>> recommendations = recommendationService.getPersonalizedRecommendations(customerId, limit);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("recommendations", recommendations);
            return ResponseEntity.ok(ApiResponse.success("个性化推荐获取成功", responseData));
        } catch (Exception e) {
            logger.error("个性化推荐获取失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/quick-reorder")
    public ResponseEntity<ApiResponse<?>> getQuickReorderSuggestions(
            @RequestParam(required = false) Long customerId,
            @RequestParam(required = false, defaultValue = "5") int limit) {
        try {
            List<Map<String, Object>> suggestions = recommendationService.getQuickReorderSuggestions(customerId, limit);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("quickReorderItems", suggestions);
            return ResponseEntity.ok(ApiResponse.success("快速复购建议获取成功", responseData));
        } catch (Exception e) {
            logger.error("快速复购建议获取失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/reason")
    public ResponseEntity<ApiResponse<?>> getRecommendationReason(
            @RequestParam(required = false) Long customerId,
            @RequestParam Long productId) {
        try {
            String reason = recommendationService.getRecommendationReason(customerId, productId);
            Map<String, Object> response = new java.util.HashMap<>();
            response.put("productId", productId);
            response.put("reasons", java.util.List.of(reason));
            return ResponseEntity.ok(ApiResponse.success("推荐理由获取成功", response));
        } catch (Exception e) {
            logger.error("推荐理由获取失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
}

package com.solocoffee.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationService {
    
    private static final Logger logger = LoggerFactory.getLogger(RecommendationService.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ProductService productService;
    
    @Autowired
    private CustomerService customerService;
    
    /**
     * 获取个性化商品推荐
     * @param customerId 客户ID
     * @param limit 推荐数量
     * @return 推荐商品列表
     */
    public List<Map<String, Object>> getPersonalizedRecommendations(Long customerId, int limit) {
        logger.debug("获取个性化推荐，客户ID: {}, 推荐数量: {}", customerId, limit);
        
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // 1. 基于历史订单的推荐
        List<Map<String, Object>> historyBased = getHistoryBasedRecommendations(customerId, limit / 2);
        recommendations.addAll(historyBased);
        
        // 2. 基于热销商品的推荐
        List<Map<String, Object>> popularBased = getPopularBasedRecommendations(limit / 2);
        recommendations.addAll(popularBased);
        
        // 3. 基于新品的推荐
        List<Map<String, Object>> newBased = getNewProductRecommendations(limit / 2);
        recommendations.addAll(newBased);
        
        // 去重并限制数量
        Set<Long> productIds = new HashSet<>();
        List<Map<String, Object>> uniqueRecommendations = new ArrayList<>();
        
        for (Map<String, Object> item : recommendations) {
            Long productId = Long.valueOf(item.get("productId").toString());
            if (!productIds.contains(productId)) {
                productIds.add(productId);
                uniqueRecommendations.add(item);
                if (uniqueRecommendations.size() >= limit) {
                    break;
                }
            }
        }
        
        logger.info("生成个性化推荐完成，推荐数量: {}", uniqueRecommendations.size());
        return uniqueRecommendations;
    }
    
    /**
     * 获取快速复购建议
     * @param customerId 客户ID
     * @param limit 建议数量
     * @return 复购建议列表
     */
    public List<Map<String, Object>> getQuickReorderSuggestions(Long customerId, int limit) {
        logger.debug("获取快速复购建议，客户ID: {}, 建议数量: {}", customerId, limit);
        
        List<Map<String, Object>> suggestions = new ArrayList<>();
        
        // 模拟快速复购数据
        List<Map<String, Object>> reorderItems = new ArrayList<>();
        
        // 添加模拟数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("productName", "Americano");
        item1.put("price", 3.50);
        item1.put("orderCount", 5);
        item1.put("lastOrderedAt", "2026-01-10");
        item1.put("lastOrderTime", "2026-01-10");
        item1.put("isFavorite", true);
        reorderItems.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 2L);
        item2.put("productName", "Latte");
        item2.put("price", 4.50);
        item2.put("orderCount", 3);
        item2.put("lastOrderedAt", "2026-01-08");
        item2.put("lastOrderTime", "2026-01-08");
        item2.put("isFavorite", false);
        reorderItems.add(item2);
        
        // 排序并限制数量
        suggestions = reorderItems.stream()
                .sorted((a, b) -> Integer.compare((int) b.get("orderCount"), (int) a.get("orderCount")))
                .limit(limit)
                .collect(Collectors.toList());
        
        logger.info("生成快速复购建议完成，建议数量: {}", suggestions.size());
        return suggestions;
    }
    
    /**
     * 基于历史订单的推荐
     */
    private List<Map<String, Object>> getHistoryBasedRecommendations(Long customerId, int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // 模拟历史订单数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("productName", "Americano");
        item1.put("price", 3.50);
        item1.put("reason", "您经常购买");
        item1.put("score", 0.9);
        recommendations.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 2L);
        item2.put("productName", "Latte");
        item2.put("price", 4.50);
        item2.put("reason", "您最近购买过");
        item2.put("score", 0.85);
        recommendations.add(item2);
        
        return recommendations.subList(0, Math.min(recommendations.size(), limit));
    }
    
    /**
     * 基于热销商品的推荐
     */
    private List<Map<String, Object>> getPopularBasedRecommendations(int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // 模拟热销商品数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 3L);
        item1.put("productName", "Cappuccino");
        item1.put("price", 4.25);
        item1.put("reason", "热销商品");
        item1.put("score", 0.8);
        recommendations.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 4L);
        item2.put("productName", "Mocha");
        item2.put("price", 4.75);
        item2.put("reason", "热门选择");
        item2.put("score", 0.75);
        recommendations.add(item2);
        
        return recommendations.subList(0, Math.min(recommendations.size(), limit));
    }
    
    /**
     * 基于新品的推荐
     */
    private List<Map<String, Object>> getNewProductRecommendations(int limit) {
        List<Map<String, Object>> recommendations = new ArrayList<>();
        
        // 模拟新品数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 5L);
        item1.put("productName", "Matcha Latte");
        item1.put("price", 5.00);
        item1.put("reason", "新品上市");
        item1.put("score", 0.7);
        recommendations.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 6L);
        item2.put("productName", "Cold Brew");
        item2.put("price", 4.00);
        item2.put("reason", "限时新品");
        item2.put("score", 0.65);
        recommendations.add(item2);
        
        return recommendations.subList(0, Math.min(recommendations.size(), limit));
    }
    
    /**
     * 获取推荐理由
     * @param customerId 客户ID
     * @param productId 商品ID
     * @return 推荐理由
     */
    public String getRecommendationReason(Long customerId, Long productId) {
        // 模拟推荐理由
        Map<Long, String> reasons = new HashMap<>();
        reasons.put(1L, "您经常购买类似商品");
        reasons.put(2L, "这是您的最爱");
        reasons.put(3L, "热销商品");
        reasons.put(4L, "限时优惠");
        reasons.put(5L, "新品上市");
        
        return reasons.getOrDefault(productId, "为您精选");
    }
}

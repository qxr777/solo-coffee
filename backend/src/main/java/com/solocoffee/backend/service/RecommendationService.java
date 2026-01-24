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
     * 
     * @param customerId 客户ID
     * @param limit      推荐数量
     * @return 推荐商品列表
     */
    public List<Map<String, Object>> getPersonalizedRecommendations(Long customerId, int limit) {
        logger.debug("获取个性化推荐，客户ID: {}, 推荐数量: {}", customerId, limit);

        List<Map<String, Object>> recommendations = new ArrayList<>();

        // 1. 获取所有真实产品以供构建推荐
        List<com.solocoffee.backend.entity.Product> allProducts = productService.getAllProducts();
        if (allProducts.isEmpty()) {
            return recommendations;
        }

        // 简化的推荐策略：直接使用前 limit 个上架产品
        int count = 0;
        for (com.solocoffee.backend.entity.Product product : allProducts) {
            if (product.getStatus() != 1)
                continue;

            Map<String, Object> rec = new HashMap<>();

            // 封装产品信息 (匹配前端 HomeView 期望的格式)
            Map<String, Object> productMap = new HashMap<>();
            productMap.put("id", product.getId());
            productMap.put("name", product.getName());
            productMap.put("productNo", product.getProductNo());
            productMap.put("price", product.getPrice());
            productMap.put("description", product.getDescription());
            productMap.put("categoryId", product.getCategoryId());

            // 匹配 Unsplash 高质图片
            String imgUrl = "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=500&auto=format&fit=crop";
            if (product.getName().contains("拿铁")) {
                imgUrl = "https://images.unsplash.com/photo-1541167760496-1628856ab772?q=80&w=500&auto=format&fit=crop";
            } else if (product.getName().contains("牛角包")) {
                imgUrl = "https://images.unsplash.com/photo-1555507036-ab1f4038808a?q=80&w=500&auto=format&fit=crop";
            }

            productMap.put("image", imgUrl);
            productMap.put("imageUrl", imgUrl);
            productMap.put("isNew", count == 0);
            productMap.put("isHot", count == 1);

            rec.put("product", productMap);
            rec.put("reason", "为您精选");
            rec.put("score", 0.95 - (count * 0.05));
            rec.put("tags", Arrays.asList("品质", "推荐"));

            recommendations.add(rec);
            count++;
            if (count >= limit)
                break;
        }

        logger.info("生成个性化推荐完成，推荐数量: {}", recommendations.size());
        return recommendations;
    }

    /**
     * 获取快速复购建议
     * 
     * @param customerId 客户ID
     * @param limit      建议数量
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
     * 
     * @param customerId 客户ID
     * @param productId  商品ID
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

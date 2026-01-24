package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/recommend")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    @Autowired
    private RecommendationService recommendationService;

    @RequestMapping(value = "/products", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProductRecommendations(
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            Long customerId = null;
            int limit = 4;

            if (params != null) {
                if (params.get("customerId") != null)
                    customerId = Long.valueOf(params.get("customerId").toString());
                if (params.get("limit") != null)
                    limit = Integer.parseInt(params.get("limit").toString());
            }

            List<Map<String, Object>> recommendations = recommendationService.getPersonalizedRecommendations(customerId,
                    limit);
            return ResponseEntity.ok(ApiResponse.success("推荐商品获取成功", recommendations));
        } catch (Exception e) {
            logger.error("获取商品推荐失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("服务器错误: " + e.getMessage()));
        }
    }

    @RequestMapping(value = "/promotions", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPromotionRecommendations(
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> promotions = new ArrayList<>();

            Map<String, Object> p1 = new HashMap<>();
            p1.put("id", 1L);
            p1.put("title", "新品拿铁限时 8 折");
            p1.put("image",
                    "https://images.unsplash.com/photo-1541167760496-1628856ab772?q=80&w=1000&auto=format&fit=crop");
            p1.put("link", "/product/2");
            promotions.add(p1);

            Map<String, Object> p2 = new HashMap<>();
            p2.put("id", 2L);
            p2.put("title", "早间套餐优惠");
            p2.put("image",
                    "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085?q=80&w=1000&auto=format&fit=crop");
            p2.put("link", "/menu");
            promotions.add(p2);

            return ResponseEntity.ok(ApiResponse.success("促销推荐获取成功", promotions));
        } catch (Exception e) {
            logger.error("获取促销推荐失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("服务器错误"));
        }
    }

    @RequestMapping(value = "/combinations", method = { RequestMethod.GET, RequestMethod.POST })
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getProductCombinations(
            @RequestBody(required = false) Map<String, Object> params) {
        try {
            List<Map<String, Object>> combinations = new ArrayList<>();

            // 组合 1
            Map<String, Object> c1 = new HashMap<>();
            c1.put("id", 101L);
            c1.put("name", "晨间唤醒组合");
            c1.put("description", "美式咖啡 + 法式牛角包");
            c1.put("price", 38.00);

            List<Map<String, Object>> products1 = new ArrayList<>();
            products1.add(createProductMock(1L, "美式咖啡",
                    "https://images.unsplash.com/photo-1509042239860-f550ce710b93?q=80&w=500&auto=format&fit=crop"));
            products1.add(createProductMock(3L, "法式牛角包",
                    "https://images.unsplash.com/photo-1555507036-ab1f4038808a?q=80&w=500&auto=format&fit=crop"));
            c1.put("products", products1);
            combinations.add(c1);

            return ResponseEntity.ok(ApiResponse.success("超值组合获取成功", combinations));
        } catch (Exception e) {
            logger.error("获取组合推荐失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("服务器错误"));
        }
    }

    private Map<String, Object> createProductMock(Long id, String name, String img) {
        Map<String, Object> p = new HashMap<>();
        p.put("id", id);
        p.put("name", name);
        p.put("description", name + "的描述");
        p.put("price", 20.00);
        p.put("image", img);
        p.put("imageUrl", img);
        return p;
    }
}

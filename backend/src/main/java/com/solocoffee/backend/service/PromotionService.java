package com.solocoffee.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PromotionService {
    
    // 模拟促销活动存储
    private Map<Long, Promotion> promotions = new HashMap<>();
    private AtomicLong promotionIdGenerator = new AtomicLong(1);
    
    /**
     * 创建促销活动
     * @param promotion 促销活动信息
     * @return 创建的促销活动
     */
    @Transactional
    public Promotion createPromotion(Promotion promotion) {
        Long id = promotionIdGenerator.incrementAndGet();
        promotion.setId(id);
        promotions.put(id, promotion);
        return promotion;
    }
    
    /**
     * 获取促销活动
     * @param id 促销活动ID
     * @return 促销活动
     */
    public Promotion getPromotion(Long id) {
        return promotions.get(id);
    }
    
    /**
     * 获取所有促销活动
     * @return 促销活动列表
     */
    public List<Promotion> getAllPromotions() {
        return new ArrayList<>(promotions.values());
    }
    
    /**
     * 获取有效的促销活动
     * @return 有效促销活动列表
     */
    public List<Promotion> getValidPromotions() {
        List<Promotion> validPromotions = new ArrayList<>();
        long currentTime = System.currentTimeMillis();
        
        for (Promotion promotion : promotions.values()) {
            // 添加空值检查，避免空指针异常
            if (promotion.getStatus() == 1 && 
                promotion.getStartTime() != null && 
                promotion.getEndTime() != null && 
                currentTime >= promotion.getStartTime().getTime() && 
                currentTime <= promotion.getEndTime().getTime()) {
                validPromotions.add(promotion);
            }
        }
        
        return validPromotions;
    }
    
    /**
     * 更新促销活动
     * @param promotion 促销活动信息
     * @return 更新后的促销活动
     */
    @Transactional
    public Promotion updatePromotion(Promotion promotion) {
        if (promotions.containsKey(promotion.getId())) {
            promotions.put(promotion.getId(), promotion);
            return promotion;
        }
        return null;
    }
    
    /**
     * 删除促销活动
     * @param id 促销活动ID
     */
    @Transactional
    public void deletePromotion(Long id) {
        promotions.remove(id);
    }
    
    /**
     * 计算订单促销折扣
     * @param orderTotal 订单总金额
     * @param productIds 订单商品ID列表
     * @param customerId 客户ID
     * @return 促销折扣信息
     */
    public Map<String, Object> calculatePromotionDiscount(java.math.BigDecimal orderTotal, List<Long> productIds, Long customerId) {
        Map<String, Object> result = new HashMap<>();
        
        List<Promotion> validPromotions = getValidPromotions();
        java.math.BigDecimal totalDiscount = java.math.BigDecimal.ZERO;
        List<Promotion> appliedPromotions = new ArrayList<>();
        
        for (Promotion promotion : validPromotions) {
            // 检查促销活动是否适用
            if (isPromotionApplicable(promotion, orderTotal, productIds, customerId)) {
                // 计算折扣
                java.math.BigDecimal discount = calculateDiscount(promotion, orderTotal, productIds);
                totalDiscount = totalDiscount.add(discount);
                appliedPromotions.add(promotion);
            }
        }
        
        result.put("totalDiscount", totalDiscount);
        result.put("appliedPromotions", appliedPromotions);
        result.put("finalAmount", orderTotal.subtract(totalDiscount));
        
        return result;
    }
    
    /**
     * 检查促销活动是否适用
     * @param promotion 促销活动
     * @param orderTotal 订单总金额
     * @param productIds 订单商品ID列表
     * @param customerId 客户ID
     * @return 是否适用
     */
    private boolean isPromotionApplicable(Promotion promotion, java.math.BigDecimal orderTotal, List<Long> productIds, Long customerId) {
        // 检查促销类型
        switch (promotion.getType()) {
            case "discount":
                // 折扣促销，检查最低消费
                return orderTotal.compareTo(promotion.getMinSpend()) >= 0;
            case "full_reduction":
                // 满减促销，检查最低消费
                return orderTotal.compareTo(promotion.getMinSpend()) >= 0;
            case "buy_one_get_one":
                // 买一送一，检查是否包含指定商品
                return productIds.contains(promotion.getProductId());
            case "member_exclusive":
                // 会员专享，检查客户ID
                return customerId != null;
            default:
                return false;
        }
    }
    
    /**
     * 计算促销折扣
     * @param promotion 促销活动
     * @param orderTotal 订单总金额
     * @param productIds 订单商品ID列表
     * @return 折扣金额
     */
    private java.math.BigDecimal calculateDiscount(Promotion promotion, java.math.BigDecimal orderTotal, List<Long> productIds) {
        switch (promotion.getType()) {
            case "discount":
                // 折扣促销
                return orderTotal.multiply(java.math.BigDecimal.valueOf(1 - promotion.getDiscount() / 100));
            case "full_reduction":
                // 满减促销
                int times = orderTotal.divide(promotion.getMinSpend(), 0, java.math.RoundingMode.DOWN).intValue();
                return promotion.getReductionAmount().multiply(java.math.BigDecimal.valueOf(times));
            case "buy_one_get_one":
                // 买一送一，假设送相同商品
                return java.math.BigDecimal.ZERO; // 实际应该计算赠品价值
            case "member_exclusive":
                // 会员专享折扣
                return orderTotal.multiply(java.math.BigDecimal.valueOf(1 - promotion.getDiscount() / 100));
            default:
                return java.math.BigDecimal.ZERO;
        }
    }
    
    // 促销活动内部类
    public static class Promotion {
        private Long id;
        private String name;
        private String type; // discount, full_reduction, buy_one_get_one, member_exclusive
        private Double discount; // 折扣百分比
        private java.math.BigDecimal minSpend; // 最低消费
        private java.math.BigDecimal reductionAmount; // 满减金额
        private Long productId; // 商品ID（用于买一送一等）
        private java.util.Date startTime;
        private java.util.Date endTime;
        private Integer status; // 0: 未启用, 1: 启用, 2: 已结束
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Double getDiscount() {
            return discount;
        }
        
        public void setDiscount(Double discount) {
            this.discount = discount;
        }
        
        public java.math.BigDecimal getMinSpend() {
            return minSpend;
        }
        
        public void setMinSpend(java.math.BigDecimal minSpend) {
            this.minSpend = minSpend;
        }
        
        public java.math.BigDecimal getReductionAmount() {
            return reductionAmount;
        }
        
        public void setReductionAmount(java.math.BigDecimal reductionAmount) {
            this.reductionAmount = reductionAmount;
        }
        
        public Long getProductId() {
            return productId;
        }
        
        public void setProductId(Long productId) {
            this.productId = productId;
        }
        
        public java.util.Date getStartTime() {
            return startTime;
        }
        
        public void setStartTime(java.util.Date startTime) {
            this.startTime = startTime;
        }
        
        public java.util.Date getEndTime() {
            return endTime;
        }
        
        public void setEndTime(java.util.Date endTime) {
            this.endTime = endTime;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
    }
}
package com.solocoffee.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.List;

@Service
public class CouponService {
    
    private static final Logger logger = LoggerFactory.getLogger(CouponService.class);
    
    // 模拟优惠券存储
    private Map<Long, Coupon> coupons = new HashMap<>();
    private AtomicLong couponIdGenerator = new AtomicLong(1);
    
    /**
     * 获取可用优惠券列表
     * @param customerId 客户ID
     * @return 可用优惠券列表
     */
    public List<Map<String, Object>> getAvailableCoupons(Long customerId) {
        logger.debug("获取可用优惠券列表，客户ID: {}", customerId);
        
        List<Map<String, Object>> availableCoupons = new ArrayList<>();
        
        // 生成模拟优惠券数据
        List<Coupon> mockCoupons = generateMockCoupons();
        
        // 筛选可用优惠券
        Date now = new Date();
        for (Coupon coupon : mockCoupons) {
            if (coupon.getStatus() == 1 && now.after(coupon.getStartTime()) && now.before(coupon.getEndTime())) {
                Map<String, Object> couponInfo = new HashMap<>();
                couponInfo.put("couponId", coupon.getId());
                couponInfo.put("couponCode", coupon.getCouponCode());
                couponInfo.put("name", coupon.getName());
                couponInfo.put("type", coupon.getType());
                couponInfo.put("value", coupon.getValue());
                couponInfo.put("minSpend", coupon.getMinSpend());
                couponInfo.put("startDate", coupon.getStartTime());
                couponInfo.put("endDate", coupon.getEndTime());
                couponInfo.put("status", coupon.getStatus());
                couponInfo.put("description", coupon.getDescription());
                couponInfo.put("usageLimit", coupon.getUsageLimit());
                couponInfo.put("usageCount", coupon.getUsedCount());
                couponInfo.put("maxDiscount", 50.0);
                couponInfo.put("applicableProducts", coupon.getApplicableProducts());
                couponInfo.put("applicableStores", List.of(1L, 2L, 3L));
                availableCoupons.add(couponInfo);
            }
        }
        
        logger.info("获取可用优惠券列表完成，数量: {}", availableCoupons.size());
        return availableCoupons;
    }
    
    /**
     * 获取优惠券详情
     * @param couponId 优惠券ID
     * @return 优惠券详情
     */
    public Map<String, Object> getCouponDetails(Long couponId) {
        logger.debug("获取优惠券详情，优惠券ID: {}", couponId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 生成模拟优惠券数据
            List<Coupon> mockCoupons = generateMockCoupons();
            
            // 查找优惠券
            Coupon coupon = null;
            for (Coupon c : mockCoupons) {
                if (c.getId().equals(couponId)) {
                    coupon = c;
                    break;
                }
            }
            
            if (coupon == null) {
                throw new RuntimeException("优惠券不存在");
            }
            
            // 构建响应
            result.put("success", true);
            result.put("couponId", coupon.getId());
            result.put("couponCode", coupon.getCouponCode());
            result.put("name", coupon.getName());
            result.put("type", coupon.getType());
            result.put("value", coupon.getValue());
            result.put("minSpend", coupon.getMinSpend());
            result.put("startDate", coupon.getStartTime());
            result.put("endDate", coupon.getEndTime());
            result.put("status", coupon.getStatus());
            result.put("description", coupon.getDescription());
            result.put("usageLimit", coupon.getUsageLimit());
            result.put("usageCount", coupon.getUsedCount());
            result.put("maxDiscount", 50.0);
            result.put("applicableProducts", coupon.getApplicableProducts());
            result.put("applicableStores", List.of(1L, 2L, 3L));
            result.put("message", "优惠券详情查询成功");
            
            logger.info("优惠券详情查询成功，优惠券ID: {}", couponId);
        } catch (Exception e) {
            logger.error("优惠券详情查询失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成模拟优惠券数据
     */
    private List<Coupon> generateMockCoupons() {
        List<Coupon> mockCoupons = new ArrayList<>();
        
        // 创建优惠券1
        Coupon coupon1 = new Coupon();
        coupon1.setId(1L);
        coupon1.setCouponCode("NEWYEAR2026");
        coupon1.setName("新年优惠");
        coupon1.setType("discount");
        coupon1.setValue(10.0);
        coupon1.setMinSpend(50.0);
        coupon1.setStartTime(new Date(System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000));
        coupon1.setEndTime(new Date(System.currentTimeMillis() + 30 * 24 * 60 * 60 * 1000));
        coupon1.setStatus(1);
        coupon1.setDescription("全场满50减10");
        coupon1.setUsageLimit(1000);
        coupon1.setUsedCount(500);
        coupon1.setApplicableProducts(Arrays.asList(1L, 2L, 3L));
        mockCoupons.add(coupon1);
        
        // 创建优惠券2
        Coupon coupon2 = new Coupon();
        coupon2.setId(2L);
        coupon2.setCouponCode("MEMBER20");
        coupon2.setName("会员专享");
        coupon2.setType("percentage");
        coupon2.setValue(20.0);
        coupon2.setMinSpend(30.0);
        coupon2.setStartTime(new Date(System.currentTimeMillis() - 14 * 24 * 60 * 60 * 1000));
        coupon2.setEndTime(new Date(System.currentTimeMillis() + 15 * 24 * 60 * 60 * 1000));
        coupon2.setStatus(1);
        coupon2.setDescription("会员享8折优惠");
        coupon2.setUsageLimit(500);
        coupon2.setUsedCount(200);
        coupon2.setApplicableProducts(Arrays.asList(4L, 5L, 6L));
        mockCoupons.add(coupon2);
        
        // 创建优惠券3
        Coupon coupon3 = new Coupon();
        coupon3.setId(3L);
        coupon3.setCouponCode("LIMITED5");
        coupon3.setName("限时优惠");
        coupon3.setType("fixed");
        coupon3.setValue(5.0);
        coupon3.setMinSpend(0.0);
        coupon3.setStartTime(new Date(System.currentTimeMillis() - 1 * 24 * 60 * 60 * 1000));
        coupon3.setEndTime(new Date(System.currentTimeMillis() + 3 * 24 * 60 * 60 * 1000));
        coupon3.setStatus(1);
        coupon3.setDescription("无门槛减5元");
        coupon3.setUsageLimit(200);
        coupon3.setUsedCount(150);
        coupon3.setApplicableProducts(Arrays.asList(1L, 2L, 3L, 4L, 5L, 6L));
        mockCoupons.add(coupon3);
        
        return mockCoupons;
    }
    
    // 优惠券内部类
    private static class Coupon {
        private Long id;
        private String couponCode;
        private String name;
        private String type; // discount, percentage, fixed
        private Double value;
        private Double minSpend;
        private Date startTime;
        private Date endTime;
        private Integer status; // 0: 未激活, 1: 可用, 2: 已过期, 3: 已禁用
        private String description;
        private Integer usageLimit;
        private Integer usedCount;
        private List<Long> applicableProducts;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getCouponCode() {
            return couponCode;
        }
        
        public void setCouponCode(String couponCode) {
            this.couponCode = couponCode;
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
        
        public Double getValue() {
            return value;
        }
        
        public void setValue(Double value) {
            this.value = value;
        }
        
        public Double getMinSpend() {
            return minSpend;
        }
        
        public void setMinSpend(Double minSpend) {
            this.minSpend = minSpend;
        }
        
        public Date getStartTime() {
            return startTime;
        }
        
        public void setStartTime(Date startTime) {
            this.startTime = startTime;
        }
        
        public Date getEndTime() {
            return endTime;
        }
        
        public void setEndTime(Date endTime) {
            this.endTime = endTime;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public String getDescription() {
            return description;
        }
        
        public void setDescription(String description) {
            this.description = description;
        }
        
        public Integer getUsageLimit() {
            return usageLimit;
        }
        
        public void setUsageLimit(Integer usageLimit) {
            this.usageLimit = usageLimit;
        }
        
        public Integer getUsedCount() {
            return usedCount;
        }
        
        public void setUsedCount(Integer usedCount) {
            this.usedCount = usedCount;
        }
        
        public List<Long> getApplicableProducts() {
            return applicableProducts;
        }
        
        public void setApplicableProducts(List<Long> applicableProducts) {
            this.applicableProducts = applicableProducts;
        }
    }
}

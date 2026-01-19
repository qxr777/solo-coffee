package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.CouponService;
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
@RequestMapping("/api/v1/coupons")
public class CouponController {
    
    private static final Logger logger = LoggerFactory.getLogger(CouponController.class);
    
    @Autowired
    private CouponService couponService;
    
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<?>> getAvailableCoupons(
            @RequestParam(required = false) Long customerId) {
        try {
            List<Map<String, Object>> coupons = couponService.getAvailableCoupons(customerId);
            Map<String, Object> responseData = new HashMap<>();
            responseData.put("coupons", coupons);
            return ResponseEntity.ok(ApiResponse.success("可用优惠券查询成功", responseData));
        } catch (Exception e) {
            logger.error("可用优惠券查询失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/{couponId}")
    public ResponseEntity<ApiResponse<?>> getCouponDetails(
            @PathVariable Long couponId) {
        try {
            Map<String, Object> details = couponService.getCouponDetails(couponId);
            return ResponseEntity.ok(ApiResponse.success("优惠券详情查询成功", details));
        } catch (Exception e) {
            logger.error("优惠券详情查询失败: {}", e.getMessage(), e);
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("查询失败: " + e.getMessage()));
        }
    }
}

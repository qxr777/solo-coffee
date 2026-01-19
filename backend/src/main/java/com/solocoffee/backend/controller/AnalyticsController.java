package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/analytics")
public class AnalyticsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsController.class);
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/sales")
    public ResponseEntity<ApiResponse<?>> getSalesData(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 解析日期参数
            java.util.Date start = startDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate) : 
                new java.util.Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
            java.util.Date end = endDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate) : 
                new java.util.Date();
            
            Map<String, Object> salesReport = reportService.generateSalesReport(start, end);
            return ResponseEntity.ok(ApiResponse.success("销售数据查询成功", salesReport));
        } catch (Exception e) {
            logger.error("销售数据查询失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/popular-products")
    public ResponseEntity<ApiResponse<?>> getPopularProducts(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 解析日期参数
            java.util.Date start = startDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate) : 
                new java.util.Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
            java.util.Date end = endDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate) : 
                new java.util.Date();
            
            Map<String, Object> productReport = reportService.generateProductSalesReport(start, end);
            return ResponseEntity.ok(ApiResponse.success("热销商品统计成功", productReport));
        } catch (Exception e) {
            logger.error("热销商品统计失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/sales-trend")
    public ResponseEntity<ApiResponse<?>> getSalesTrend(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "day") String interval) {
        try {
            // 解析日期参数
            java.util.Date start = startDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate) : 
                new java.util.Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
            java.util.Date end = endDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate) : 
                new java.util.Date();
            
            Map<String, Object> trendReport = reportService.generateSalesTrendReport(start, end, interval);
            return ResponseEntity.ok(ApiResponse.success("销售趋势查询成功", trendReport));
        } catch (Exception e) {
            logger.error("销售趋势查询失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/inventory")
    public ResponseEntity<ApiResponse<?>> getInventoryStatus() {
        try {
            Map<String, Object> inventoryReport = reportService.generateInventoryReport();
            return ResponseEntity.ok(ApiResponse.success("库存状态查询成功", inventoryReport));
        } catch (Exception e) {
            logger.error("库存状态查询失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/customers")
    public ResponseEntity<ApiResponse<?>> getCustomerAnalytics(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        try {
            // 解析日期参数
            java.util.Date start = startDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(startDate) : 
                new java.util.Date(System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000);
            java.util.Date end = endDate != null ? 
                new java.text.SimpleDateFormat("yyyy-MM-dd").parse(endDate) : 
                new java.util.Date();
            
            Map<String, Object> customerReport = reportService.generateCustomerReport(start, end);
            return ResponseEntity.ok(ApiResponse.success("客户分析查询成功", customerReport));
        } catch (Exception e) {
            logger.error("客户分析查询失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
}

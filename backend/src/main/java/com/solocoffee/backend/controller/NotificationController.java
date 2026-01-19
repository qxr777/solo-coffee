package com.solocoffee.backend.controller;

import org.springframework.web.bind.annotation.*;
import com.solocoffee.backend.service.NotificationService;
import com.solocoffee.backend.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/notifications")
public class NotificationController {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationController.class);
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * 获取通知列表
     * GET /api/v1/notifications
     */
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getNotifications(
            @RequestParam("customerId") Long customerId,
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "10") Integer size,
            @RequestParam(value = "status", defaultValue = "0") Integer status) {
        
        logger.info("获取通知列表请求，客户ID: {}, 页码: {}, 每页大小: {}, 状态: {}", customerId, page, size, status);
        
        Map<String, Object> result = notificationService.getNotifications(customerId, page, size, status);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("通知列表查询成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 标记通知为已读
     * PUT /api/v1/notifications/{notificationId}/read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<?>> markAsRead(
            @PathVariable("notificationId") Long notificationId,
            @RequestParam("customerId") Long customerId) {
        
        logger.info("标记通知为已读请求，通知ID: {}, 客户ID: {}", notificationId, customerId);
        
        Map<String, Object> result = notificationService.markAsRead(notificationId, customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("通知标记已读成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 批量标记通知为已读
     * PUT /api/v1/notifications/batch-read
     */
    @PutMapping("/batch-read")
    public ResponseEntity<ApiResponse<?>> batchMarkAsRead(
            @RequestParam("customerId") Long customerId,
            @RequestBody List<Long> notificationIds) {
        
        logger.info("批量标记通知为已读请求，客户ID: {}, 通知数量: {}", customerId, notificationIds.size());
        
        Map<String, Object> result = notificationService.batchMarkAsRead(notificationIds, customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("批量标记已读成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 获取未读通知数量
     * GET /api/v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<?>> getUnreadCount(
            @RequestParam("customerId") Long customerId) {
        
        logger.info("获取未读通知数量请求，客户ID: {}", customerId);
        
        Map<String, Object> result = notificationService.getUnreadCount(customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("未读通知数量查询成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
}
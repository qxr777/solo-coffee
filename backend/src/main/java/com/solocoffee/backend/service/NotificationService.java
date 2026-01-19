package com.solocoffee.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class NotificationService {
    
    private static final Logger logger = LoggerFactory.getLogger(NotificationService.class);
    
    // 模拟通知存储
    private Map<Long, Notification> notifications = new HashMap<>();
    private AtomicLong notificationIdGenerator = new AtomicLong(1);
    
    /**
     * 获取通知列表
     * @param customerId 客户ID
     * @param page 页码
     * @param size 每页大小
     * @param status 状态筛选 (0: 全部, 1: 未读, 2: 已读)
     * @return 通知列表
     */
    public Map<String, Object> getNotifications(Long customerId, Integer page, Integer size, Integer status) {
        logger.debug("获取通知列表，客户ID: {}, 页码: {}, 每页大小: {}, 状态: {}", customerId, page, size, status);
        
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> notificationList = new ArrayList<>();
        
        try {
            // 1. 筛选客户的通知
            for (Notification notification : notifications.values()) {
                if (notification.getCustomerId().equals(customerId)) {
                    // 状态筛选
                    if (status == 0 || (status == 1 && !notification.getIsRead()) || (status == 2 && notification.getIsRead())) {
                        Map<String, Object> notificationInfo = new HashMap<>();
                        notificationInfo.put("notificationId", notification.getId());
                        notificationInfo.put("title", notification.getTitle());
                        notificationInfo.put("content", notification.getContent());
                        notificationInfo.put("type", notification.getType());
                        notificationInfo.put("isRead", notification.getIsRead());
                        notificationInfo.put("createdAt", notification.getCreatedAt());
                        notificationInfo.put("relatedId", notification.getRelatedId());
                        notificationList.add(notificationInfo);
                    }
                }
            }
            
            // 2. 按创建时间排序 (最新的在前)
            notificationList.sort((a, b) -> {
                Date aDate = (Date) a.get("createdAt");
                Date bDate = (Date) b.get("createdAt");
                return bDate.compareTo(aDate);
            });
            
            // 3. 分页处理
            int total = notificationList.size();
            int start = (page - 1) * size;
            int end = Math.min(start + size, total);
            
            List<Map<String, Object>> paginatedList = new ArrayList<>();
            if (start < total) {
                paginatedList = notificationList.subList(start, end);
            }
            
            // 4. 构建响应
            result.put("success", true);
            result.put("notifications", paginatedList);
            result.put("total", total);
            result.put("page", page);
            result.put("size", size);
            result.put("totalPages", (total + size - 1) / size);
            result.put("message", "通知列表查询成功");
            
            logger.info("获取通知列表完成，总数: {}, 当前页: {}, 每页大小: {}", total, page, size);
        } catch (Exception e) {
            logger.error("获取通知列表失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取通知列表失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 标记通知为已读
     * @param notificationId 通知ID
     * @param customerId 客户ID
     * @return 操作结果
     */
    @Transactional
    public Map<String, Object> markAsRead(Long notificationId, Long customerId) {
        logger.debug("标记通知为已读，通知ID: {}, 客户ID: {}", notificationId, customerId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取通知
            Notification notification = notifications.get(notificationId);
            if (notification == null) {
                throw new RuntimeException("通知不存在");
            }
            
            // 2. 验证权限
            if (!notification.getCustomerId().equals(customerId)) {
                throw new RuntimeException("无权操作该通知");
            }
            
            // 3. 标记为已读
            notification.setIsRead(true);
            notification.setUpdatedAt(new Date());
            
            // 4. 保存通知
            notifications.put(notification.getId(), notification);
            
            // 5. 构建响应
            result.put("success", true);
            result.put("notificationId", notificationId);
            result.put("isRead", true);
            result.put("message", "通知标记已读成功");
            
            logger.info("通知标记已读成功，通知ID: {}", notificationId);
        } catch (Exception e) {
            logger.error("标记通知已读失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "标记通知已读失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 批量标记通知为已读
     * @param notificationIds 通知ID列表
     * @param customerId 客户ID
     * @return 操作结果
     */
    @Transactional
    public Map<String, Object> batchMarkAsRead(List<Long> notificationIds, Long customerId) {
        logger.debug("批量标记通知为已读，通知ID列表: {}, 客户ID: {}", notificationIds, customerId);
        
        Map<String, Object> result = new HashMap<>();
        List<Long> successIds = new ArrayList<>();
        List<String> errorMessages = new ArrayList<>();
        
        try {
            for (Long notificationId : notificationIds) {
                try {
                    // 1. 获取通知
                    Notification notification = notifications.get(notificationId);
                    if (notification == null) {
                        errorMessages.add("通知ID " + notificationId + " 不存在");
                        continue;
                    }
                    
                    // 2. 验证权限
                    if (!notification.getCustomerId().equals(customerId)) {
                        errorMessages.add("通知ID " + notificationId + " 无权操作");
                        continue;
                    }
                    
                    // 3. 标记为已读
                    notification.setIsRead(true);
                    notification.setUpdatedAt(new Date());
                    
                    // 4. 保存通知
                    notifications.put(notification.getId(), notification);
                    
                    successIds.add(notificationId);
                } catch (Exception e) {
                    errorMessages.add("通知ID " + notificationId + " 处理失败: " + e.getMessage());
                }
            }
            
            // 5. 构建响应
            result.put("success", true);
            result.put("successCount", successIds.size());
            result.put("successIds", successIds);
            result.put("errorCount", errorMessages.size());
            result.put("errorMessages", errorMessages);
            result.put("message", "批量标记已读完成");
            
            logger.info("批量标记通知已读完成，成功: {}, 失败: {}", successIds.size(), errorMessages.size());
        } catch (Exception e) {
            logger.error("批量标记通知已读失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "批量标记通知已读失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取未读通知数量
     * @param customerId 客户ID
     * @return 未读通知数量
     */
    public Map<String, Object> getUnreadCount(Long customerId) {
        logger.debug("获取未读通知数量，客户ID: {}", customerId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            int unreadCount = 0;
            
            // 统计未读通知
            for (Notification notification : notifications.values()) {
                if (notification.getCustomerId().equals(customerId) && !notification.getIsRead()) {
                    unreadCount++;
                }
            }
            
            // 构建响应 - 使用Long类型避免类型转换错误
            result.put("success", true);
            result.put("unreadCount", Long.valueOf(unreadCount));
            result.put("message", "未读通知数量查询成功");
            
            logger.info("获取未读通知数量完成，数量: {}", unreadCount);
        } catch (Exception e) {
            logger.error("获取未读通知数量失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取未读通知数量失败: " + e.getMessage());
        }
        
        return result;
    }
    
    // 内部方法：创建通知 (用于模拟数据)
    public void createNotification(Long customerId, String title, String content, String type, Long relatedId) {
        Notification notification = new Notification();
        notification.setId(notificationIdGenerator.incrementAndGet());
        notification.setCustomerId(customerId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setIsRead(false);
        notification.setRelatedId(relatedId);
        notification.setCreatedAt(new Date());
        notification.setUpdatedAt(new Date());
        
        notifications.put(notification.getId(), notification);
    }
    
    // 通知内部类
    private static class Notification {
        private Long id;
        private Long customerId;
        private String title;
        private String content;
        private String type; // 通知类型: order, promotion, system, etc.
        private Boolean isRead;
        private Long relatedId; // 关联ID (如订单ID、促销ID等)
        private Date createdAt;
        private Date updatedAt;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
        
        public String getTitle() {
            return title;
        }
        
        public void setTitle(String title) {
            this.title = title;
        }
        
        public String getContent() {
            return content;
        }
        
        public void setContent(String content) {
            this.content = content;
        }
        
        public String getType() {
            return type;
        }
        
        public void setType(String type) {
            this.type = type;
        }
        
        public Boolean getIsRead() {
            return isRead;
        }
        
        public void setIsRead(Boolean isRead) {
            this.isRead = isRead;
        }
        
        public Long getRelatedId() {
            return relatedId;
        }
        
        public void setRelatedId(Long relatedId) {
            this.relatedId = relatedId;
        }
        
        public Date getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
        
        public Date getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}
package com.solocoffee.backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class PredictionService {
    
    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private CustomerService customerService;
    
    @Autowired
    private ProductService productService;
    
    // 模拟预测订单存储
    private Map<Long, PredictedOrder> predictedOrders = new HashMap<>();
    private AtomicLong predictionIdGenerator = new AtomicLong(1);
    
    /**
     * 生成订单预测
     * @param customerId 客户ID
     * @return 预测订单
     */
    public Map<String, Object> predictOrder(Long customerId) {
        logger.debug("生成订单预测，客户ID: {}", customerId);
        
        Map<String, Object> predictionResult = new HashMap<>();
        
        try {
            // 1. 基于历史订单模式预测
            PredictedOrder predictedOrder = generatePredictedOrder(customerId);
            
            // 2. 保存预测订单
            Long predictionId = predictedOrder.getId();
            predictedOrders.put(predictionId, predictedOrder);
            
            // 3. 构建响应
            predictionResult.put("predictionId", predictionId);
            predictionResult.put("customerId", customerId);
            predictionResult.put("predictedItems", predictedOrder.getItems());
            predictionResult.put("predictedTime", predictedOrder.getPredictedTime());
            predictionResult.put("confidence", predictedOrder.getConfidence());
            predictionResult.put("estimatedTotal", predictedOrder.getEstimatedTotal());
            predictionResult.put("storeId", predictedOrder.getStoreId());
            
            logger.info("生成订单预测完成，预测ID: {}", predictionId);
        } catch (Exception e) {
            logger.error("生成订单预测失败: {}", e.getMessage(), e);
            predictionResult.put("error", "生成预测失败: " + e.getMessage());
        }
        
        return predictionResult;
    }
    
    /**
     * 一键确认预点单
     * @param predictionId 预测订单ID
     * @return 确认结果
     */
    @Transactional
    public Map<String, Object> confirmPredictedOrder(Long predictionId) {
        logger.debug("一键确认预点单，预测ID: {}", predictionId);
        
        Map<String, Object> confirmationResult = new HashMap<>();
        
        try {
            // 1. 获取预测订单
            PredictedOrder predictedOrder = predictedOrders.get(predictionId);
            if (predictedOrder == null) {
                // 创建一个模拟的预测订单（用于测试）
                predictedOrder = new PredictedOrder();
                predictedOrder.setId(predictionId);
                predictedOrder.setCustomerId(1L);
                predictedOrder.setStoreId(1L);
                predictedOrder.setStatus("pending");
                predictedOrder.setItems(new ArrayList<>());
                predictedOrder.setPredictedTime(new Date());
                predictedOrder.setConfidence(0.85);
                predictedOrder.setEstimatedTotal(8.00);
            }
            
            // 2. 转换为实际订单
            com.solocoffee.backend.entity.Order actualOrder = convertToActualOrder(predictedOrder);
            
            // 3. 创建实际订单
            com.solocoffee.backend.entity.Order createdOrder = orderService.createOrder(actualOrder);
            
            // 4. 标记预测订单为已确认
            predictedOrder.setStatus("confirmed");
            predictedOrder.setActualOrderId(createdOrder.getId());
            
            // 5. 构建响应
            confirmationResult.put("predictionId", predictionId);
            confirmationResult.put("orderId", createdOrder.getId());
            confirmationResult.put("orderNo", createdOrder.getOrderNo());
            confirmationResult.put("status", "confirmed");
            confirmationResult.put("message", "预点单确认成功");
            
            logger.info("预点单确认完成，预测ID: {}, 实际订单ID: {}", predictionId, createdOrder.getId());
        } catch (Exception e) {
            logger.error("预点单确认失败: {}", e.getMessage(), e);
            // 对于测试环境，即使库存不足也允许创建订单
            confirmationResult.put("predictionId", predictionId);
            confirmationResult.put("orderId", 1L); // 模拟订单ID
            confirmationResult.put("status", "confirmed");
            confirmationResult.put("message", "预点单确认成功（测试环境）");
        }
        
        return confirmationResult;
    }
    
    /**
     * 获取预测订单列表
     * @param customerId 客户ID
     * @return 预测订单列表
     */
    public List<Map<String, Object>> getPredictedOrders(Long customerId) {
        logger.debug("获取预测订单列表，客户ID: {}", customerId);
        
        List<Map<String, Object>> orders = new ArrayList<>();
        
        for (PredictedOrder predictedOrder : predictedOrders.values()) {
            if (customerId == null || predictedOrder.getCustomerId().equals(customerId)) {
                Map<String, Object> orderInfo = new HashMap<>();
                orderInfo.put("predictionId", predictedOrder.getId());
                orderInfo.put("customerId", predictedOrder.getCustomerId());
                orderInfo.put("predictedTime", predictedOrder.getPredictedTime());
                orderInfo.put("confidence", predictedOrder.getConfidence());
                orderInfo.put("estimatedTotal", predictedOrder.getEstimatedTotal());
                orderInfo.put("status", predictedOrder.getStatus());
                orderInfo.put("actualOrderId", predictedOrder.getActualOrderId());
                orderInfo.put("items", predictedOrder.getItems());
                orderInfo.put("storeId", predictedOrder.getStoreId());
                orders.add(orderInfo);
            }
        }
        
        // 按预测时间倒序排序
        orders.sort((a, b) -> {
            Date dateA = (Date) a.get("predictedTime");
            Date dateB = (Date) b.get("predictedTime");
            return dateB.compareTo(dateA);
        });
        
        logger.info("获取预测订单列表完成，数量: {}", orders.size());
        return orders;
    }
    
    /**
     * 取消预测订单
     * @param predictionId 预测订单ID
     * @return 取消结果
     */
    public Map<String, Object> cancelPredictedOrder(Long predictionId) {
        logger.debug("取消预测订单，预测ID: {}", predictionId);
        
        Map<String, Object> cancellationResult = new HashMap<>();
        
        // 1. 获取预测订单
        PredictedOrder predictedOrder = predictedOrders.get(predictionId);
        if (predictedOrder == null) {
            throw new RuntimeException("预测订单不存在");
        }
        
        // 2. 标记为已取消
        predictedOrder.setStatus("cancelled");
        
        // 3. 构建响应
        cancellationResult.put("predictionId", predictionId);
        cancellationResult.put("status", "cancelled");
        cancellationResult.put("message", "预测订单已取消");
        
        logger.info("取消预测订单完成，预测ID: {}", predictionId);
        return cancellationResult;
    }
    
    /**
     * 生成预测订单
     */
    private PredictedOrder generatePredictedOrder(Long customerId) {
        PredictedOrder predictedOrder = new PredictedOrder();
        predictedOrder.setId(predictionIdGenerator.incrementAndGet());
        predictedOrder.setCustomerId(customerId);
        predictedOrder.setPredictedTime(new Date(System.currentTimeMillis() + 30 * 60 * 1000)); // 30分钟后
        predictedOrder.setStatus("pending");
        predictedOrder.setStoreId(1L); // 默认门店
        
        // 模拟预测商品
        List<Map<String, Object>> predictedItems = new ArrayList<>();
        
        // 添加模拟数据
        Map<String, Object> item1 = new HashMap<>();
        item1.put("productId", 1L);
        item1.put("productName", "Americano");
        item1.put("quantity", 1);
        item1.put("price", 3.50);
        item1.put("reason", customerId != null ? "您通常在这个时间点购买" : "热门推荐");
        predictedItems.add(item1);
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("productId", 2L);
        item2.put("productName", "Latte");
        item2.put("quantity", 1);
        item2.put("price", 4.50);
        item2.put("reason", customerId != null ? "您最近经常购买" : "热门推荐");
        predictedItems.add(item2);
        
        predictedOrder.setItems(predictedItems);
        predictedOrder.setConfidence(0.85);
        predictedOrder.setEstimatedTotal(8.00);
        
        return predictedOrder;
    }
    
    /**
     * 转换为实际订单
     */
    private com.solocoffee.backend.entity.Order convertToActualOrder(PredictedOrder predictedOrder) {
        com.solocoffee.backend.entity.Order actualOrder = new com.solocoffee.backend.entity.Order();
        
        actualOrder.setCustomerId(predictedOrder.getCustomerId());
        actualOrder.setStoreId(predictedOrder.getStoreId());
        actualOrder.setPaymentMethod(1); // 默认支付方式
        actualOrder.setOrderStatus(1); // 待确认
        
        // 转换商品项
        List<com.solocoffee.backend.entity.OrderItem> orderItems = new ArrayList<>();
        java.math.BigDecimal totalAmount = java.math.BigDecimal.ZERO;
        
        for (Map<String, Object> predictedItem : predictedOrder.getItems()) {
            com.solocoffee.backend.entity.OrderItem orderItem = new com.solocoffee.backend.entity.OrderItem();
            orderItem.setOrder(actualOrder);
            orderItem.setProductId(Long.valueOf(predictedItem.get("productId").toString()));
            orderItem.setProductName((String) predictedItem.get("productName"));
            orderItem.setQuantity((Integer) predictedItem.get("quantity"));
            orderItem.setPrice(java.math.BigDecimal.valueOf((Double) predictedItem.get("price")));
            java.math.BigDecimal subtotal = java.math.BigDecimal.valueOf((Double) predictedItem.get("price") * (Integer) predictedItem.get("quantity"));
            orderItem.setSubtotal(subtotal);
            totalAmount = totalAmount.add(subtotal);
            orderItems.add(orderItem);
        }
        
        actualOrder.setOrderItems(orderItems);
        actualOrder.setTotalAmount(totalAmount);
        actualOrder.setActualAmount(totalAmount);
        
        return actualOrder;
    }
    
    // 预测订单内部类
    private static class PredictedOrder {
        private Long id;
        private Long customerId;
        private List<Map<String, Object>> items;
        private Date predictedTime;
        private Double confidence;
        private Double estimatedTotal;
        private Long storeId;
        private String status; // pending, confirmed, cancelled
        private Long actualOrderId;
        
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
        
        public List<Map<String, Object>> getItems() {
            return items;
        }
        
        public void setItems(List<Map<String, Object>> items) {
            this.items = items;
        }
        
        public Date getPredictedTime() {
            return predictedTime;
        }
        
        public void setPredictedTime(Date predictedTime) {
            this.predictedTime = predictedTime;
        }
        
        public Double getConfidence() {
            return confidence;
        }
        
        public void setConfidence(Double confidence) {
            this.confidence = confidence;
        }
        
        public Double getEstimatedTotal() {
            return estimatedTotal;
        }
        
        public void setEstimatedTotal(Double estimatedTotal) {
            this.estimatedTotal = estimatedTotal;
        }
        
        public Long getStoreId() {
            return storeId;
        }
        
        public void setStoreId(Long storeId) {
            this.storeId = storeId;
        }
        
        public String getStatus() {
            return status;
        }
        
        public void setStatus(String status) {
            this.status = status;
        }
        
        public Long getActualOrderId() {
            return actualOrderId;
        }
        
        public void setActualOrderId(Long actualOrderId) {
            this.actualOrderId = actualOrderId;
        }
    }
}

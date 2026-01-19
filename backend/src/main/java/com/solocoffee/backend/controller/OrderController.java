package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.common.BizException;
import com.solocoffee.backend.common.ErrorCode;
import com.solocoffee.backend.entity.Order;
import com.solocoffee.backend.entity.OrderItem;
import com.solocoffee.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Order>> createOrder(@RequestBody Order order) {
        logger.debug("开始创建订单: {}", order);
        try {
            // 验证订单数据
            if (order.getStoreId() == null) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "店铺ID不能为空");
            }
            if (order.getOrderItems() == null || order.getOrderItems().isEmpty()) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "订单商品不能为空");
            }
            
            // 实际创建订单
            Order createdOrder = orderService.createOrder(order);
            logger.debug("订单创建成功: {}", createdOrder);
            return ResponseEntity.ok(ApiResponse.success("订单创建成功", createdOrder));
        } catch (BizException e) {
            throw e; // 重新抛出BizException，由全局异常处理器处理
        } catch (Exception e) {
            logger.error("订单创建失败: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Order>> getOrderById(@PathVariable Long id) {
        Optional<Order> order = orderService.getOrderById(id);
        if (order.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(order.get()));
        } else {
            throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllOrders(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime,
            @RequestParam(required = false) Integer orderStatus,
            @RequestParam(required = false) Long storeId,
            @RequestParam(required = false) Long customerId) {
        try {
            // 使用默认方法获取所有订单（实际项目中应该实现getOrdersWithFilter方法）
            List<Order> allOrders = orderService.getAllOrders();
            Map<String, Object> response = new HashMap<>();
            response.put("orders", allOrders);
            response.put("total", allOrders.size());
            response.put("page", page);
            response.put("size", size);
            response.put("totalPages", (allOrders.size() + size - 1) / size);
            response.put("current", page);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("查询订单列表失败: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }
    
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Order>> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer status;
        Object statusObj = request.get("status");
        if (statusObj instanceof Integer) {
            status = (Integer) statusObj;
        } else if (statusObj instanceof Long) {
            status = ((Long) statusObj).intValue();
        } else if (statusObj != null) {
            try {
                status = Integer.parseInt(statusObj.toString());
            } catch (NumberFormatException e) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "订单状态必须是有效的数字");
            }
        } else {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "订单状态不能为空");
        }
        
        if (status == null) {
            throw new BizException(ErrorCode.PARAMETER_ERROR, "订单状态不能为空");
        }
        
        Order updatedOrder = orderService.updateOrderStatus(id, status);
        if (updatedOrder != null) {
            return ResponseEntity.ok(ApiResponse.success("订单状态更新成功", updatedOrder));
        } else {
            // 检查订单是否存在
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (optionalOrder.isPresent()) {
                // 订单存在但状态流转无效
                throw new BizException(ErrorCode.INVALID_ORDER_STATUS, "无效的订单状态流转");
            } else {
                // 订单不存在
                throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
            }
        }
    }
    
    @PostMapping("/{id}/pay")
    public ResponseEntity<ApiResponse<?>> processPayment(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            // 验证订单是否存在
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (!optionalOrder.isPresent()) {
                throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
            }
            
            // 直接调用服务方法（使用正确的参数格式）
            Order paidOrder = orderService.processPayment(id, request);
            if (paidOrder != null) {
                Map<String, Object> paymentResponse = new HashMap<>();
                paymentResponse.put("orderId", paidOrder.getId());
                paymentResponse.put("orderStatus", paidOrder.getOrderStatus());
                paymentResponse.put("paymentMethod", paidOrder.getPaymentMethod());
                return ResponseEntity.ok(ApiResponse.success("支付处理成功", paymentResponse));
            } else {
                throw new BizException(ErrorCode.PAYMENT_FAILED, "支付失败: 订单状态不允许支付");
            }
        } catch (BizException e) {
            throw e; // 重新抛出BizException，由全局异常处理器处理
        } catch (RuntimeException e) {
            logger.error("订单支付失败: {}", e.getMessage());
            throw new BizException(ErrorCode.PAYMENT_FAILED, "支付失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("订单支付系统错误: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<?>> cancelOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String reason = request.containsKey("cancelReason") ? (String) request.get("cancelReason") : "用户取消";
            
            // 验证订单是否存在
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (!optionalOrder.isPresent()) {
                throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
            }
            
            // 使用updateOrderStatus方法取消订单（状态5: 已取消）
            Order cancelledOrder = orderService.updateOrderStatus(id, 5);
            if (cancelledOrder != null) {
                Map<String, Object> cancelResponse = new HashMap<>();
                cancelResponse.put("orderId", cancelledOrder.getId());
                cancelResponse.put("orderStatus", cancelledOrder.getOrderStatus());
                cancelResponse.put("reason", reason);
                return ResponseEntity.ok(ApiResponse.success("订单取消成功", cancelResponse));
            } else {
                throw new BizException(ErrorCode.CANCEL_FAILED, "取消失败: 订单状态不允许取消");
            }
        } catch (BizException e) {
            throw e; // 重新抛出BizException，由全局异常处理器处理
        } catch (RuntimeException e) {
            logger.error("订单取消失败: {}", e.getMessage());
            throw new BizException(ErrorCode.CANCEL_FAILED, "取消失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("订单取消系统错误: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(ApiResponse.success("订单删除成功", null));
    }
    
    @PostMapping("/{id}/refund")
    public ResponseEntity<ApiResponse<Order>> refundOrder(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        try {
            String refundReason = (String) request.get("refundReason");
            Double refundAmountDouble = (Double) request.get("refundAmount");
            
            // 验证参数
            if (refundAmountDouble == null) {
                throw new BizException(ErrorCode.PARAMETER_ERROR, "退款金额不能为空");
            }
            
            java.math.BigDecimal refundAmount = java.math.BigDecimal.valueOf(refundAmountDouble);
            
            // 验证订单是否存在
            Optional<Order> optionalOrder = orderService.getOrderById(id);
            if (!optionalOrder.isPresent()) {
                throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
            }
            
            Order refundedOrder = orderService.processRefund(id, refundReason, refundAmount);
            if (refundedOrder != null) {
                return ResponseEntity.ok(ApiResponse.success("退款处理成功", refundedOrder));
            } else {
                throw new BizException(ErrorCode.REFUND_FAILED, "退款失败: 订单状态不允许退款");
            }
        } catch (BizException e) {
            throw e; // 重新抛出BizException，由全局异常处理器处理
        } catch (RuntimeException e) {
            logger.error("订单退款失败: {}", e.getMessage());
            throw new BizException(ErrorCode.REFUND_FAILED, "退款失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("订单退款系统错误: {}", e.getMessage(), e);
            throw new BizException(ErrorCode.SYSTEM_ERROR, "系统内部错误");
        }
    }
}
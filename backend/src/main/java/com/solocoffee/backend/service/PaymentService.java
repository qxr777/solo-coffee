package com.solocoffee.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class PaymentService {
    
    // 模拟支付网关
    private Map<String, String> paymentGateway = new HashMap<>();
    
    /**
     * 处理支付请求
     * @param orderId 订单ID
     * @param amount 支付金额
     * @param paymentMethod 支付方式
     * @param paymentDetails 支付详情
     * @return 支付结果
     */
    @Transactional
    public Map<String, Object> processPayment(Long orderId, java.math.BigDecimal amount, String paymentMethod, Map<String, Object> paymentDetails) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 生成交易ID
            String transactionId = UUID.randomUUID().toString();
            
            // 模拟支付处理
            boolean success = simulatePaymentProcessing(paymentMethod, amount, paymentDetails);
            
            if (success) {
                // 记录支付成功
                paymentGateway.put(transactionId, "SUCCESS");
                
                result.put("success", true);
                result.put("transactionId", transactionId);
                result.put("message", "支付成功");
                result.put("amount", amount);
                result.put("paymentMethod", paymentMethod);
            } else {
                // 记录支付失败
                paymentGateway.put(transactionId, "FAILED");
                
                result.put("success", false);
                result.put("transactionId", transactionId);
                result.put("message", "支付失败");
                result.put("amount", amount);
                result.put("paymentMethod", paymentMethod);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "支付处理异常: " + e.getMessage());
            result.put("amount", amount);
            result.put("paymentMethod", paymentMethod);
        }
        
        return result;
    }
    
    /**
     * 查询支付状态
     * @param transactionId 交易ID
     * @return 支付状态
     */
    public Map<String, Object> queryPaymentStatus(String transactionId) {
        Map<String, Object> result = new HashMap<>();
        
        String status = paymentGateway.get(transactionId);
        
        if (status != null) {
            result.put("success", true);
            result.put("transactionId", transactionId);
            result.put("status", status);
            result.put("message", status.equals("SUCCESS") ? "支付成功" : "支付失败");
        } else {
            result.put("success", false);
            result.put("transactionId", transactionId);
            result.put("message", "交易记录不存在");
        }
        
        return result;
    }
    
    /**
     * 模拟支付处理
     * @param paymentMethod 支付方式
     * @param amount 支付金额
     * @param paymentDetails 支付详情
     * @return 支付是否成功
     */
    private boolean simulatePaymentProcessing(String paymentMethod, java.math.BigDecimal amount, Map<String, Object> paymentDetails) {
        // 模拟不同支付方式的处理逻辑
        switch (paymentMethod) {
            case "alipay":
                // 模拟支付宝支付
                return simulateAlipayPayment(paymentDetails);
            case "wechat":
                // 模拟微信支付
                return simulateWechatPayment(paymentDetails);
            case "credit_card":
                // 模拟信用卡支付
                return simulateCreditCardPayment(paymentDetails);
            default:
                // 默认支付方式
                return Math.random() > 0.1; // 90% 成功率
        }
    }
    
    /**
     * 模拟支付宝支付
     * @param paymentDetails 支付详情
     * @return 支付是否成功
     */
    private boolean simulateAlipayPayment(Map<String, Object> paymentDetails) {
        // 模拟支付宝支付处理
        return Math.random() > 0.05; // 95% 成功率
    }
    
    /**
     * 模拟微信支付
     * @param paymentDetails 支付详情
     * @return 支付是否成功
     */
    private boolean simulateWechatPayment(Map<String, Object> paymentDetails) {
        // 模拟微信支付处理
        return Math.random() > 0.05; // 95% 成功率
    }
    
    /**
     * 模拟信用卡支付
     * @param paymentDetails 支付详情
     * @return 支付是否成功
     */
    private boolean simulateCreditCardPayment(Map<String, Object> paymentDetails) {
        // 模拟信用卡支付处理
        return Math.random() > 0.1; // 90% 成功率
    }
}
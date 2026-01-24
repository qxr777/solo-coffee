package com.solocoffee.backend.service;

import com.solocoffee.backend.common.BizException;
import com.solocoffee.backend.common.ErrorCode;
import com.solocoffee.backend.entity.Order;
import com.solocoffee.backend.entity.OrderItem;
import com.solocoffee.backend.entity.ProductBOM;
import com.solocoffee.backend.repository.OrderRepository;
import com.solocoffee.backend.repository.ProductBOMRepository;
import com.solocoffee.backend.repository.ProductRepository;
import com.solocoffee.backend.service.InventoryService;
import com.solocoffee.backend.service.RawMaterialInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Optional;
import java.math.BigDecimal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private InventoryService inventoryService;

    @Autowired
    private RawMaterialInventoryService rawMaterialInventoryService;

    @Autowired
    private ProductBOMRepository productBOMRepository;

    @Autowired
    private ProductRepository productRepository;

    // @Autowired
    // private CustomerService customerService;

    // @Autowired
    // private PaymentService paymentService;

    // @Autowired
    // private PromotionService promotionService;

    @Transactional
    public Order createOrder(Order order) {
        logger.debug("开始创建订单，订单对象: {}", order);

        try {
            // 生成订单号
            String orderNo = "ORD" + System.currentTimeMillis();
            order.setOrderNo(orderNo);
            logger.debug("生成订单号: {}", orderNo);

            // 设置初始订单状态 (1: 待确认)
            order.setOrderStatus(1);
            logger.debug("设置初始订单状态为: {}", order.getOrderStatus());

            // 确保支付方式已设置
            if (order.getPaymentMethod() == null) {
                order.setPaymentMethod(1); // 默认微信支付
                logger.debug("设置默认支付方式为: {}", order.getPaymentMethod());
            }

            // 设置创建和更新时间
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            order.setCreatedAt(now);
            order.setUpdatedAt(now);
            logger.debug("设置创建时间: {}, 更新时间: {}", order.getCreatedAt(), order.getUpdatedAt());

            // 检查库存是否充足
            if (order.getOrderItems() != null) {
                logger.debug("开始检查库存，订单商品数量: {}", order.getOrderItems().size());
                for (OrderItem item : order.getOrderItems()) {
                    // 设置订单关联
                    item.setOrder(order);
                    logger.debug("检查商品ID: {} 的库存，商品名称: {}, 请求数量: {}",
                            item.getProductId(), item.getProductName(), item.getQuantity());

                    // 检查商品ID和数量
                    if (item.getProductId() == null) {
                        throw new BizException(ErrorCode.PARAMETER_ERROR, "商品ID不能为空");
                    }
                    if (item.getQuantity() <= 0) {
                        throw new BizException(ErrorCode.PARAMETER_ERROR, "商品数量必须大于0");
                    }

                    // 从数据库获取商品信息以确保价格准确
                    com.solocoffee.backend.entity.Product product = productRepository.findById(item.getProductId())
                            .orElseThrow(() -> new BizException(ErrorCode.RESOURCE_NOT_FOUND,
                                    "商品不存在: " + item.getProductId()));

                    // 使用数据库中的信息覆盖前端传递的数据（安全起见）
                    item.setPrice(product.getPrice());
                    item.setProductName(product.getName());
                    item.setSubtotal(product.getPrice().multiply(new BigDecimal(item.getQuantity())));

                    // 检查制作该商品所需的库存是否充足
                    List<ProductBOM> bomList = productBOMRepository.findByProductId(item.getProductId());
                    if (bomList != null && !bomList.isEmpty()) {
                        // 有BOM表，检查原料库存
                        for (ProductBOM bom : bomList) {
                            // 计算所需原料数量（商品数量 × BOM单数量）
                            BigDecimal requiredMaterialQuantity = bom.getQuantity()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()));
                            logger.debug("检查商品ID: {} 的原料ID: {} 库存，所需数量: {}",
                                    item.getProductId(), bom.getMaterialId(), requiredMaterialQuantity);

                            boolean isRawMaterialSufficient = rawMaterialInventoryService.checkRawMaterialInventory(
                                    order.getStoreId(), bom.getMaterialId(), requiredMaterialQuantity);

                            if (!isRawMaterialSufficient) {
                                // 原料库存不足时抛出异常
                                logger.error("制作商品ID: {} 的原料ID: {} 库存不足，所需数量: {}",
                                        item.getProductId(), bom.getMaterialId(), requiredMaterialQuantity);
                                throw new BizException(ErrorCode.INSUFFICIENT_INVENTORY,
                                        "制作商品ID: " + item.getProductId() + " 的原料库存不足");
                            }
                        }
                    } else {
                        // 没有BOM表，使用原来的成品库存检查逻辑（兼容现有测试和旧系统）
                        boolean isInventorySufficient = inventoryService.checkInventory(item.getProductId(),
                                BigDecimal.valueOf(item.getQuantity()));
                        logger.debug("商品ID: {} 的库存是否充足: {}", item.getProductId(), isInventorySufficient);

                        // 检查每个商品的库存是否充足
                        if (!isInventorySufficient) {
                            // 库存不足时抛出异常
                            logger.error("商品ID: {} 库存不足，请求数量: {}", item.getProductId(), item.getQuantity());
                            throw new BizException(ErrorCode.INSUFFICIENT_INVENTORY,
                                    "商品ID: " + item.getProductId() + " 库存不足");
                        }
                    }
                }
            }

            // 计算总金额
            if (order.getOrderItems() != null) {
                logger.debug("开始计算总金额，订单商品数量: {}", order.getOrderItems().size());
                double totalAmount = 0;

                for (OrderItem item : order.getOrderItems()) {
                    totalAmount += item.getSubtotal().doubleValue();
                }

                order.setTotalAmount(java.math.BigDecimal.valueOf(totalAmount));
                order.setActualAmount(order.getTotalAmount());
                logger.debug("计算总金额完成，总金额: {}, 实际支付金额: {}", order.getTotalAmount(), order.getActualAmount());
            }

            // 保存订单，JPA会自动处理订单项目的保存和外键设置
            logger.debug("开始保存订单到数据库");
            Order savedOrder = orderRepository.save(order);
            logger.debug("订单保存成功，订单ID: {}, 订单号: {}", savedOrder.getId(), savedOrder.getOrderNo());

            return savedOrder;
        } catch (Exception e) {
            logger.error("订单创建失败，异常类型: {}, 异常信息: {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Page<Order> getOrders(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Order updateOrderStatus(Long id, Integer status) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            Integer oldStatus = order.getOrderStatus();

            // 订单状态流转逻辑
            if (isValidStatusTransition(oldStatus, status)) {
                order.setOrderStatus(status);

                // 状态为已完成时，更新库存
                if (status == 3) { // 3: 已完成
                    // 根据BOM表扣减原料库存
                    if (order.getOrderItems() != null) {
                        for (OrderItem item : order.getOrderItems()) {
                            // 获取商品的BOM清单
                            List<ProductBOM> bomList = productBOMRepository.findByProductId(item.getProductId());
                            if (bomList != null && !bomList.isEmpty()) {
                                // 有BOM表，扣减原料库存
                                for (ProductBOM bom : bomList) {
                                    // 计算需要扣减的原料数量（商品数量 × BOM单数量）
                                    BigDecimal requiredMaterialQuantity = bom.getQuantity()
                                            .multiply(BigDecimal.valueOf(item.getQuantity()));
                                    logger.debug("订单完成，扣减商品ID: {} 的原料ID: {} 库存，数量: {}",
                                            item.getProductId(), bom.getMaterialId(), requiredMaterialQuantity);

                                    // 扣减原料库存
                                    rawMaterialInventoryService.deductRawMaterialInventory(
                                            order.getStoreId(), bom.getMaterialId(), requiredMaterialQuantity);
                                }
                            } else {
                                // 没有BOM表，使用原来的成品库存扣减逻辑（兼容现有测试和旧系统）
                                logger.debug("订单完成，扣减商品ID: {} 的库存，数量: {}",
                                        item.getProductId(), item.getQuantity());

                                // 扣减成品库存
                                inventoryService.deductInventory(item.getProductId(),
                                        BigDecimal.valueOf(item.getQuantity()));
                            }
                        }
                    }
                }

                return orderRepository.save(order);
            }
        }
        return null;
    }

    @Transactional
    public Order processPayment(Long id, Map<String, Object> request) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // 检查订单状态 (1: 待确认)
            if (order.getOrderStatus() == 1) {
                // 从请求中获取支付信息
                String paymentMethod = (String) request.get("paymentMethod");
                Map<String, Object> paymentDetails = (Map<String, Object>) request.get("paymentDetails");

                // 模拟支付成功
                Map<String, Object> paymentResult = Map.of(
                        "success", true,
                        "transactionId", "TXN" + System.currentTimeMillis());

                // 检查支付结果
                if ((Boolean) paymentResult.get("success")) {
                    // 更新订单状态为制作中 (2)
                    order.setOrderStatus(2);
                    order.setPaymentMethod(1); // 假设1: 微信支付

                    return orderRepository.save(order);
                } else {
                    throw new BizException(ErrorCode.PAYMENT_FAILED, "支付失败: 支付处理未完成");
                }
            } else {
                throw new BizException(ErrorCode.PAYMENT_FAILED, "支付失败: 订单状态不允许支付");
            }
        } else {
            throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }

    @Transactional
    public Order processRefund(Long id, String refundReason, BigDecimal refundAmount) {
        Optional<Order> optionalOrder = orderRepository.findById(id);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();

            // 检查订单状态是否可以退款
            Integer currentStatus = order.getOrderStatus();
            if (currentStatus == 3 || currentStatus == 5) { // 3: 已完成, 5: 退款中
                // 更新订单状态为退款中
                order.setOrderStatus(5);
                order.setRemarks(refundReason);

                // 模拟退款处理
                // 实际项目中这里应该调用支付服务进行退款操作

                // 退款成功后更新状态为已退款
                order.setOrderStatus(6);

                // 恢复库存
                if (order.getOrderItems() != null) {
                    for (OrderItem item : order.getOrderItems()) {
                        // 获取商品的BOM清单
                        List<ProductBOM> bomList = productBOMRepository.findByProductId(item.getProductId());
                        if (bomList != null && !bomList.isEmpty()) {
                            // 有BOM表，恢复原料库存
                            for (ProductBOM bom : bomList) {
                                // 计算需要恢复的原料数量（商品数量 × BOM单数量）
                                BigDecimal requiredMaterialQuantity = bom.getQuantity()
                                        .multiply(BigDecimal.valueOf(item.getQuantity()));
                                logger.debug("退款处理，恢复商品ID: {} 的原料ID: {} 库存，数量: {}",
                                        item.getProductId(), bom.getMaterialId(), requiredMaterialQuantity);

                                // 恢复原料库存
                                boolean success = rawMaterialInventoryService.addRawMaterialInventory(
                                        order.getStoreId(), bom.getMaterialId(), requiredMaterialQuantity);
                                if (success) {
                                    logger.debug("成功恢复原料ID: {} 的库存，数量: {}",
                                            bom.getMaterialId(), requiredMaterialQuantity);
                                } else {
                                    logger.error("恢复原料ID: {} 的库存失败，数量: {}",
                                            bom.getMaterialId(), requiredMaterialQuantity);
                                }
                            }
                        } else {
                            // 没有BOM表，使用原来的成品库存恢复逻辑（兼容现有测试和旧系统）
                            logger.debug("退款处理，恢复商品ID: {} 的库存，数量: {}",
                                    item.getProductId(), item.getQuantity());

                            // 恢复成品库存
                            inventoryService.addInventory(item.getProductId(), BigDecimal.valueOf(item.getQuantity()));
                        }
                    }
                }

                return orderRepository.save(order);
            } else {
                throw new BizException(ErrorCode.REFUND_FAILED, "退款失败: 订单状态不允许退款");
            }
        } else {
            throw new BizException(ErrorCode.ORDER_NOT_FOUND, "订单不存在");
        }
    }

    private boolean isValidStatusTransition(Integer oldStatus, Integer newStatus) {
        // 订单状态流转规则
        // 1: 待确认 -> 2: 制作中, 4: 已取消
        // 2: 制作中 -> 3: 已完成, 4: 已取消
        // 3: 已完成 -> 不可更改
        // 4: 已取消 -> 不可更改
        // 5: 退款中 -> 6: 已退款

        switch (oldStatus) {
            case 1:
                return newStatus == 2 || newStatus == 4 || newStatus == 5;
            case 2:
                return newStatus == 3 || newStatus == 4 || newStatus == 5;
            case 3:
                return newStatus == 5;
            case 4:
                return false;
            case 5:
                return newStatus == 6;
            case 6:
                return false;
            default:
                return false;
        }
    }
}
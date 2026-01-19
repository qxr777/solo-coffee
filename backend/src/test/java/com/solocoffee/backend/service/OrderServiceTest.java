package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Order;
import com.solocoffee.backend.entity.OrderItem;
import com.solocoffee.backend.repository.OrderRepository;
import com.solocoffee.backend.repository.ProductBOMRepository;
import com.solocoffee.backend.service.RawMaterialInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private InventoryService inventoryService;

    @Mock
    private RawMaterialInventoryService rawMaterialInventoryService;

    @Mock
    private ProductBOMRepository productBOMRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateOrder() {
        // 创建测试订单
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        order.setOrderStatus(1);

        // 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setProductId(1L);
        item1.setProductName("Americano");
        item1.setQuantity(1);
        item1.setPrice(BigDecimal.valueOf(3.50));
        item1.setSubtotal(BigDecimal.valueOf(3.50));
        orderItems.add(item1);

        order.setOrderItems(orderItems);

        // 模拟 inventoryService.checkInventory() 方法
        when(inventoryService.checkInventory(anyLong(), any(BigDecimal.class))).thenReturn(true);

        // 模拟 repository.save() 方法
        when(orderRepository.save(order)).thenReturn(order);

        // 调用服务方法
        Order createdOrder = orderService.createOrder(order);

        // 验证结果
        assertNotNull(createdOrder);
        assertNotNull(createdOrder.getOrderNo());
        assertEquals(BigDecimal.valueOf(3.50), createdOrder.getTotalAmount());
        assertEquals(BigDecimal.valueOf(3.50), createdOrder.getActualAmount());
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testGetOrderById() {
        // 创建测试订单
        Order order = new Order();
        order.setId(1L);
        order.setOrderNo("ORD123456");
        order.setStoreId(1L);
        order.setCustomerId(1L);

        // 模拟 repository.findById() 方法
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // 调用服务方法
        Optional<Order> result = orderService.getOrderById(1L);

        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("ORD123456", result.get().getOrderNo());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllOrders() {
        // 创建测试订单列表
        List<Order> orders = new ArrayList<>();
        Order order1 = new Order();
        order1.setId(1L);
        order1.setOrderNo("ORD123456");
        orders.add(order1);

        Order order2 = new Order();
        order2.setId(2L);
        order2.setOrderNo("ORD123457");
        orders.add(order2);

        // 模拟 repository.findAll() 方法
        when(orderRepository.findAll()).thenReturn(orders);

        // 调用服务方法
        List<Order> result = orderService.getAllOrders();

        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(orderRepository, times(1)).findAll();
    }

    @Test
    void testUpdateOrderStatus() {
        // 创建测试订单
        Order order = new Order();
        order.setId(1L);
        order.setOrderStatus(1);

        // 模拟 repository.findById() 和 repository.save() 方法
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        when(orderRepository.save(order)).thenReturn(order);

        // 调用服务方法
        Order updatedOrder = orderService.updateOrderStatus(1L, 2);

        // 验证结果
        assertNotNull(updatedOrder);
        assertEquals(2, updatedOrder.getOrderStatus());
        verify(orderRepository, times(1)).findById(1L);
        verify(orderRepository, times(1)).save(order);
    }

    @Test
    void testDeleteOrder() {
        // 调用服务方法
        orderService.deleteOrder(1L);

        // 验证 repository.deleteById() 方法被调用
        verify(orderRepository, times(1)).deleteById(1L);
    }
}
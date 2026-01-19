package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Order;
import com.solocoffee.backend.entity.OrderItem;
import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.entity.Customer;
import com.solocoffee.backend.entity.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import com.solocoffee.backend.service.PromotionService;
import com.solocoffee.backend.service.OrderService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class OrderIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private PromotionService promotionService;
    
    @Autowired
    private OrderService orderService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        // 创建测试产品
        createProduct("Americano", BigDecimal.valueOf(3.50), 1L);
        createProduct("Latte", BigDecimal.valueOf(4.50), 1L);
        
        // 创建测试客户
        createCustomer("Test Customer", "13800138000", "test@example.com");
        
        // 创建测试库存
        createInventory(1L, 100, "杯");
        createInventory(2L, 100, "杯");
    }

    private void createProduct(String name, BigDecimal price, Long categoryId) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setCategoryId(categoryId);
        product.setStatus(1);
        
        restTemplate.postForEntity(baseUrl + "/api/v1/products", product, ApiResponse.class);
    }

    private void createCustomer(String name, String phone, String email) {
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        
        restTemplate.postForEntity(baseUrl + "/api/v1/customers", customer, ApiResponse.class);
    }

    private void createInventory(Long productId, int quantity, String unit) {
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStoreId(1L);
        inventory.setQuantity(BigDecimal.valueOf(quantity));
        inventory.setUnit(unit);
        inventory.setWarningThreshold(BigDecimal.valueOf(10));
        
        restTemplate.postForEntity(baseUrl + "/api/v1/inventory", inventory, ApiResponse.class);
    }

    @Test
    void testCompleteOrderFlow() {
        // 1. 创建订单
        Order order = createTestOrder();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdOrderMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdOrderMap);
        Long orderId = Long.valueOf(createdOrderMap.get("id").toString());
        assertEquals(1L, createdOrderMap.get("orderStatus")); // 待确认
        
        // 2. 处理支付
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("cardNumber", "4111111111111111");
        paymentDetails.put("expiryDate", "12/25");
        paymentDetails.put("cvv", "123");
        
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("paymentMethod", "credit_card");
        paymentRequest.put("paymentDetails", paymentDetails);
        
        ResponseEntity<ApiResponse> paymentResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/pay", 
                paymentRequest, ApiResponse.class);
        
        ApiResponse paymentResponseBody = paymentResponse.getBody();
        assertNotNull(paymentResponseBody);
        assertEquals(200, paymentResponseBody.getCode());
        Map<String, Object> paidOrderMap = (Map<String, Object>) paymentResponseBody.getData();
        assertNotNull(paidOrderMap);
        assertEquals(2L, paidOrderMap.get("orderStatus")); // 制作中
        
        // 3. 更新订单状态为已完成
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", 3); // 已完成
        
        ResponseEntity<ApiResponse> completeResponse = restTemplate.exchange(
                baseUrl + "/api/v1/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest),
                ApiResponse.class);
        
        ApiResponse completeResponseBody = completeResponse.getBody();
        assertNotNull(completeResponseBody);
        assertEquals(200, completeResponseBody.getCode());
        Map<String, Object> completedOrderMap = (Map<String, Object>) completeResponseBody.getData();
        assertNotNull(completedOrderMap);
        assertEquals(3L, completedOrderMap.get("orderStatus")); // 已完成
        

        
        // 5. 验证库存已更新
        ResponseEntity<ApiResponse> inventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/product/" + 1L, ApiResponse.class);
        
        ApiResponse inventoryResponseBody = inventoryResponse.getBody();
        assertNotNull(inventoryResponseBody);
        assertEquals(200, inventoryResponseBody.getCode());
        Map<String, Object> inventoryMap = (Map<String, Object>) inventoryResponseBody.getData();
        assertNotNull(inventoryMap);
        // 验证库存已减少
        assertNotNull(inventoryMap.get("quantity"));
    }

    private Order createTestOrder() {
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item1 = new OrderItem();
        item1.setOrder(order); // 设置订单关联
        item1.setProductId(1L);
        item1.setProductName("Americano");
        item1.setQuantity(1);
        item1.setPrice(BigDecimal.valueOf(3.50));
        item1.setSubtotal(BigDecimal.valueOf(3.50));
        orderItems.add(item1);
        
        order.setOrderItems(orderItems);
        return order;
    }
    
    @Test
    void testOrderWithExactlyEnoughInventory() {
        // 1. 先获取现有库存
        ResponseEntity<ApiResponse> inventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/product/" + 1L, ApiResponse.class);
        
        ApiResponse inventoryResponseBody = inventoryResponse.getBody();
        assertNotNull(inventoryResponseBody);
        Map<String, Object> existingInventoryMap = (Map<String, Object>) inventoryResponseBody.getData();
        assertNotNull(existingInventoryMap);
        Long inventoryId = Long.valueOf(existingInventoryMap.get("id").toString());
        
        // 2. 更新库存为正好等于请求数量
        Inventory inventory = new Inventory();
        inventory.setId(inventoryId);
        inventory.setProductId(1L);
        inventory.setStoreId(1L);
        inventory.setQuantity(BigDecimal.valueOf(5)); // 正好5个
        inventory.setUnit("杯");
        inventory.setWarningThreshold(BigDecimal.valueOf(10));
        
        restTemplate.put(baseUrl + "/api/v1/inventory/" + inventoryId, inventory);
        
        // 3. 创建请求正好5个的订单
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(1L);
        item.setProductName("Americano");
        item.setQuantity(5); // 正好请求5个
        item.setPrice(BigDecimal.valueOf(3.50));
        item.setSubtotal(BigDecimal.valueOf(17.50));
        orderItems.add(item);
        
        order.setOrderItems(orderItems);
        
        // 4. 验证订单创建成功
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdOrderMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdOrderMap);
        assertEquals(1L, createdOrderMap.get("orderStatus")); // 待确认状态
    }

    @Test
    void testOrderWithInsufficientInventory() {
        // 1. 先将库存设置为0
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setStoreId(1L);
        inventory.setQuantity(BigDecimal.valueOf(0));
        inventory.setUnit("杯");
        inventory.setWarningThreshold(BigDecimal.valueOf(10));
        
        restTemplate.put(baseUrl + "/api/v1/inventory/product/" + 1L, inventory);
        
        // 2. 尝试创建订单
        Order order = createTestOrder();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        // 应该返回错误，因为库存不足
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(40004, responseBody.getCode()); // 库存不足错误码
    }

    @Test
    void testOrderWithPromotion() {
        // 1. 创建促销活动
        createPromotion("Test Discount", "discount", 10.0, BigDecimal.valueOf(0));
        
        // 2. 创建订单
        Order order = createTestOrder();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdOrderMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdOrderMap);
        
        // 验证促销已应用
        assertNotNull(createdOrderMap.get("actualAmount"));
    }
    
    @Test
    void testOrderRefund() {
        // 1. 创建订单
        Order order = createTestOrder();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdOrderMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdOrderMap);
        Long orderId = Long.valueOf(createdOrderMap.get("id").toString());
        
        // 2. 处理支付
        Map<String, Object> paymentDetails = new HashMap<>();
        paymentDetails.put("cardNumber", "4111111111111111");
        paymentDetails.put("expiryDate", "12/25");
        paymentDetails.put("cvv", "123");
        
        Map<String, Object> paymentRequest = new HashMap<>();
        paymentRequest.put("paymentMethod", "credit_card");
        paymentRequest.put("paymentDetails", paymentDetails);
        
        ResponseEntity<ApiResponse> paymentResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/pay", 
                paymentRequest, ApiResponse.class);
        
        // 3. 更新订单状态为已完成
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", 3); // 已完成
        
        ResponseEntity<ApiResponse> completeResponse = restTemplate.exchange(
                baseUrl + "/api/v1/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest),
                ApiResponse.class);
        assertEquals(200, completeResponse.getBody().getCode());
        
        // 4. 申请退款
        Map<String, Object> refundRequest = new HashMap<>();
        refundRequest.put("refundReason", "Test refund reason");
        refundRequest.put("refundAmount", 3.50);
        
        ResponseEntity<ApiResponse> refundResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/refund",
                refundRequest,
                ApiResponse.class);
        
        ApiResponse refundResponseBody = refundResponse.getBody();
        assertNotNull(refundResponseBody);
        assertEquals(200, refundResponseBody.getCode());
        Map<String, Object> refundedOrderMap = (Map<String, Object>) refundResponseBody.getData();
        assertNotNull(refundedOrderMap);
        // 验证订单状态已更新为已退款
        assertEquals(6L, refundedOrderMap.get("orderStatus"));
    }
    
    @Test
    void testOrderListWithFiltering() {
        // 1. 创建多个订单用于测试筛选
        createMultipleTestOrders();
        
        // 2. 测试订单列表筛选（按状态）
        ResponseEntity<ApiResponse> statusFilterResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/orders?status=1&page=1&size=10", 
                ApiResponse.class);
        
        ApiResponse statusFilterResponseBody = statusFilterResponse.getBody();
        assertNotNull(statusFilterResponseBody);
        assertEquals(200, statusFilterResponseBody.getCode());
        Map<String, Object> statusFilterData = (Map<String, Object>) statusFilterResponseBody.getData();
        assertNotNull(statusFilterData);
        assertNotNull(statusFilterData.get("orders"));
        assertNotNull(statusFilterData.get("total"));
        assertNotNull(statusFilterData.get("page"));
        assertNotNull(statusFilterData.get("size"));
        assertNotNull(statusFilterData.get("totalPages"));
        
        // 3. 测试订单列表筛选（按日期范围）
        ResponseEntity<ApiResponse> dateFilterResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/orders?startDate=2024-01-01&endDate=2024-12-31&page=1&size=10", 
                ApiResponse.class);
        
        ApiResponse dateFilterResponseBody = dateFilterResponse.getBody();
        assertNotNull(dateFilterResponseBody);
        assertEquals(200, dateFilterResponseBody.getCode());
        Map<String, Object> dateFilterData = (Map<String, Object>) dateFilterResponseBody.getData();
        assertNotNull(dateFilterData);
        assertNotNull(dateFilterData.get("orders"));
    }
    
    @Test
    void testOrderCancellation() {
        // 1. 创建订单
        Order order = createTestOrder();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdOrderMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdOrderMap);
        Long orderId = Long.valueOf(createdOrderMap.get("id").toString());
        
        // 2. 取消订单
        Map<String, Object> cancelRequest = new HashMap<>();
        cancelRequest.put("cancelReason", "Test cancellation reason");
        
        ResponseEntity<ApiResponse> cancelResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/cancel",
                cancelRequest,
                ApiResponse.class);
        
        ApiResponse cancelResponseBody = cancelResponse.getBody();
        assertNotNull(cancelResponseBody);
        assertEquals(200, cancelResponseBody.getCode());
        Map<String, Object> cancelledOrderMap = (Map<String, Object>) cancelResponseBody.getData();
        assertNotNull(cancelledOrderMap);
        // 验证订单状态已更新为已取消
        assertEquals(5L, cancelledOrderMap.get("orderStatus"));
    }
    
    private void createMultipleTestOrders() {
        // 创建第一个订单
        Order order1 = createTestOrder();
        restTemplate.postForEntity(baseUrl + "/api/v1/orders", order1, ApiResponse.class);
        
        // 创建第二个订单
        Order order2 = new Order();
        order2.setStoreId(1L);
        order2.setCustomerId(1L);
        order2.setPaymentMethod(2);
        
        List<OrderItem> orderItems2 = new ArrayList<>();
        OrderItem item2 = new OrderItem();
        item2.setOrder(order2);
        item2.setProductId(2L);
        item2.setProductName("Latte");
        item2.setQuantity(1);
        item2.setPrice(BigDecimal.valueOf(4.50));
        item2.setSubtotal(BigDecimal.valueOf(4.50));
        orderItems2.add(item2);
        
        order2.setOrderItems(orderItems2);
        restTemplate.postForEntity(baseUrl + "/api/v1/orders", order2, ApiResponse.class);
    }
    
    @Test
    void testCreateOrderDirectly() {
        // 直接调用OrderService的createOrder方法，以便获取详细的错误信息
        try {
            // 创建测试订单
            Order order = createTestOrder();
            
            // 直接调用服务方法
            Order createdOrder = orderService.createOrder(order);
            
            // 验证结果
            assertNotNull(createdOrder);
            assertNotNull(createdOrder.getOrderNo());
        } catch (Exception e) {
            // 打印详细的错误信息
            System.err.println("直接调用createOrder方法时发生错误:");
            e.printStackTrace();
            // 重新抛出异常，让测试失败
            throw e;
        }
    }

    private void createPromotion(String name, String type, Double discount, BigDecimal minSpend) {
        PromotionService.Promotion promotion = new PromotionService.Promotion();
        promotion.setName(name);
        promotion.setType(type);
        promotion.setDiscount(discount);
        promotion.setMinSpend(minSpend);
        promotion.setStatus(1);
        
        // 设置有效时间范围
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        calendar.add(java.util.Calendar.DAY_OF_MONTH, -1); // 开始时间为昨天
        promotion.setStartTime(calendar.getTime());
        
        calendar.add(java.util.Calendar.DAY_OF_MONTH, 2); // 结束时间为明天
        promotion.setEndTime(calendar.getTime());
        
        // 直接使用PromotionService创建促销活动
        promotionService.createPromotion(promotion);
    }
}
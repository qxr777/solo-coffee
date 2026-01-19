package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Order;
import com.solocoffee.backend.entity.OrderItem;
import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.entity.ProductBOM;
import com.solocoffee.backend.entity.RawMaterial;
import com.solocoffee.backend.entity.RawMaterialInventory;
import com.solocoffee.backend.service.OrderService;
import com.solocoffee.backend.service.RawMaterialInventoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class BOMIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RawMaterialInventoryService rawMaterialInventoryService;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        // 创建测试分类
        createCategory("Coffee", 1);
        
        // 创建测试产品
        createProduct("Americano", BigDecimal.valueOf(3.50), 1L);
        createProduct("Latte", BigDecimal.valueOf(4.50), 1L);
        
        // 创建测试客户
        createCustomer("Test Customer", "13800138000", "test@example.com");
        
        // 创建测试门店
        createStore("Test Store", "123 Main St", "09:00-18:00", "test@store.com", "13800138001");
    }

    private void createCategory(String name, int sortOrder) {
        Map<String, Object> category = new HashMap<>();
        category.put("categoryName", name);
        category.put("sortOrder", sortOrder);
        restTemplate.postForEntity(baseUrl + "/api/v1/categories", category, ApiResponse.class);
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
        Map<String, Object> customer = new HashMap<>();
        customer.put("name", name);
        customer.put("phone", phone);
        customer.put("email", email);
        restTemplate.postForEntity(baseUrl + "/api/v1/customers", customer, ApiResponse.class);
    }

    private void createStore(String name, String address, String businessHours, String email, String phone) {
        Map<String, Object> store = new HashMap<>();
        store.put("name", name);
        store.put("address", address);
        store.put("businessHours", businessHours);
        store.put("email", email);
        store.put("phone", phone);
        store.put("status", 1);
        restTemplate.postForEntity(baseUrl + "/api/v1/stores", store, ApiResponse.class);
    }

    private Long createRawMaterial(String name, String category, String unit) {
        RawMaterial material = new RawMaterial();
        material.setName(name);
        material.setCategory(category);
        material.setUnit(unit);
        material.setStatus(1);
        material.setMaterialNo("MAT" + System.currentTimeMillis());
        
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/raw-materials", material, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
        return Long.valueOf(data.get("id").toString());
    }

    private void createRawMaterialInventory(Long storeId, Long materialId, BigDecimal quantity) {
        RawMaterialInventory inventory = new RawMaterialInventory();
        inventory.setStoreId(storeId);
        inventory.setMaterialId(materialId);
        inventory.setQuantity(quantity);
        inventory.setWarningThreshold(BigDecimal.valueOf(10));
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        
        restTemplate.postForEntity(
                baseUrl + "/api/v1/raw-material-inventory", inventory, ApiResponse.class);
    }

    private Long createProductBOM(Long productId, Long materialId, BigDecimal quantity, String unit, Boolean isMain) {
        ProductBOM bom = new ProductBOM();
        bom.setProductId(productId);
        bom.setMaterialId(materialId);
        bom.setQuantity(quantity);
        bom.setUnit(unit);
        bom.setIsMain(isMain);
        bom.setCreatedAt(LocalDateTime.now());
        bom.setUpdatedAt(LocalDateTime.now());
        
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/product-bom", bom, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> data = (Map<String, Object>) response.getBody().getData();
        return Long.valueOf(data.get("id").toString());
    }

    // 1. BOM 基础测试
    @Test
    void testBOMCrudOperations() {
        // 创建原材料
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        Long milkId = createRawMaterial("Milk", "Dairy", "ml");
        
        // 创建BOM
        Long bomId = createProductBOM(1L, coffeeBeanId, BigDecimal.valueOf(15), "g", true);
        
        // 验证BOM创建成功
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/product-bom/" + bomId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Map<String, Object> bomData = (Map<String, Object>) getResponse.getBody().getData();
        assertNotNull(bomData);
        assertEquals(1L, Long.valueOf(bomData.get("productId").toString()));
        assertEquals(coffeeBeanId, Long.valueOf(bomData.get("materialId").toString()));
        assertTrue(bomData.get("quantity").toString().startsWith("15")); // 允许小数点
        assertEquals("g", bomData.get("unit"));
        assertEquals(true, bomData.get("isMain"));
        
        // 更新BOM
        ProductBOM updatedBom = new ProductBOM();
        updatedBom.setId(bomId);
        updatedBom.setProductId(1L);
        updatedBom.setMaterialId(coffeeBeanId);
        updatedBom.setQuantity(BigDecimal.valueOf(20));
        updatedBom.setUnit("g");
        updatedBom.setIsMain(true);
        updatedBom.setCreatedAt(LocalDateTime.now());
        updatedBom.setUpdatedAt(LocalDateTime.now());
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/product-bom/" + bomId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedBom),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        Map<String, Object> updatedBomData = (Map<String, Object>) updateResponse.getBody().getData();
        assertEquals("20", updatedBomData.get("quantity").toString());
        
        // 获取产品的所有BOM
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/product-bom/product/" + 1L, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<?> bomList = (List<?>) listResponse.getBody().getData();
        assertTrue(bomList.size() > 0);
    }

    // 2. 原材料库存测试
    @Test
    void testRawMaterialInventoryOperations() {
        // 创建原材料
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        
        // 创建原材料库存
        createRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(1000));
        
        // 检查原材料库存
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/store/" + 1L + "/material/" + coffeeBeanId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        Map<String, Object> inventoryData = (Map<String, Object>) getResponse.getBody().getData();
        assertTrue(inventoryData.get("quantity").toString().startsWith("1000")); // 允许小数点
        
        // 测试库存检查
        boolean isSufficient = rawMaterialInventoryService.checkRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(100));
        assertTrue(isSufficient);
        
        // 测试库存不足
        isSufficient = rawMaterialInventoryService.checkRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(2000));
        assertFalse(isSufficient);
    }

    // 3. BOM 集成测试
    @Test
    void testOrderWithBOM() {
        // 创建原材料
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        Long milkId = createRawMaterial("Milk", "Dairy", "ml");
        
        // 创建原材料库存
        createRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(1000));
        createRawMaterialInventory(1L, milkId, BigDecimal.valueOf(5000));
        
        // 创建产品BOM
        createProductBOM(1L, coffeeBeanId, BigDecimal.valueOf(15), "g", true);
        createProductBOM(1L, milkId, BigDecimal.valueOf(200), "ml", false);
        
        // 创建包含BOM的产品订单
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(1L);
        item.setProductName("Americano");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(3.50));
        item.setSubtotal(BigDecimal.valueOf(7.00));
        orderItems.add(item);
        
        order.setOrderItems(orderItems);
        
        // 创建订单
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Map<String, Object> orderData = (Map<String, Object>) createResponse.getBody().getData();
        Long orderId = Long.valueOf(orderData.get("id").toString());
        
        // 先支付订单，将状态改为制作中
        Map<String, Object> payRequest = new HashMap<>();
        payRequest.put("paymentMethod", "WeChat");
        payRequest.put("paymentDetails", Map.of("transactionId", "TXN" + System.currentTimeMillis()));
        
        ResponseEntity<ApiResponse> payResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/pay",
                payRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, payResponse.getStatusCode());
        
        // 完成订单，验证原材料库存扣减
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", 3); // 已完成
        
        ResponseEntity<ApiResponse> completeResponse = restTemplate.exchange(
                baseUrl + "/api/v1/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, completeResponse.getStatusCode());
        
        // 验证原材料库存已扣减
        ResponseEntity<ApiResponse> coffeeInventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/store/" + 1L + "/material/" + coffeeBeanId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, coffeeInventoryResponse.getStatusCode());
        Map<String, Object> coffeeInventoryData = (Map<String, Object>) coffeeInventoryResponse.getBody().getData();
        BigDecimal coffeeQuantity = new BigDecimal(coffeeInventoryData.get("quantity").toString());
        // 2杯 × 15g = 30g 扣减
        assertEquals(BigDecimal.valueOf(970.0), coffeeQuantity);
        
        ResponseEntity<ApiResponse> milkInventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/store/" + 1L + "/material/" + milkId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, milkInventoryResponse.getStatusCode());
        Map<String, Object> milkInventoryData = (Map<String, Object>) milkInventoryResponse.getBody().getData();
        BigDecimal milkQuantity = new BigDecimal(milkInventoryData.get("quantity").toString());
        // 2杯 × 200ml = 400ml 扣减
        assertEquals(BigDecimal.valueOf(4600.0), milkQuantity);
        
        // 测试退款时原材料库存恢复
        Map<String, Object> refundRequest = new HashMap<>();
        refundRequest.put("refundReason", "Test refund");
        refundRequest.put("refundAmount", 7.00);
        
        ResponseEntity<ApiResponse> refundResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/refund",
                refundRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, refundResponse.getStatusCode());
        
        // 验证原材料库存已恢复
        ResponseEntity<ApiResponse> coffeeInventoryAfterRefundResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/store/1/material/" + coffeeBeanId, ApiResponse.class);
        
        Map<String, Object> coffeeInventoryAfterRefundData = (Map<String, Object>) coffeeInventoryAfterRefundResponse.getBody().getData();
        BigDecimal coffeeQuantityAfterRefund = new BigDecimal(coffeeInventoryAfterRefundData.get("quantity").toString());
        assertEquals(BigDecimal.valueOf(1000.0), coffeeQuantityAfterRefund);
    }

    // 4. 边界场景测试
    @Test
    void testMultiProductSharedMaterial() {
        // 创建原材料（多产品共用）
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        
        // 创建原材料库存
        createRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(500));
        
        // 为两个产品创建BOM
        createProductBOM(1L, coffeeBeanId, BigDecimal.valueOf(15), "g", true);
        createProductBOM(2L, coffeeBeanId, BigDecimal.valueOf(20), "g", true);
        
        // 创建包含两个产品的订单
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        
        List<OrderItem> orderItems = new ArrayList<>();
        
        // 产品1：Americano
        OrderItem item1 = new OrderItem();
        item1.setOrder(order);
        item1.setProductId(1L);
        item1.setProductName("Americano");
        item1.setQuantity(2);
        item1.setPrice(BigDecimal.valueOf(3.50));
        item1.setSubtotal(BigDecimal.valueOf(7.00));
        orderItems.add(item1);
        
        // 产品2：Latte
        OrderItem item2 = new OrderItem();
        item2.setOrder(order);
        item2.setProductId(2L);
        item2.setProductName("Latte");
        item2.setQuantity(2);
        item2.setPrice(BigDecimal.valueOf(4.50));
        item2.setSubtotal(BigDecimal.valueOf(9.00));
        orderItems.add(item2);
        
        order.setOrderItems(orderItems);
        
        // 创建订单
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Map<String, Object> orderData = (Map<String, Object>) createResponse.getBody().getData();
        Long orderId = Long.valueOf(orderData.get("id").toString());
        
        // 先支付订单，将状态改为制作中
        Map<String, Object> payRequest = new HashMap<>();
        payRequest.put("paymentMethod", "WeChat");
        payRequest.put("paymentDetails", Map.of("transactionId", "TXN" + System.currentTimeMillis()));
        
        ResponseEntity<ApiResponse> payResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders/" + orderId + "/pay",
                payRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, payResponse.getStatusCode());
        
        // 完成订单
        Map<String, Object> statusRequest = new HashMap<>();
        statusRequest.put("status", 3); // 已完成
        
        ResponseEntity<ApiResponse> completeResponse = restTemplate.exchange(
                baseUrl + "/api/v1/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(statusRequest),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, completeResponse.getStatusCode());
        
        // 验证原材料库存扣减
        // 产品1: 2 × 15g = 30g
        // 产品2: 2 × 20g = 40g
        // 总计扣减: 70g
        ResponseEntity<ApiResponse> inventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/store/" + 1L + "/material/" + coffeeBeanId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, inventoryResponse.getStatusCode());
        Map<String, Object> inventoryData = (Map<String, Object>) inventoryResponse.getBody().getData();
        BigDecimal remainingQuantity = new BigDecimal(inventoryData.get("quantity").toString());
        assertEquals(BigDecimal.valueOf(430.0), remainingQuantity);
    }

    @Test
    void testRawMaterialLowStockWarning() {
        // 创建原材料
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        
        // 创建低库存的原材料库存
        createRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(5));
        
        // 测试低库存预警
        ResponseEntity<ApiResponse> lowStockResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/raw-material-inventory/low-stock", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, lowStockResponse.getStatusCode());
        List<?> lowStockItems = (List<?>) lowStockResponse.getBody().getData();
        assertTrue(lowStockItems.size() > 0);
    }

    @Test
    void testInsufficientRawMaterialInventory() {
        // 创建原材料
        Long coffeeBeanId = createRawMaterial("Coffee Bean", "Coffee", "g");
        
        // 创建原材料库存（不足）
        createRawMaterialInventory(1L, coffeeBeanId, BigDecimal.valueOf(10));
        
        // 创建产品BOM
        createProductBOM(1L, coffeeBeanId, BigDecimal.valueOf(15), "g", true);
        
        // 尝试创建订单（需要2杯，共30g，库存只有10g）
        Order order = new Order();
        order.setStoreId(1L);
        order.setCustomerId(1L);
        order.setPaymentMethod(1);
        
        List<OrderItem> orderItems = new ArrayList<>();
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProductId(1L);
        item.setProductName("Americano");
        item.setQuantity(2);
        item.setPrice(BigDecimal.valueOf(3.50));
        item.setSubtotal(BigDecimal.valueOf(7.00));
        orderItems.add(item);
        
        order.setOrderItems(orderItems);
        
        // 验证订单创建失败（原材料库存不足）
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/orders", order, ApiResponse.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertTrue(responseBody.getMessage().contains("原料库存不足"));
    }
}

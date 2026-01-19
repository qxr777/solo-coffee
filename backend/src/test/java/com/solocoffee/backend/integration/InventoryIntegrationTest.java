package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Inventory;
import com.solocoffee.backend.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class InventoryIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        // 初始化测试产品
        createTestProduct();
    }

    private void createTestProduct() {
        Product product = new Product();
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(5.00));
        product.setCategoryId(1L);
        product.setStatus(1);
        
        restTemplate.postForEntity(baseUrl + "/api/v1/products", product, Product.class);
    }

    @Test
    void testCompleteInventoryFlow() {
        // 1. 创建库存
        Inventory inventory = createTestInventory();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory", inventory, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdInventoryMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdInventoryMap);
        Long inventoryId = Long.valueOf(createdInventoryMap.get("id").toString());
        
        // 2. 获取库存详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/" + inventoryId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedInventoryMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedInventoryMap);
        assertEquals(inventoryId, Long.valueOf(retrievedInventoryMap.get("id").toString()));
        assertEquals(inventory.getProductId(), Long.valueOf(retrievedInventoryMap.get("productId").toString()));
        
        // 3. 更新库存数量
        Inventory updatedInventory = new Inventory();
        updatedInventory.setId(inventoryId);
        updatedInventory.setProductId(inventory.getProductId());
        updatedInventory.setStoreId(inventory.getStoreId());
        updatedInventory.setQuantity(BigDecimal.valueOf(30));
        updatedInventory.setUnit(inventory.getUnit());
        updatedInventory.setWarningThreshold(inventory.getWarningThreshold());
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/inventory/" + inventoryId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedInventory),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedInventoryMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedInventoryMap);
        assertEquals("30", updatedInventoryMap.get("quantity").toString());
        
        // 4. 通过产品ID获取库存
        ResponseEntity<ApiResponse> getByProductResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/product/" + 1L, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getByProductResponse.getStatusCode());
        ApiResponse getByProductResponseBody = getByProductResponse.getBody();
        assertNotNull(getByProductResponseBody);
        assertEquals(200, getByProductResponseBody.getCode());
        Map<String, Object> inventoryByProductMap = (Map<String, Object>) getByProductResponseBody.getData();
        assertNotNull(inventoryByProductMap);
        assertEquals(1L, Long.valueOf(inventoryByProductMap.get("productId").toString()));
        
        // 5. 获取低库存商品
        ResponseEntity<ApiResponse> lowStockResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/warning", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, lowStockResponse.getStatusCode());
        ApiResponse lowStockResponseBody = lowStockResponse.getBody();
        assertNotNull(lowStockResponseBody);
        assertEquals(200, lowStockResponseBody.getCode());
        List<?> lowStockItems = (List<?>) lowStockResponseBody.getData();
        assertNotNull(lowStockItems);
    }

    @Test
    void testInventoryWithLowStock() {
        // 1. 创建低库存
        Inventory lowInventory = new Inventory();
        lowInventory.setProductId(1L);
        lowInventory.setStoreId(1L);
        lowInventory.setQuantity(BigDecimal.valueOf(5)); // 低于预警阈值
        lowInventory.setUnit("个");
        lowInventory.setWarningThreshold(BigDecimal.valueOf(10));
        
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory", lowInventory, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdInventoryMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdInventoryMap);
        
        // 2. 检查低库存
        ResponseEntity<ApiResponse> lowStockResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/warning", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, lowStockResponse.getStatusCode());
        ApiResponse lowStockResponseBody = lowStockResponse.getBody();
        assertNotNull(lowStockResponseBody);
        assertEquals(200, lowStockResponseBody.getCode());
        List<?> lowStockItems = (List<?>) lowStockResponseBody.getData();
        assertNotNull(lowStockItems);
    }
    
    @Test
    void testReduceInventory() {
        // 1. 创建库存
        Inventory inventory = createTestInventory();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory", inventory, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        // 2. 减少库存
        Map<String, Object> reduceRequest = new java.util.HashMap<>();
        reduceRequest.put("productId", 1L);
        reduceRequest.put("quantity", 5);
        
        ResponseEntity<ApiResponse> reduceResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory/reduce",
                reduceRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, reduceResponse.getStatusCode());
        ApiResponse reduceResponseBody = reduceResponse.getBody();
        assertNotNull(reduceResponseBody);
        assertEquals(200, reduceResponseBody.getCode());
        Map<String, Object> updatedInventoryMap = (Map<String, Object>) reduceResponseBody.getData();
        assertNotNull(updatedInventoryMap);
        // 验证库存已减少
        BigDecimal updatedQuantity = new BigDecimal(updatedInventoryMap.get("quantity").toString());
        assertEquals(BigDecimal.valueOf(45.0), updatedQuantity);
    }

    private Inventory createTestInventory() {
        Inventory inventory = new Inventory();
        inventory.setProductId(1L);
        inventory.setStoreId(1L);
        inventory.setQuantity(BigDecimal.valueOf(50));
        inventory.setUnit("个");
        inventory.setWarningThreshold(BigDecimal.valueOf(10));
        return inventory;
    }
    
    @Test
    void testAutoReorderFunctionality() {
        // 1. 创建低库存（低于补货阈值5）
        Inventory lowInventory = new Inventory();
        lowInventory.setProductId(1L);
        lowInventory.setStoreId(1L);
        lowInventory.setQuantity(BigDecimal.valueOf(3)); // 低于补货阈值
        lowInventory.setUnit("个");
        lowInventory.setWarningThreshold(BigDecimal.valueOf(10));
        
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory", lowInventory, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        
        // 2. 触发自动补货
        ResponseEntity<ApiResponse> reorderResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory/auto-reorder", 
                null, 
                ApiResponse.class);
        
        // 3. 验证补货成功
        assertEquals(HttpStatus.OK, reorderResponse.getStatusCode());
        ApiResponse reorderResponseBody = reorderResponse.getBody();
        assertNotNull(reorderResponseBody);
        assertEquals(200, reorderResponseBody.getCode());
        
        // 4. 验证库存已更新
        ResponseEntity<ApiResponse> inventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/product/" + 1L, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, inventoryResponse.getStatusCode());
        ApiResponse inventoryResponseBody = inventoryResponse.getBody();
        assertNotNull(inventoryResponseBody);
        Map<String, Object> inventoryMap = (Map<String, Object>) inventoryResponseBody.getData();
        assertNotNull(inventoryMap);
        
        BigDecimal updatedQuantity = new BigDecimal(inventoryMap.get("quantity").toString());
        // 验证库存已补货（默认补货后为50）
        assertEquals(BigDecimal.valueOf(50.0), updatedQuantity);
    }
}
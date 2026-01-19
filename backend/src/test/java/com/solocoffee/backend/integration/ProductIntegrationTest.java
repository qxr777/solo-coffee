package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.entity.Inventory;
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
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class ProductIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
    }

    @Test
    void testCompleteProductFlow() {
        // 1. 创建产品
        Product product = createTestProduct();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/products", product, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdProductMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdProductMap);
        Long productId = Long.valueOf(createdProductMap.get("id").toString());
        assertNotNull(createdProductMap.get("productNo"));
        assertEquals(1L, createdProductMap.get("status"));
        
        // 2. 获取产品详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/products/" + productId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedProductMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedProductMap);
        assertEquals(productId, Long.valueOf(retrievedProductMap.get("id").toString()));
        assertEquals(product.getName(), retrievedProductMap.get("name"));
        
        // 3. 更新产品
        Product updatedProduct = new Product();
        updatedProduct.setId(productId);
        updatedProduct.setName("Updated Americano");
        updatedProduct.setPrice(BigDecimal.valueOf(3.80));
        updatedProduct.setCategoryId(product.getCategoryId());
        updatedProduct.setStatus(1);
        updatedProduct.setDescription(product.getDescription());
        updatedProduct.setImageUrl(product.getImageUrl());
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/products/" + productId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedProduct),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedProductMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedProductMap);
        assertEquals("Updated Americano", updatedProductMap.get("name"));
        
        // 4. 禁用产品
        ResponseEntity<ApiResponse> disableResponse = restTemplate.exchange(
                baseUrl + "/api/v1/products/" + productId + "/status/0",
                HttpMethod.PUT,
                null,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, disableResponse.getStatusCode());
        ApiResponse disableResponseBody = disableResponse.getBody();
        assertNotNull(disableResponseBody);
        assertEquals(200, disableResponseBody.getCode());
        Map<String, Object> disabledProductMap = (Map<String, Object>) disableResponseBody.getData();
        assertNotNull(disabledProductMap);
        assertEquals(0L, disabledProductMap.get("status"));
        
        // 5. 获取所有产品
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/products", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        List<?> products = (List<?>) listResponseBody.getData();
        assertNotNull(products);
        assertTrue(products.size() > 0);
    }

    @Test
    void testProductWithInventory() {
        // 1. 创建产品
        Product product = createTestProduct();

        ResponseEntity<ApiResponse> createProductResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/products", product, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createProductResponse.getStatusCode());
        ApiResponse createProductResponseBody = createProductResponse.getBody();
        assertNotNull(createProductResponseBody);
        assertEquals(200, createProductResponseBody.getCode());
        Map<String, Object> createdProductMap = (Map<String, Object>) createProductResponseBody.getData();
        assertNotNull(createdProductMap);
        Long productId = Long.valueOf(createdProductMap.get("id").toString());
        
        // 2. 为产品创建库存
        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStoreId(1L);
        inventory.setQuantity(BigDecimal.valueOf(50));
        inventory.setUnit("杯");
        inventory.setWarningThreshold(BigDecimal.valueOf(5));
        
        ResponseEntity<ApiResponse> createInventoryResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/inventory", inventory, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createInventoryResponse.getStatusCode());
        ApiResponse createInventoryResponseBody = createInventoryResponse.getBody();
        assertNotNull(createInventoryResponseBody);
        assertEquals(200, createInventoryResponseBody.getCode());
        Map<String, Object> createdInventoryMap = (Map<String, Object>) createInventoryResponseBody.getData();
        assertNotNull(createdInventoryMap);
        assertEquals(productId, Long.valueOf(createdInventoryMap.get("productId").toString()));
        
        // 3. 通过产品ID获取库存
        ResponseEntity<ApiResponse> getInventoryResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/inventory/product/" + productId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getInventoryResponse.getStatusCode());
        ApiResponse getInventoryResponseBody = getInventoryResponse.getBody();
        assertNotNull(getInventoryResponseBody);
        assertEquals(200, getInventoryResponseBody.getCode());
        Map<String, Object> retrievedInventoryMap = (Map<String, Object>) getInventoryResponseBody.getData();
        assertNotNull(retrievedInventoryMap);
        assertEquals(productId, Long.valueOf(retrievedInventoryMap.get("productId").toString()));
    }

    private Product createTestProduct() {
        Product product = new Product();
        product.setName("Americano");
        product.setPrice(BigDecimal.valueOf(3.50));
        product.setCategoryId(1L);
        product.setStatus(1);
        product.setDescription("A classic coffee drink");
        product.setImageUrl("https://example.com/americano.jpg");
        return product;
    }
}
package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class AnalyticsIntegrationTest {

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
    void testGetSalesData() {
        // 获取销售数据
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/analytics/sales?startDate=2024-01-01&endDate=2024-12-31&storeId=1", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        // 验证返回的数据结构
        assertNotNull(data.get("totalSales"));
        assertNotNull(data.get("orderCount"));
        assertNotNull(data.get("averageOrderValue"));
        assertNotNull(data.get("salesByDay"));
    }

    @Test
    void testGetPopularProducts() {
        // 获取热销商品统计
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/analytics/popular-products?startDate=2024-01-01&endDate=2024-12-31&limit=10", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("popularProducts"));
    }

    @Test
    void testGetSalesTrend() {
        // 获取销售趋势
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/analytics/sales-trend?period=month&startDate=2024-01-01&endDate=2024-12-31", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("trendData"));
        assertNotNull(data.get("labels"));
        assertNotNull(data.get("datasets"));
    }

    @Test
    void testGetInventoryAnalytics() {
        // 获取库存分析
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/analytics/inventory?storeId=1&categoryId=1", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("totalProducts"));
        assertNotNull(data.get("lowStockProducts"));
        assertNotNull(data.get("outOfStockProducts"));
        assertNotNull(data.get("inventoryValue"));
    }

    @Test
    void testGetCustomerAnalytics() {
        // 获取客户分析
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/analytics/customers?startDate=2024-01-01&endDate=2024-12-31", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("totalCustomers"));
        assertNotNull(data.get("newCustomers"));
        assertNotNull(data.get("returningCustomers"));
        assertNotNull(data.get("customerRetentionRate"));
        assertNotNull(data.get("averageOrderPerCustomer"));
    }
}
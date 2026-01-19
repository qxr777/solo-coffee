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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class RecommendationIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;
    private Long testCustomerId;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        initTestData();
    }

    private void initTestData() {
        // 创建测试客户
        Map<String, Object> customerData = new HashMap<>();
        customerData.put("name", "Test Customer");
        customerData.put("phone", "13800138000");
        customerData.put("email", "customer@example.com");
        customerData.put("password", "password123");
        
        ResponseEntity<ApiResponse> customerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                customerData, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, customerResponse.getStatusCode());
        ApiResponse responseBody = customerResponse.getBody();
        assertNotNull(responseBody);
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        testCustomerId = Long.valueOf(data.get("id").toString());
    }

    @Test
    void testGetPersonalizedRecommendations() {
        // 获取个性化商品推荐
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/recommendations/personalized?customerId=" + testCustomerId + "&limit=10", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("recommendations"));
        
        List<Map<String, Object>> recommendations = (List<Map<String, Object>>) data.get("recommendations");
        assertNotNull(recommendations);
        // 验证推荐列表中的每个商品都有必要的字段
        for (Map<String, Object> recommendation : recommendations) {
            assertNotNull(recommendation.get("productId"));
            assertNotNull(recommendation.get("productName"));
            assertNotNull(recommendation.get("price"));
            assertNotNull(recommendation.get("score"));
        }
    }

    @Test
    void testGetQuickReorderRecommendations() {
        // 获取快速复购建议
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/recommendations/quick-reorder?customerId=" + testCustomerId + "&limit=10", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("quickReorderItems"));
        
        List<Map<String, Object>> quickReorderItems = (List<Map<String, Object>>) data.get("quickReorderItems");
        assertNotNull(quickReorderItems);
        // 验证快速复购列表中的每个商品都有必要的字段
        for (Map<String, Object> item : quickReorderItems) {
            assertNotNull(item.get("productId"));
            assertNotNull(item.get("productName"));
            assertNotNull(item.get("price"));
            assertNotNull(item.get("lastOrderTime"));
            assertNotNull(item.get("orderCount"));
        }
    }

    @Test
    void testGetRecommendationReason() {
        // 1. 先获取个性化推荐，找到一个商品ID
        ResponseEntity<ApiResponse> recommendResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/recommendations/personalized?customerId=" + testCustomerId + "&limit=5", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, recommendResponse.getStatusCode());
        Map<String, Object> recommendData = (Map<String, Object>) recommendResponse.getBody().getData();
        List<Map<String, Object>> recommendations = (List<Map<String, Object>>) recommendData.get("recommendations");
        
        // 如果有推荐商品，测试获取推荐原因
        if (!recommendations.isEmpty()) {
            Map<String, Object> firstRecommendation = recommendations.get(0);
            Long productId = Long.valueOf(firstRecommendation.get("productId").toString());
            
            // 2. 获取推荐原因
            ResponseEntity<ApiResponse> reasonResponse = restTemplate.getForEntity(
                    baseUrl + "/api/v1/recommendations/reason?customerId=" + testCustomerId + "&productId=" + productId, 
                    ApiResponse.class);
            
            assertEquals(HttpStatus.OK, reasonResponse.getStatusCode());
            ApiResponse reasonResponseBody = reasonResponse.getBody();
            assertNotNull(reasonResponseBody);
            assertEquals(200, reasonResponseBody.getCode());
            
            Map<String, Object> reasonData = (Map<String, Object>) reasonResponseBody.getData();
            assertNotNull(reasonData);
            assertNotNull(reasonData.get("productId"));
            assertNotNull(reasonData.get("reasons"));
            
            List<String> reasons = (List<String>) reasonData.get("reasons");
            assertNotNull(reasons);
            assertFalse(reasons.isEmpty());
        }
    }
}
package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class PredictionIntegrationTest {

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
    void testPredictOrder() {
        // 构建订单预测请求
        Map<String, Object> predictRequest = new HashMap<>();
        predictRequest.put("customerId", testCustomerId);
        predictRequest.put("storeId", 1L);
        predictRequest.put("timeRange", "morning"); // morning, noon, evening
        
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/prediction/predict", 
                predictRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("predictionId"));
        assertNotNull(data.get("predictedItems"));
        assertNotNull(data.get("predictedTime"));
        assertNotNull(data.get("confidence"));
        
        List<Map<String, Object>> predictedItems = (List<Map<String, Object>>) data.get("predictedItems");
        assertNotNull(predictedItems);
        // 验证预测商品列表中的每个商品都有必要的字段
        for (Map<String, Object> item : predictedItems) {
            assertNotNull(item.get("productId"));
            assertNotNull(item.get("productName"));
            assertNotNull(item.get("quantity"));
            assertNotNull(item.get("price"));
        }
    }

    @Test
    void testConfirmPrediction() {
        // 1. 先创建订单预测
        Map<String, Object> predictRequest = new HashMap<>();
        predictRequest.put("customerId", testCustomerId);
        predictRequest.put("storeId", 1L);
        predictRequest.put("timeRange", "morning");
        
        ResponseEntity<ApiResponse> predictResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/prediction/predict", 
                predictRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, predictResponse.getStatusCode());
        Map<String, Object> predictData = (Map<String, Object>) predictResponse.getBody().getData();
        Long predictionId = Long.valueOf(predictData.get("predictionId").toString());
        
        // 2. 确认预测订单
        Map<String, Object> confirmRequest = new HashMap<>();
        confirmRequest.put("customerId", testCustomerId);
        confirmRequest.put("paymentMethod", "wechat");
        
        ResponseEntity<ApiResponse> confirmResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/prediction/" + predictionId + "/confirm", 
                confirmRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, confirmResponse.getStatusCode());
        ApiResponse confirmResponseBody = confirmResponse.getBody();
        assertNotNull(confirmResponseBody);
        assertEquals(200, confirmResponseBody.getCode());
        
        Map<String, Object> confirmData = (Map<String, Object>) confirmResponseBody.getData();
        assertNotNull(confirmData);
        assertNotNull(confirmData.get("orderId"));
        assertNotNull(confirmData.get("status"));
        assertEquals("confirmed", confirmData.get("status"));
    }

    @Test
    void testGetPredictionOrders() {
        // 获取预测订单列表
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/prediction/orders?customerId=" + testCustomerId + "&page=1&size=10", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("predictions"));
        assertNotNull(data.get("total"));
        assertNotNull(data.get("page"));
        assertNotNull(data.get("size"));
        assertNotNull(data.get("totalPages"));
    }

    @Test
    void testCancelPrediction() {
        // 1. 先创建订单预测
        Map<String, Object> predictRequest = new HashMap<>();
        predictRequest.put("customerId", testCustomerId);
        predictRequest.put("storeId", 1L);
        predictRequest.put("timeRange", "morning");
        
        ResponseEntity<ApiResponse> predictResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/prediction/predict", 
                predictRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, predictResponse.getStatusCode());
        Map<String, Object> predictData = (Map<String, Object>) predictResponse.getBody().getData();
        Long predictionId = Long.valueOf(predictData.get("predictionId").toString());
        
        // 2. 取消预测订单
        Map<String, Object> cancelRequest = new HashMap<>();
        cancelRequest.put("customerId", testCustomerId);
        cancelRequest.put("reason", "改变主意");
        
        ResponseEntity<ApiResponse> cancelResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/prediction/" + predictionId + "/cancel", 
                cancelRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, cancelResponse.getStatusCode());
        ApiResponse cancelResponseBody = cancelResponse.getBody();
        assertNotNull(cancelResponseBody);
        assertEquals(200, cancelResponseBody.getCode());
        
        Map<String, Object> cancelData = (Map<String, Object>) cancelResponseBody.getData();
        assertNotNull(cancelData);
        assertNotNull(cancelData.get("predictionId"));
        assertNotNull(cancelData.get("status"));
        assertEquals("cancelled", cancelData.get("status"));
    }
}
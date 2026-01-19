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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class NotificationIntegrationTest {

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
    void testGetNotifications() {
        // 获取通知列表（默认参数）
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/notifications?customerId=" + testCustomerId + "&page=1&size=10&status=0", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("notifications"));
        assertNotNull(data.get("total"));
        assertNotNull(data.get("page"));
        assertNotNull(data.get("size"));
        assertNotNull(data.get("totalPages"));
    }

    @Test
    void testMarkAsRead() {
        // 1. 先获取通知列表，找到一个通知ID
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/notifications?customerId=" + testCustomerId + "&page=1&size=10&status=0", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        Map<String, Object> listData = (Map<String, Object>) listResponse.getBody().getData();
        List<Map<String, Object>> notifications = (List<Map<String, Object>>) listData.get("notifications");
        
        // 如果有通知，测试标记为已读
        if (!notifications.isEmpty()) {
            Map<String, Object> firstNotification = notifications.get(0);
            Long notificationId = Long.valueOf(firstNotification.get("notificationId").toString());
            
            // 2. 标记通知为已读
            ResponseEntity<ApiResponse> readResponse = restTemplate.exchange(
                    baseUrl + "/api/v1/notifications/" + notificationId + "/read?customerId=" + testCustomerId,
                    HttpMethod.PUT,
                    null,
                    ApiResponse.class);
            
            assertEquals(HttpStatus.OK, readResponse.getStatusCode());
            ApiResponse readResponseBody = readResponse.getBody();
            assertNotNull(readResponseBody);
            assertEquals(200, readResponseBody.getCode());
            
            Map<String, Object> readData = (Map<String, Object>) readResponseBody.getData();
            assertNotNull(readData);
            assertEquals(notificationId, readData.get("notificationId"));
            assertEquals(true, readData.get("isRead"));
        }
    }

    @Test
    void testBatchMarkAsRead() {
        // 1. 先获取通知列表，收集通知ID
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/notifications?customerId=" + testCustomerId + "&page=1&size=10&status=0", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        Map<String, Object> listData = (Map<String, Object>) listResponse.getBody().getData();
        List<Map<String, Object>> notifications = (List<Map<String, Object>>) listData.get("notifications");
        
        // 如果有通知，测试批量标记为已读
        if (!notifications.isEmpty()) {
            List<Long> notificationIds = new ArrayList<>();
            for (Map<String, Object> notification : notifications) {
                notificationIds.add(Long.valueOf(notification.get("notificationId").toString()));
            }
            
            // 2. 构建批量标记请求
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("customerId", testCustomerId);
            
            HttpEntity<List<Long>> requestEntity = new HttpEntity<>(notificationIds);
            
            // 3. 批量标记为已读
            ResponseEntity<ApiResponse> batchReadResponse = restTemplate.exchange(
                    baseUrl + "/api/v1/notifications/batch-read?customerId=" + testCustomerId,
                    HttpMethod.PUT,
                    requestEntity,
                    ApiResponse.class);
            
            assertEquals(HttpStatus.OK, batchReadResponse.getStatusCode());
            ApiResponse batchReadResponseBody = batchReadResponse.getBody();
            assertNotNull(batchReadResponseBody);
            assertEquals(200, batchReadResponseBody.getCode());
            
            Map<String, Object> batchReadData = (Map<String, Object>) batchReadResponseBody.getData();
            assertNotNull(batchReadData);
            assertNotNull(batchReadData.get("successCount"));
            assertNotNull(batchReadData.get("successIds"));
        }
    }

    @Test
    void testGetUnreadCount() {
        // 获取未读通知数量
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/notifications/unread-count?customerId=" + testCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("unreadCount"));
        assertTrue(((Long) data.get("unreadCount")) >= 0);
    }

    @Test
    void testNotificationPermissionValidation() {
        // 1. 先获取通知列表，找到一个通知ID
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/notifications?customerId=" + testCustomerId + "&page=1&size=10&status=0", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        Map<String, Object> listData = (Map<String, Object>) listResponse.getBody().getData();
        List<Map<String, Object>> notifications = (List<Map<String, Object>>) listData.get("notifications");
        
        // 如果有通知，测试使用其他客户ID标记为已读（应该失败）
        if (!notifications.isEmpty()) {
            Map<String, Object> firstNotification = notifications.get(0);
            Long notificationId = Long.valueOf(firstNotification.get("notificationId").toString());
            
            // 使用其他客户ID
            Long otherCustomerId = testCustomerId + 1;
            
            // 尝试标记为已读
            ResponseEntity<ApiResponse> readResponse = restTemplate.exchange(
                    baseUrl + "/api/v1/notifications/" + notificationId + "/read?customerId=" + otherCustomerId,
                    HttpMethod.PUT,
                    null,
                    ApiResponse.class);
            
            // 应该返回错误
            assertEquals(HttpStatus.BAD_REQUEST, readResponse.getStatusCode());
            ApiResponse readResponseBody = readResponse.getBody();
            assertNotNull(readResponseBody);
            assertEquals(400, readResponseBody.getCode());
        }
    }
}
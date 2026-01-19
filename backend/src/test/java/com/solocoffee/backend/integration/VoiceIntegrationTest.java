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
class VoiceIntegrationTest {

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
    void testVoiceOrder() {
        // 构建语音订单请求
        Map<String, Object> voiceOrderRequest = new HashMap<>();
        voiceOrderRequest.put("customerId", 1L);
        voiceOrderRequest.put("storeId", 1L);
        voiceOrderRequest.put("voiceInput", "我要一杯美式咖啡，中杯，热的");
        voiceOrderRequest.put("language", "zh-CN");
        
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/voice/order", 
                voiceOrderRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("orderId"));
        assertNotNull(data.get("recognizedText"));
        assertNotNull(data.get("orderItems"));
        assertNotNull(data.get("status"));
        assertEquals("success", data.get("status"));
        
        List<Map<String, Object>> orderItems = (List<Map<String, Object>>) data.get("orderItems");
        assertNotNull(orderItems);
        // 验证订单商品列表中的每个商品都有必要的字段
        for (Map<String, Object> item : orderItems) {
            assertNotNull(item.get("productId"));
            assertNotNull(item.get("productName"));
            assertNotNull(item.get("quantity"));
            assertNotNull(item.get("size"));
            assertNotNull(item.get("temperature"));
        }
    }

    @Test
    void testVoiceOrderWithInvalidInput() {
        // 构建无效的语音订单请求（空语音输入）
        Map<String, Object> voiceOrderRequest = new HashMap<>();
        voiceOrderRequest.put("customerId", 1L);
        voiceOrderRequest.put("storeId", 1L);
        voiceOrderRequest.put("voiceInput", "");
        voiceOrderRequest.put("language", "zh-CN");
        
        ResponseEntity<ApiResponse> response = restTemplate.postForEntity(
                baseUrl + "/api/v1/voice/order", 
                voiceOrderRequest, 
                ApiResponse.class);
        
        // 应该返回错误，因为语音输入为空
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(400, responseBody.getCode());
    }

    @Test
    void testGetVoiceCommands() {
        // 获取语音命令列表
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/voice/commands", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("commands"));
        
        List<Map<String, Object>> commands = (List<Map<String, Object>>) data.get("commands");
        assertNotNull(commands);
        assertFalse(commands.isEmpty());
        // 验证命令列表中的每个命令都有必要的字段
        for (Map<String, Object> command : commands) {
            assertNotNull(command.get("command"));
            assertNotNull(command.get("description"));
            assertNotNull(command.get("example"));
        }
    }
}
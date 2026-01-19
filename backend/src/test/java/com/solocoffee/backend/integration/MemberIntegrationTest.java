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
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class MemberIntegrationTest {

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
    void testMemberRegistration() {
        // 构建会员注册请求
        Map<String, Object> memberRequest = new HashMap<>();
        memberRequest.put("phone", "13800138001");
        memberRequest.put("password", "password123");
        memberRequest.put("name", "New Member");
        memberRequest.put("email", "newmember@example.com");
        
        ResponseEntity<ApiResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                memberRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, registerResponse.getStatusCode());
        ApiResponse responseBody = registerResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("id"));
        assertEquals("New Member", data.get("name"));
        assertEquals("13800138001", data.get("phone"));
    }

    @Test
    void testGetMemberDetails() {
        // 先注册会员
        Map<String, Object> memberRequest = new HashMap<>();
        memberRequest.put("phone", "13800138002");
        memberRequest.put("password", "password123");
        memberRequest.put("name", "Test Member");
        memberRequest.put("email", "testmember@example.com");
        
        ResponseEntity<ApiResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                memberRequest, 
                ApiResponse.class);
        
        Map<String, Object> registerData = (Map<String, Object>) registerResponse.getBody().getData();
        Long memberId = Long.valueOf(registerData.get("id").toString());
        
        // 获取会员详情
        ResponseEntity<ApiResponse> detailsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/members/" + memberId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, detailsResponse.getStatusCode());
        ApiResponse responseBody = detailsResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertEquals(memberId, Long.valueOf(data.get("id").toString()));
        assertEquals("Test Member", data.get("name"));
        assertEquals("13800138002", data.get("phone"));
    }

    @Test
    void testUpdateMemberDetails() {
        // 先注册会员
        Map<String, Object> memberRequest = new HashMap<>();
        memberRequest.put("phone", "13800138003");
        memberRequest.put("password", "password123");
        memberRequest.put("name", "Old Name");
        memberRequest.put("email", "old@example.com");
        
        ResponseEntity<ApiResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                memberRequest, 
                ApiResponse.class);
        
        Map<String, Object> registerData = (Map<String, Object>) registerResponse.getBody().getData();
        Long memberId = Long.valueOf(registerData.get("id").toString());
        
        // 更新会员信息
        Map<String, Object> updateRequest = new HashMap<>();
        updateRequest.put("name", "Updated Name");
        updateRequest.put("email", "updated@example.com");
        
        HttpEntity<Map<String, Object>> updateEntity = new HttpEntity<>(updateRequest);
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/members/" + memberId, 
                HttpMethod.PUT, 
                updateEntity, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse responseBody = updateResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        // 验证更新结果
        ResponseEntity<ApiResponse> detailsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/members/" + memberId, 
                ApiResponse.class);
        
        Map<String, Object> data = (Map<String, Object>) detailsResponse.getBody().getData();
        assertEquals("Updated Name", data.get("name"));
        assertEquals("updated@example.com", data.get("email"));
    }

    @Test
    void testMemberPointsManagement() {
        // 先注册会员
        Map<String, Object> memberRequest = new HashMap<>();
        memberRequest.put("phone", "13800138004");
        memberRequest.put("password", "password123");
        memberRequest.put("name", "Points Member");
        memberRequest.put("email", "points@example.com");
        
        ResponseEntity<ApiResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                memberRequest, 
                ApiResponse.class);
        
        Map<String, Object> registerData = (Map<String, Object>) registerResponse.getBody().getData();
        Long memberId = Long.valueOf(registerData.get("id").toString());
        
        // 添加积分
        Map<String, Object> pointsRequest = new HashMap<>();
        pointsRequest.put("points", 100);
        pointsRequest.put("reason", "Test points addition");
        
        ResponseEntity<ApiResponse> addPointsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members/" + memberId + "/points", 
                pointsRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, addPointsResponse.getStatusCode());
        ApiResponse responseBody = addPointsResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        // 查询会员积分
        ResponseEntity<ApiResponse> pointsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/members/" + memberId + "/points", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, pointsResponse.getStatusCode());
        ApiResponse pointsResponseBody = pointsResponse.getBody();
        assertNotNull(pointsResponseBody);
        assertEquals(200, pointsResponseBody.getCode());
    }

    @Test
    void testMemberLevelCalculation() {
        // 先注册会员
        Map<String, Object> memberRequest = new HashMap<>();
        memberRequest.put("phone", "13800138005");
        memberRequest.put("password", "password123");
        memberRequest.put("name", "Level Member");
        memberRequest.put("email", "level@example.com");
        
        ResponseEntity<ApiResponse> registerResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/members", 
                memberRequest, 
                ApiResponse.class);
        
        Map<String, Object> registerData = (Map<String, Object>) registerResponse.getBody().getData();
        Long memberId = Long.valueOf(registerData.get("id").toString());
        
        // 添加大量积分以触发等级变化
        Map<String, Object> pointsRequest = new HashMap<>();
        pointsRequest.put("points", 1000);
        pointsRequest.put("reason", "Test level upgrade");
        
        restTemplate.postForEntity(
                baseUrl + "/api/v1/members/" + memberId + "/points", 
                pointsRequest, 
                ApiResponse.class);
        
        // 验证会员等级
        ResponseEntity<ApiResponse> detailsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/members/" + memberId, 
                ApiResponse.class);
        
        Map<String, Object> data = (Map<String, Object>) detailsResponse.getBody().getData();
        assertNotNull(data.get("level"));
    }
}

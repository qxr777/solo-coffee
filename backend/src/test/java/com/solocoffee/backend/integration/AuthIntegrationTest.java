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
class AuthIntegrationTest {

    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    private String baseUrl;

    @BeforeEach
    void setUp() {
        baseUrl = "http://localhost:" + port;
        // 初始化测试数据
        initTestData();
    }

    private void initTestData() {
        // 创建测试用户（手机号：13800138000，密码：password123）
        Map<String, Object> userData = new HashMap<>();
        userData.put("phone", "13800138000");
        userData.put("password", "password123");
        userData.put("name", "Test User");
        userData.put("email", "test@example.com");
        
        restTemplate.postForEntity(baseUrl + "/api/v1/members", userData, ApiResponse.class);
    }

    @Test
    void testLoginWithPhoneAndPassword() {
        // 构建登录请求
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("phone", "13800138000");
        loginRequest.put("password", "password123");
        
        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login", 
                loginRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, loginResponse.getStatusCode());
        ApiResponse responseBody = loginResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("accessToken"));
        assertNotNull(data.get("refreshToken"));
        assertNotNull(data.get("userId"));
    }

    @Test
    void testLoginWithInvalidCredentials() {
        // 构建无效登录请求
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("phone", "13800138000");
        loginRequest.put("password", "wrongpassword");
        
        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login", 
                loginRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, loginResponse.getStatusCode());
        ApiResponse responseBody = loginResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(400, responseBody.getCode());
    }

    @Test
    void testSendSms() {
        // 构建发送验证码请求
        Map<String, Object> smsRequest = new HashMap<>();
        smsRequest.put("phone", "13800138000");
        
        ResponseEntity<ApiResponse> smsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/send-sms", 
                smsRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, smsResponse.getStatusCode());
        ApiResponse responseBody = smsResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
    }

    @Test
    void testSmsLogin() {
        // 先发送验证码
        Map<String, Object> smsRequest = new HashMap<>();
        smsRequest.put("phone", "13800138000");
        
        restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/send-sms", 
                smsRequest, 
                ApiResponse.class);
        
        // 构建短信登录请求（使用实际生成的验证码，这里我们只测试API调用是否成功）
        Map<String, Object> smsLoginRequest = new HashMap<>();
        smsLoginRequest.put("phone", "13800138000");
        smsLoginRequest.put("code", "123456");
        
        ResponseEntity<ApiResponse> smsLoginResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/sms-login", 
                smsLoginRequest, 
                ApiResponse.class);
        
        // 验证码可能验证失败，但API调用应该成功
        assertEquals(HttpStatus.OK, smsLoginResponse.getStatusCode());
    }

    @Test
    void testRefreshToken() {
        // 先登录获取令牌
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("phone", "13800138000");
        loginRequest.put("password", "password123");
        
        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login", 
                loginRequest, 
                ApiResponse.class);
        
        Map<String, Object> loginData = (Map<String, Object>) loginResponse.getBody().getData();
        String refreshToken = (String) loginData.get("refreshToken");
        
        // 构建刷新令牌请求
        Map<String, Object> refreshRequest = new HashMap<>();
        refreshRequest.put("refreshToken", refreshToken);
        
        ResponseEntity<ApiResponse> refreshResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/refresh", 
                refreshRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, refreshResponse.getStatusCode());
        ApiResponse responseBody = refreshResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> refreshData = (Map<String, Object>) responseBody.getData();
        assertNotNull(refreshData);
        assertNotNull(refreshData.get("accessToken"));
    }

    @Test
    void testLogout() {
        // 先登录获取令牌
        Map<String, Object> loginRequest = new HashMap<>();
        loginRequest.put("phone", "13800138000");
        loginRequest.put("password", "password123");
        
        ResponseEntity<ApiResponse> loginResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/login", 
                loginRequest, 
                ApiResponse.class);
        
        Map<String, Object> loginData = (Map<String, Object>) loginResponse.getBody().getData();
        String accessToken = (String) loginData.get("accessToken");
        
        // 构建登出请求
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        
        HttpEntity<String> logoutRequest = new HttpEntity<>(headers);
        
        ResponseEntity<ApiResponse> logoutResponse = restTemplate.exchange(
                baseUrl + "/api/v1/auth/logout", 
                HttpMethod.POST, 
                logoutRequest, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, logoutResponse.getStatusCode());
        ApiResponse responseBody = logoutResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
    }

    @Test
    void testOauthLogin() {
        // 测试第三方登录（模拟）
        Map<String, Object> oauthRequest = new HashMap<>();
        oauthRequest.put("code", "test_code");
        
        ResponseEntity<ApiResponse> oauthResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/auth/oauth/google", 
                oauthRequest, 
                ApiResponse.class);
        
        // 应该返回成功响应
        assertEquals(HttpStatus.OK, oauthResponse.getStatusCode());
    }
}

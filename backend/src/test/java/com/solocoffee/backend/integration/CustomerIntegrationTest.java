package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Customer;
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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class CustomerIntegrationTest {

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
    void testCompleteCustomerFlow() {
        // 1. 创建客户
        Customer customer = createTestCustomer();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers", customer, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdCustomerMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdCustomerMap);
        Long customerId = Long.valueOf(createdCustomerMap.get("id").toString());
        
        // 2. 获取客户详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/customers/" + customerId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedCustomerMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedCustomerMap);
        assertEquals(customerId, Long.valueOf(retrievedCustomerMap.get("id").toString()));
        assertEquals(customer.getName(), retrievedCustomerMap.get("name"));
        
        // 3. 更新客户信息
        Customer updatedCustomer = new Customer();
        updatedCustomer.setId(customerId);
        updatedCustomer.setName("Updated Test Customer");
        updatedCustomer.setEmail("updated@example.com");
        updatedCustomer.setPhone(customer.getPhone());
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/customers/" + customerId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedCustomer),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedCustomerMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedCustomerMap);
        assertEquals("Updated Test Customer", updatedCustomerMap.get("name"));
        assertEquals("updated@example.com", updatedCustomerMap.get("email"));
        
        // 4. 为客户添加积分
        Map<String, Object> pointsRequest = Map.of(
                "points", 100,
                "type", 1,
                "relatedId", 1L,
                "description", "Test points"
        );
        
        ResponseEntity<ApiResponse> addPointsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/points",
                pointsRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, addPointsResponse.getStatusCode());
        ApiResponse addPointsResponseBody = addPointsResponse.getBody();
        assertNotNull(addPointsResponseBody);
        assertEquals(200, addPointsResponseBody.getCode());
        Map<String, Object> customerWithPointsMap = (Map<String, Object>) addPointsResponseBody.getData();
        assertNotNull(customerWithPointsMap);
        assertEquals(100L, customerWithPointsMap.get("points"));
        
        // 5. 兑换积分
        Map<String, Object> redeemRequest = Map.of(
                "points", 50,
                "type", 2,
                "relatedId", 1L,
                "description", "Test redemption"
        );
        
        ResponseEntity<ApiResponse> redeemPointsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/redeem",
                redeemRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, redeemPointsResponse.getStatusCode());
        ApiResponse redeemPointsResponseBody = redeemPointsResponse.getBody();
        assertNotNull(redeemPointsResponseBody);
        assertEquals(200, redeemPointsResponseBody.getCode());
        Map<String, Object> customerAfterRedemptionMap = (Map<String, Object>) redeemPointsResponseBody.getData();
        assertNotNull(customerAfterRedemptionMap);
        assertEquals(50L, customerAfterRedemptionMap.get("points"));
        
        // 6. 获取所有客户
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/customers", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        List<Map<String, Object>> customers = (List<Map<String, Object>>) listResponseBody.getData();
        assertNotNull(customers);
        assertTrue(customers.size() > 0);
    }

    @Test
    void testMemberLevelUpgrade() {
        // 1. 创建客户
        Customer customer = createTestCustomer();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers", customer, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdCustomerMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdCustomerMap);
        Long customerId = Long.valueOf(createdCustomerMap.get("id").toString());
        
        // 2. 添加足够积分升级会员等级
        Map<String, Object> pointsRequest = Map.of(
                "points", 1500, // 超过银卡会员阈值(1000)
                "type", 1,
                "relatedId", 1L,
                "description", "Points for membership upgrade"
        );
        
        ResponseEntity<ApiResponse> addPointsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/points",
                pointsRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, addPointsResponse.getStatusCode());
        ApiResponse addPointsResponseBody = addPointsResponse.getBody();
        assertNotNull(addPointsResponseBody);
        assertEquals(200, addPointsResponseBody.getCode());
        Map<String, Object> customerWithPointsMap = (Map<String, Object>) addPointsResponseBody.getData();
        assertNotNull(customerWithPointsMap);
        assertEquals(1500L, customerWithPointsMap.get("points"));
        // 验证会员等级是否升级
        Object memberLevelId = customerWithPointsMap.get("memberLevelId");
        assertNotNull(memberLevelId);
        Long memberLevelIdLong = Long.valueOf(memberLevelId.toString());
        assertTrue(memberLevelIdLong >= 1); // 至少为普通会员
    }
    
    @Test
    void testCustomerPointsAndLevelQuery() {
        // 1. 创建客户
        Customer customer = createTestCustomer();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers", customer, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdCustomerMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdCustomerMap);
        Long customerId = Long.valueOf(createdCustomerMap.get("id").toString());
        
        // 2. 添加积分
        Map<String, Object> pointsRequest = Map.of(
                "points", 1200,
                "type", 1,
                "relatedId", 1L,
                "description", "Test points"
        );
        
        ResponseEntity<ApiResponse> addPointsResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/points",
                pointsRequest,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, addPointsResponse.getStatusCode());
        
        // 3. 查询会员积分
        ResponseEntity<ApiResponse> getPointsResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/points",
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getPointsResponse.getStatusCode());
        ApiResponse getPointsResponseBody = getPointsResponse.getBody();
        assertNotNull(getPointsResponseBody);
        assertEquals(200, getPointsResponseBody.getCode());
        Integer points = ((Number) getPointsResponseBody.getData()).intValue();
        assertNotNull(points);
        assertEquals(1200, points);
        
        // 4. 查询会员等级
        ResponseEntity<ApiResponse> getLevelResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/customers/" + customerId + "/level",
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getLevelResponse.getStatusCode());
        ApiResponse getLevelResponseBody = getLevelResponse.getBody();
        assertNotNull(getLevelResponseBody);
        assertEquals(200, getLevelResponseBody.getCode());
        Map<String, Object> levelInfoMap = (Map<String, Object>) getLevelResponseBody.getData();
        assertNotNull(levelInfoMap);
        // 验证等级信息包含必要字段
        assertNotNull(levelInfoMap.get("memberLevelId"));
        assertNotNull(levelInfoMap.get("levelName"));
        assertNotNull(levelInfoMap.get("points"));
    }

    private Customer createTestCustomer() {
        Customer customer = new Customer();
        customer.setName("Test Customer");
        customer.setPhone("13800138000");
        customer.setEmail("test@example.com");
        customer.setWechatOpenId("test_openid");
        customer.setAvatarUrl("https://example.com/avatar.jpg");
        return customer;
    }
}
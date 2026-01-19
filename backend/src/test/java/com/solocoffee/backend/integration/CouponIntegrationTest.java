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

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class CouponIntegrationTest {

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
    void testGetAvailableCoupons() {
        // 获取可用优惠券列表
        ResponseEntity<ApiResponse> response = restTemplate.getForEntity(
                baseUrl + "/api/v1/coupons/available?customerId=1&storeId=1", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        ApiResponse responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        
        Map<String, Object> data = (Map<String, Object>) responseBody.getData();
        assertNotNull(data);
        assertNotNull(data.get("coupons"));
        
        List<Map<String, Object>> coupons = (List<Map<String, Object>>) data.get("coupons");
        assertNotNull(coupons);
        // 验证优惠券列表中的每个优惠券都有必要的字段
        for (Map<String, Object> coupon : coupons) {
            assertNotNull(coupon.get("couponId"));
            assertNotNull(coupon.get("name"));
            assertNotNull(coupon.get("type"));
            assertNotNull(coupon.get("value"));
            assertNotNull(coupon.get("minSpend"));
            assertNotNull(coupon.get("startDate"));
            assertNotNull(coupon.get("endDate"));
            assertNotNull(coupon.get("status"));
        }
    }

    @Test
    void testGetCouponDetails() {
        // 1. 先获取可用优惠券列表，找到一个优惠券ID
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/coupons/available?customerId=1&storeId=1", 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        Map<String, Object> listData = (Map<String, Object>) listResponse.getBody().getData();
        List<Map<String, Object>> coupons = (List<Map<String, Object>>) listData.get("coupons");
        
        // 如果有优惠券，测试获取优惠券详情
        if (!coupons.isEmpty()) {
            Map<String, Object> firstCoupon = coupons.get(0);
            Long couponId = Long.valueOf(firstCoupon.get("couponId").toString());
            
            // 2. 获取优惠券详情
            ResponseEntity<ApiResponse> detailResponse = restTemplate.getForEntity(
                    baseUrl + "/api/v1/coupons/" + couponId, 
                    ApiResponse.class);
            
            assertEquals(HttpStatus.OK, detailResponse.getStatusCode());
            ApiResponse detailResponseBody = detailResponse.getBody();
            assertNotNull(detailResponseBody);
            assertEquals(200, detailResponseBody.getCode());
            
            Map<String, Object> detailData = (Map<String, Object>) detailResponseBody.getData();
            assertNotNull(detailData);
            assertNotNull(detailData.get("couponId"));
            assertNotNull(detailData.get("name"));
            assertNotNull(detailData.get("description"));
            assertNotNull(detailData.get("type"));
            assertNotNull(detailData.get("value"));
            assertNotNull(detailData.get("minSpend"));
            assertNotNull(detailData.get("maxDiscount"));
            assertNotNull(detailData.get("startDate"));
            assertNotNull(detailData.get("endDate"));
            assertNotNull(detailData.get("usageLimit"));
            assertNotNull(detailData.get("usageCount"));
            assertNotNull(detailData.get("status"));
            assertNotNull(detailData.get("applicableProducts"));
            assertNotNull(detailData.get("applicableStores"));
        }
    }
}
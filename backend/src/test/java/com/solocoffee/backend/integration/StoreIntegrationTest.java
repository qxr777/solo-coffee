package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Store;
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

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class StoreIntegrationTest {

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
    void testCompleteStoreFlow() {
        // 1. 创建门店
        Store store = createTestStore();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/stores", store, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdStoreMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdStoreMap);
        Long storeId = Long.valueOf(createdStoreMap.get("id").toString());
        
        // 2. 获取门店详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/stores/" + storeId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedStoreMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedStoreMap);
        assertEquals(storeId, Long.valueOf(retrievedStoreMap.get("id").toString()));
        assertEquals(store.getName(), retrievedStoreMap.get("name"));
        
        // 3. 更新门店
        Store updatedStore = new Store();
        updatedStore.setName("Updated Test Store");
        updatedStore.setAddress("Updated Address");
        updatedStore.setPhone("13900139000");
        updatedStore.setEmail("updated@example.com");
        updatedStore.setBusinessHours("09:00-22:00");
        updatedStore.setManagerId(1L);
        updatedStore.setStatus(1);
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/stores/" + storeId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedStore),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedStoreMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedStoreMap);
        assertEquals("Updated Test Store", updatedStoreMap.get("name"));
        assertEquals("Updated Address", updatedStoreMap.get("address"));
        
        // 4. 获取所有门店
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/stores", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        
        // 5. 删除门店
        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                baseUrl + "/api/v1/stores/" + storeId,
                HttpMethod.DELETE,
                null,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ApiResponse deleteResponseBody = deleteResponse.getBody();
        assertNotNull(deleteResponseBody);
        assertEquals(200, deleteResponseBody.getCode());
    }

    private Store createTestStore() {
        Store store = new Store();
        store.setName("Test Store");
        store.setAddress("Test Address");
        store.setPhone("13800138000");
        store.setEmail("test@example.com");
        store.setBusinessHours("08:00-21:00");
        store.setManagerId(1L);
        store.setStatus(1); // 营业中
        return store;
    }
}
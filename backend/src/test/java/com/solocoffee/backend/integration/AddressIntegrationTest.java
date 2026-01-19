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
class AddressIntegrationTest {

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
    void testCompleteAddressFlow() {
        // 1. 创建地址
        Map<String, Object> addressData = createTestAddressData();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/addresses", 
                addressData, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse createResponseBody = createResponse.getBody();
        assertNotNull(createResponseBody);
        assertEquals(200, createResponseBody.getCode());
        Map<String, Object> createdAddress = (Map<String, Object>) createResponseBody.getData();
        assertNotNull(createdAddress);
        Long addressId = Long.valueOf(createdAddress.get("addressId").toString());
        
        // 2. 获取地址详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/addresses/" + addressId + "?customerId=" + testCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedAddress = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedAddress);
        assertEquals(addressId, retrievedAddress.get("addressId"));
        assertEquals("John Doe", retrievedAddress.get("name"));
        
        // 3. 更新地址
        Map<String, Object> updateData = new HashMap<>();
        updateData.put("customerId", testCustomerId);
        updateData.put("name", "John Updated");
        updateData.put("phone", "13900139000");
        updateData.put("province", "北京市");
        updateData.put("city", "北京市");
        updateData.put("district", "海淀区");
        updateData.put("detailAddress", "更新后的详细地址");
        updateData.put("isDefault", true);
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/addresses/" + addressId,
                HttpMethod.PUT,
                new HttpEntity<>(updateData),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedAddress = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedAddress);
        assertEquals("John Updated", updatedAddress.get("name"));
        assertEquals("13900139000", updatedAddress.get("phone"));
        assertEquals(true, updatedAddress.get("isDefault"));
        
        // 4. 获取客户地址列表
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/addresses/customer/" + testCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        List<Map<String, Object>> addressList = (List<Map<String, Object>>) listResponseBody.getData();
        assertNotNull(addressList);
        assertFalse(addressList.isEmpty());
        
        // 5. 删除地址
        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                baseUrl + "/api/v1/addresses/" + addressId + "?customerId=" + testCustomerId,
                HttpMethod.DELETE,
                null,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ApiResponse deleteResponseBody = deleteResponse.getBody();
        assertNotNull(deleteResponseBody);
        assertEquals(200, deleteResponseBody.getCode());
        
        // 6. 验证地址已删除
        ResponseEntity<ApiResponse> listAfterDeleteResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/addresses/customer/" + testCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listAfterDeleteResponse.getStatusCode());
        ApiResponse listAfterDeleteResponseBody = listAfterDeleteResponse.getBody();
        assertNotNull(listAfterDeleteResponseBody);
        List<Map<String, Object>> addressListAfterDelete = (List<Map<String, Object>>) listAfterDeleteResponseBody.getData();
        assertNotNull(addressListAfterDelete);
        assertTrue(addressListAfterDelete.isEmpty());
    }

    @Test
    void testDefaultAddressManagement() {
        // 1. 创建第一个地址（非默认）
        Map<String, Object> addressData1 = createTestAddressData();
        addressData1.put("isDefault", false);
        addressData1.put("name", "Address 1");
        
        ResponseEntity<ApiResponse> createResponse1 = restTemplate.postForEntity(
                baseUrl + "/api/v1/addresses", 
                addressData1, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse1.getStatusCode());
        Map<String, Object> createdAddress1 = (Map<String, Object>) createResponse1.getBody().getData();
        Long addressId1 = Long.valueOf(createdAddress1.get("addressId").toString());
        
        // 2. 创建第二个地址（设为默认）
        Map<String, Object> addressData2 = createTestAddressData();
        addressData2.put("isDefault", true);
        addressData2.put("name", "Address 2");
        addressData2.put("detailAddress", "详细地址2");
        
        ResponseEntity<ApiResponse> createResponse2 = restTemplate.postForEntity(
                baseUrl + "/api/v1/addresses", 
                addressData2, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse2.getStatusCode());
        Map<String, Object> createdAddress2 = (Map<String, Object>) createResponse2.getBody().getData();
        Long addressId2 = Long.valueOf(createdAddress2.get("addressId").toString());
        
        // 3. 验证地址2是默认，地址1不是默认
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/addresses/customer/" + testCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        List<Map<String, Object>> addressList = (List<Map<String, Object>>) listResponse.getBody().getData();
        assertNotNull(addressList);
        assertEquals(2, addressList.size());
        
        // 检查默认地址状态
        boolean foundDefault = false;
        for (Map<String, Object> address : addressList) {
            if (address.get("addressId").equals(addressId2)) {
                assertEquals(true, address.get("isDefault"));
                foundDefault = true;
            } else if (address.get("addressId").equals(addressId1)) {
                assertEquals(false, address.get("isDefault"));
            }
        }
        assertTrue(foundDefault);
    }

    @Test
    void testAddressPermissionValidation() {
        // 1. 创建测试地址
        Map<String, Object> addressData = createTestAddressData();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/addresses", 
                addressData, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        Map<String, Object> createdAddress = (Map<String, Object>) createResponse.getBody().getData();
        Long addressId = Long.valueOf(createdAddress.get("addressId").toString());
        
        // 2. 尝试使用其他客户ID获取地址详情（应该失败）
        Long otherCustomerId = testCustomerId + 1;
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/addresses/" + addressId + "?customerId=" + otherCustomerId, 
                ApiResponse.class);
        
        assertEquals(HttpStatus.BAD_REQUEST, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(400, getResponseBody.getCode());
    }

    private Map<String, Object> createTestAddressData() {
        Map<String, Object> addressData = new HashMap<>();
        addressData.put("customerId", testCustomerId);
        addressData.put("name", "John Doe");
        addressData.put("phone", "13800138000");
        addressData.put("province", "北京市");
        addressData.put("city", "北京市");
        addressData.put("district", "朝阳区");
        addressData.put("detailAddress", "详细地址");
        addressData.put("isDefault", false);
        return addressData;
    }
}
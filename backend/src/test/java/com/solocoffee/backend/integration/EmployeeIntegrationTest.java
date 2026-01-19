package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Employee;
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

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@Import(TestSecurityConfig.class)
class EmployeeIntegrationTest {

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
    void testCompleteEmployeeFlow() {
        // 1. 创建员工
        Employee employee = createTestEmployee();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/employees", employee, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdEmployeeMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdEmployeeMap);
        Long employeeId = Long.valueOf(createdEmployeeMap.get("id").toString());
        
        // 2. 获取员工详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/employees/" + employeeId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedEmployeeMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedEmployeeMap);
        assertEquals(employeeId, Long.valueOf(retrievedEmployeeMap.get("id").toString()));
        assertEquals(employee.getName(), retrievedEmployeeMap.get("name"));
        
        // 3. 更新员工
        Employee updatedEmployee = new Employee();
        updatedEmployee.setName("Updated Test Employee");
        updatedEmployee.setPhone("13900139000");
        updatedEmployee.setEmail("updated@example.com");
        updatedEmployee.setPosition("Senior Barista");
        updatedEmployee.setStatus(1);
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/employees/" + employeeId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedEmployee),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedEmployeeMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedEmployeeMap);
        assertEquals("Updated Test Employee", updatedEmployeeMap.get("name"));
        assertEquals("13900139000", updatedEmployeeMap.get("phone"));
        
        // 4. 获取所有员工
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/employees", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        
        // 5. 删除员工
        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                baseUrl + "/api/v1/employees/" + employeeId,
                HttpMethod.DELETE,
                null,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ApiResponse deleteResponseBody = deleteResponse.getBody();
        assertNotNull(deleteResponseBody);
        assertEquals(200, deleteResponseBody.getCode());
    }

    private Employee createTestEmployee() {
        Employee employee = new Employee();
        employee.setStoreId(1L);
        employee.setName("Test Employee");
        employee.setPhone("13800138000");
        employee.setEmail("test@example.com");
        employee.setPosition("Barista");
        employee.setEmployeeId("EMP" + System.currentTimeMillis());
        employee.setHireDate(LocalDateTime.now());
        employee.setStatus(1); // 在职
        return employee;
    }
}
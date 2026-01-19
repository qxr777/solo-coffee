package com.solocoffee.backend.integration;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Category;
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
class CategoryIntegrationTest {

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
    void testCompleteCategoryFlow() {
        // 1. 创建分类
        Category category = createTestCategory();
        ResponseEntity<ApiResponse> createResponse = restTemplate.postForEntity(
                baseUrl + "/api/v1/categories", category, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        ApiResponse responseBody = createResponse.getBody();
        assertNotNull(responseBody);
        assertEquals(200, responseBody.getCode());
        Map<String, Object> createdCategoryMap = (Map<String, Object>) responseBody.getData();
        assertNotNull(createdCategoryMap);
        Long categoryId = Long.valueOf(createdCategoryMap.get("id").toString());
        
        // 2. 获取分类详情
        ResponseEntity<ApiResponse> getResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/categories/" + categoryId, ApiResponse.class);
        
        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        ApiResponse getResponseBody = getResponse.getBody();
        assertNotNull(getResponseBody);
        assertEquals(200, getResponseBody.getCode());
        Map<String, Object> retrievedCategoryMap = (Map<String, Object>) getResponseBody.getData();
        assertNotNull(retrievedCategoryMap);
        assertEquals(categoryId, Long.valueOf(retrievedCategoryMap.get("id").toString()));
        assertEquals(category.getCategoryName(), retrievedCategoryMap.get("categoryName"));
        
        // 3. 更新分类
        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Updated Test Category");
        updatedCategory.setDescription("Updated description");
        updatedCategory.setSortOrder(2);
        
        ResponseEntity<ApiResponse> updateResponse = restTemplate.exchange(
                baseUrl + "/api/v1/categories/" + categoryId,
                HttpMethod.PUT,
                new HttpEntity<>(updatedCategory),
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, updateResponse.getStatusCode());
        ApiResponse updateResponseBody = updateResponse.getBody();
        assertNotNull(updateResponseBody);
        assertEquals(200, updateResponseBody.getCode());
        Map<String, Object> updatedCategoryMap = (Map<String, Object>) updateResponseBody.getData();
        assertNotNull(updatedCategoryMap);
        assertEquals("Updated Test Category", updatedCategoryMap.get("categoryName"));
        assertEquals("Updated description", updatedCategoryMap.get("description"));
        
        // 4. 获取所有分类
        ResponseEntity<ApiResponse> listResponse = restTemplate.getForEntity(
                baseUrl + "/api/v1/categories", ApiResponse.class);
        
        assertEquals(HttpStatus.OK, listResponse.getStatusCode());
        ApiResponse listResponseBody = listResponse.getBody();
        assertNotNull(listResponseBody);
        assertEquals(200, listResponseBody.getCode());
        
        // 5. 删除分类
        ResponseEntity<ApiResponse> deleteResponse = restTemplate.exchange(
                baseUrl + "/api/v1/categories/" + categoryId,
                HttpMethod.DELETE,
                null,
                ApiResponse.class);
        
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ApiResponse deleteResponseBody = deleteResponse.getBody();
        assertNotNull(deleteResponseBody);
        assertEquals(200, deleteResponseBody.getCode());
    }

    private Category createTestCategory() {
        Category category = new Category();
        category.setCategoryName("Test Category");
        category.setDescription("Test description");
        category.setSortOrder(1);
        return category;
    }
}
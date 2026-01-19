package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Category;
import com.solocoffee.backend.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {
    
    private static final Logger logger = LoggerFactory.getLogger(CategoryController.class);
    
    @Autowired
    private CategoryService categoryService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Category>> createCategory(@RequestBody Category category) {
        logger.debug("开始创建分类: {}", category);
        try {
            Category createdCategory = categoryService.createCategory(category);
            logger.debug("分类创建成功: {}", createdCategory);
            return ResponseEntity.ok(ApiResponse.success("分类创建成功", createdCategory));
        } catch (Exception e) {
            logger.error("分类创建失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("分类创建失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> getCategoryById(@PathVariable Long id) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(category.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("分类不存在"));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success(categories));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Category>> updateCategory(@PathVariable Long id, @RequestBody Category category) {
        logger.debug("开始更新分类，ID: {}, 分类数据: {}", id, category);
        try {
            Category updatedCategory = categoryService.updateCategory(id, category);
            if (updatedCategory != null) {
                logger.debug("分类更新成功: {}", updatedCategory);
                return ResponseEntity.ok(ApiResponse.success("分类更新成功", updatedCategory));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("分类不存在"));
            }
        } catch (Exception e) {
            logger.error("分类更新失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("分类更新失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCategory(@PathVariable Long id) {
        logger.debug("开始删除分类，ID: {}", id);
        try {
            categoryService.deleteCategory(id);
            logger.debug("分类删除成功，ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success("分类删除成功", null));
        } catch (Exception e) {
            logger.error("分类删除失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("分类删除失败: " + e.getMessage()));
        }
    }
}
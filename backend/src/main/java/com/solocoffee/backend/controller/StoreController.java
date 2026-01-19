package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Store;
import com.solocoffee.backend.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/stores")
public class StoreController {
    
    private static final Logger logger = LoggerFactory.getLogger(StoreController.class);
    
    @Autowired
    private StoreService storeService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Store>> createStore(@RequestBody Store store) {
        logger.debug("开始创建门店: {}", store);
        try {
            Store createdStore = storeService.createStore(store);
            logger.debug("门店创建成功: {}", createdStore);
            return ResponseEntity.ok(ApiResponse.success("门店创建成功", createdStore));
        } catch (Exception e) {
            logger.error("门店创建失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("门店创建失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> getStoreById(@PathVariable Long id) {
        try {
            Map<String, Object> storeDetails = storeService.getStoreDetails(id);
            if (storeDetails != null) {
                return ResponseEntity.ok(ApiResponse.success(storeDetails));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("门店不存在"));
            }
        } catch (Exception e) {
            logger.error("获取门店详情失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Store>>> getAllStores() {
        List<Store> stores = storeService.getAllStores();
        return ResponseEntity.ok(ApiResponse.success(stores));
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<ApiResponse<?>> getNearbyStores(@RequestParam double latitude, @RequestParam double longitude,
                                                        @RequestParam(required = false, defaultValue = "5000") int radius,
                                                        @RequestParam(required = false, defaultValue = "1") int page,
                                                        @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            Map<String, Object> response = storeService.getNearbyStores(latitude, longitude, radius, page, size);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("获取附近门店失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<?>> searchStores(@RequestParam String keyword,
                                                     @RequestParam(required = false) Double latitude,
                                                     @RequestParam(required = false) Double longitude,
                                                     @RequestParam(required = false, defaultValue = "1") int page,
                                                     @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            Map<String, Object> response = storeService.searchStores(keyword, latitude, longitude, page, size);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("搜索门店失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PostMapping("/{storeId}/favorite")
    public ResponseEntity<ApiResponse<?>> favoriteStore(@PathVariable Long storeId, @RequestBody Map<String, Object> request) {
        try {
            Boolean isFavorite = (Boolean) request.get("isFavorite");
            Map<String, Object> response = storeService.favoriteStore(storeId, isFavorite);
            return ResponseEntity.ok(ApiResponse.success("操作成功", response));
        } catch (RuntimeException e) {
            logger.error("收藏门店失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(ApiResponse.badRequest(e.getMessage()));
        } catch (Exception e) {
            logger.error("收藏门店系统错误: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<?>> getFavoriteStores(@RequestParam(required = false, defaultValue = "1") int page,
                                                          @RequestParam(required = false, defaultValue = "10") int size) {
        try {
            Map<String, Object> response = storeService.getFavoriteStores(page, size);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            logger.error("获取收藏门店失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Store>> updateStore(@PathVariable Long id, @RequestBody Store store) {
        logger.debug("开始更新门店，ID: {}, 门店数据: {}", id, store);
        try {
            Store updatedStore = storeService.updateStore(id, store);
            if (updatedStore != null) {
                logger.debug("门店更新成功: {}", updatedStore);
                return ResponseEntity.ok(ApiResponse.success("门店更新成功", updatedStore));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("门店不存在"));
            }
        } catch (Exception e) {
            logger.error("门店更新失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("门店更新失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(@PathVariable Long id) {
        logger.debug("开始删除门店，ID: {}", id);
        try {
            storeService.deleteStore(id);
            logger.debug("门店删除成功，ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success("门店删除成功", null));
        } catch (Exception e) {
            logger.error("门店删除失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("门店删除失败: " + e.getMessage()));
        }
    }
}
package com.solocoffee.backend.controller;

import org.springframework.web.bind.annotation.*;
import com.solocoffee.backend.service.AddressService;
import com.solocoffee.backend.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {
    
    private static final Logger logger = LoggerFactory.getLogger(AddressController.class);
    
    @Autowired
    private AddressService addressService;
    
    /**
     * 创建地址
     * POST /api/v1/addresses
     */
    @PostMapping
    public ResponseEntity<ApiResponse<?>> createAddress(
            @RequestBody Map<String, Object> addressData) {
        
        Long customerId = Long.valueOf(addressData.get("customerId").toString());
        String name = (String) addressData.get("name");
        String phone = (String) addressData.get("phone");
        String province = (String) addressData.get("province");
        String city = (String) addressData.get("city");
        String district = (String) addressData.get("district");
        String detailAddress = (String) addressData.get("detailAddress");
        Boolean isDefault = addressData.containsKey("isDefault") ? (Boolean) addressData.get("isDefault") : false;
        
        logger.info("创建地址请求，客户ID: {}", customerId);
        
        Map<String, Object> result = addressService.createAddress(
                customerId, name, phone, province, city, district, detailAddress, isDefault);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("地址创建成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 更新地址
     * PUT /api/v1/addresses/{addressId}
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<?>> updateAddress(
            @PathVariable("addressId") Long addressId,
            @RequestBody Map<String, Object> addressData) {
        
        Long customerId = Long.valueOf(addressData.get("customerId").toString());
        String name = (String) addressData.get("name");
        String phone = (String) addressData.get("phone");
        String province = (String) addressData.get("province");
        String city = (String) addressData.get("city");
        String district = (String) addressData.get("district");
        String detailAddress = (String) addressData.get("detailAddress");
        Boolean isDefault = addressData.containsKey("isDefault") ? (Boolean) addressData.get("isDefault") : false;
        
        logger.info("更新地址请求，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = addressService.updateAddress(
                addressId, name, phone, province, city, district, detailAddress, isDefault, customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("地址更新成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 删除地址
     * DELETE /api/v1/addresses/{addressId}
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<?>> deleteAddress(
            @PathVariable("addressId") Long addressId,
            @RequestParam("customerId") Long customerId) {
        
        logger.info("删除地址请求，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = addressService.deleteAddress(addressId, customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("地址删除成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
    
    /**
     * 获取客户地址列表
     * GET /api/v1/addresses/customer/{customerId}
     */
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<?>> getCustomerAddresses(
            @PathVariable("customerId") Long customerId) {
        
        logger.info("获取客户地址列表请求，客户ID: {}", customerId);
        
        List<Map<String, Object>> addresses = addressService.getCustomerAddresses(customerId);
        
        return ResponseEntity.ok(ApiResponse.success("地址列表查询成功", addresses));
    }
    
    /**
     * 获取地址详情
     * GET /api/v1/addresses/{addressId}
     */
    @GetMapping("/{addressId}")
    public ResponseEntity<ApiResponse<?>> getAddressDetails(
            @PathVariable("addressId") Long addressId,
            @RequestParam("customerId") Long customerId) {
        
        logger.info("获取地址详情请求，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = addressService.getAddressDetails(addressId, customerId);
        
        if ((Boolean) result.get("success")) {
            return ResponseEntity.ok(ApiResponse.success("地址详情查询成功", result));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest((String) result.get("message")));
        }
    }
}
package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Customer;
import com.solocoffee.backend.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/customers")
public class CustomerController {
    
    @Autowired
    private CustomerService customerService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Customer>> createCustomer(@RequestBody Customer customer) {
        Customer createdCustomer = customerService.createCustomer(customer);
        return ResponseEntity.ok(ApiResponse.success("顾客创建成功", createdCustomer));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> getCustomerById(@PathVariable Long id) {
        Optional<Customer> customer = customerService.getCustomerById(id);
        return customer.map(c -> ResponseEntity.ok(ApiResponse.success(c)))
                   .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在")));
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<List<Customer>>> getAllCustomers() {
        List<Customer> customers = customerService.getAllCustomers();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Customer>> updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        if (customer == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("顾客信息不能为空"));
        }
        customer.setId(id);
        Customer updatedCustomer = customerService.updateCustomer(customer);
        return updatedCustomer != null ? ResponseEntity.ok(ApiResponse.success("顾客更新成功", updatedCustomer))
                                   : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在"));
    }
    
    @PostMapping("/{id}/points")
    public ResponseEntity<ApiResponse<Customer>> addPoints(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer points = ((Number) request.get("points")).intValue();
        Integer type = ((Number) request.get("type")).intValue();
        Long relatedId = Long.valueOf(request.get("relatedId").toString());
        String description = (String) request.get("description");
        
        Customer updatedCustomer = customerService.addPoints(id, points, type, relatedId, description);
        return updatedCustomer != null ? ResponseEntity.ok(ApiResponse.success("积分添加成功", updatedCustomer))
                                   : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在"));
    }
    
    @PostMapping("/{id}/redeem")
    public ResponseEntity<ApiResponse<Customer>> redeemPoints(@PathVariable Long id, @RequestBody Map<String, Object> request) {
        Integer points = ((Number) request.get("points")).intValue();
        Integer type = ((Number) request.get("type")).intValue();
        Long relatedId = Long.valueOf(request.get("relatedId").toString());
        String description = (String) request.get("description");
        
        Customer updatedCustomer = customerService.redeemPoints(id, points, type, relatedId, description);
        return updatedCustomer != null ? ResponseEntity.ok(ApiResponse.success("积分兑换成功", updatedCustomer))
                                   : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在"));
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.ok(ApiResponse.success("顾客删除成功", null));
    }
    
    @GetMapping("/{id}/points")
    public ResponseEntity<ApiResponse<Integer>> getCustomerPoints(@PathVariable Long id) {
        Integer points = customerService.getCustomerPoints(id);
        return points != null ? ResponseEntity.ok(ApiResponse.success(points))
                           : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在"));
    }
    
    @GetMapping("/{id}/level")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> getCustomerMemberLevel(@PathVariable Long id) {
        java.util.Map<String, Object> levelInfo = customerService.getCustomerMemberLevel(id);
        return levelInfo != null ? ResponseEntity.ok(ApiResponse.success(levelInfo))
                              : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("顾客不存在"));
    }
}
package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Employee;
import com.solocoffee.backend.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    
    @Autowired
    private EmployeeService employeeService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Employee>> createEmployee(@RequestBody Employee employee) {
        logger.debug("开始创建员工: {}", employee);
        try {
            Employee createdEmployee = employeeService.createEmployee(employee);
            logger.debug("员工创建成功: {}", createdEmployee);
            return ResponseEntity.ok(ApiResponse.success("员工创建成功", createdEmployee));
        } catch (Exception e) {
            logger.error("员工创建失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("员工创建失败: " + e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> getEmployeeById(@PathVariable Long id) {
        Optional<Employee> employee = employeeService.getEmployeeById(id);
        if (employee.isPresent()) {
            return ResponseEntity.ok(ApiResponse.success(employee.get()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("员工不存在"));
        }
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<?>> getEmployees(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long storeId) {
        try {
            Map<String, Object> employees = employeeService.getEmployeesWithFilter(page, size, keyword, status, storeId);
            return ResponseEntity.ok(ApiResponse.success(employees));
        } catch (Exception e) {
            logger.error("查询员工列表失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("系统内部错误"));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Employee>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {
        logger.debug("开始更新员工，ID: {}, 员工数据: {}", id, employee);
        try {
            Employee updatedEmployee = employeeService.updateEmployee(id, employee);
            if (updatedEmployee != null) {
                logger.debug("员工更新成功: {}", updatedEmployee);
                return ResponseEntity.ok(ApiResponse.success("员工更新成功", updatedEmployee));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("员工不存在"));
            }
        } catch (Exception e) {
            logger.error("员工更新失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("员工更新失败: " + e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {
        logger.debug("开始删除员工，ID: {}", id);
        try {
            employeeService.deleteEmployee(id);
            logger.debug("员工删除成功，ID: {}", id);
            return ResponseEntity.ok(ApiResponse.success("员工删除成功", null));
        } catch (Exception e) {
            logger.error("员工删除失败: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError().body(ApiResponse.internalError("员工删除失败: " + e.getMessage()));
        }
    }
}
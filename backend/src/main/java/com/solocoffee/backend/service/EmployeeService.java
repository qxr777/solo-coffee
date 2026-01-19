package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Employee;
import com.solocoffee.backend.repository.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;

@Service
public class EmployeeService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);
    
    @Autowired
    private EmployeeRepository employeeRepository;
    
    @Transactional
    public Employee createEmployee(Employee employee) {
        logger.debug("开始创建员工: {}", employee);
        return employeeRepository.save(employee);
    }
    
    public Optional<Employee> getEmployeeById(Long id) {
        return employeeRepository.findById(id);
    }
    
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }
    
    public Map<String, Object> getEmployeesWithFilter(int page, int size, String keyword, Integer status, Long storeId) {
        List<Employee> allEmployees = employeeRepository.findAll();
        
        // 筛选
        List<Employee> filteredEmployees = new ArrayList<>();
        for (Employee employee : allEmployees) {
            boolean matchKeyword = keyword == null || employee.getName().contains(keyword) || 
                                 employee.getPhone().contains(keyword) || employee.getEmployeeId().contains(keyword);
            boolean matchStatus = status == null || employee.getStatus().equals(status);
            boolean matchStore = storeId == null || employee.getStoreId().equals(storeId);
            
            if (matchKeyword && matchStatus && matchStore) {
                filteredEmployees.add(employee);
            }
        }
        
        // 分页
        int total = filteredEmployees.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Employee> paginatedEmployees = filteredEmployees.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("items", paginatedEmployees);
        response.put("total", total);
        response.put("pages", (total + size - 1) / size);
        response.put("current", page);
        response.put("size", size);
        
        return response;
    }
    
    @Transactional
    public Employee updateEmployee(Long id, Employee employee) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(id);
        if (optionalEmployee.isPresent()) {
            Employee existingEmployee = optionalEmployee.get();
            existingEmployee.setName(employee.getName());
            existingEmployee.setPhone(employee.getPhone());
            existingEmployee.setEmail(employee.getEmail());
            existingEmployee.setPosition(employee.getPosition());
            existingEmployee.setStatus(employee.getStatus());
            return employeeRepository.save(existingEmployee);
        }
        return null;
    }
    
    @Transactional
    public void deleteEmployee(Long id) {
        employeeRepository.deleteById(id);
    }
}
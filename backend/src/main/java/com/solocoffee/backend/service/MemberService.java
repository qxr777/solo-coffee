package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Customer;
import com.solocoffee.backend.entity.MemberLevel;
import com.solocoffee.backend.entity.PointsRecord;
import com.solocoffee.backend.repository.CustomerRepository;
import com.solocoffee.backend.repository.PointsRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class MemberService {
    
    private static final Logger logger = LoggerFactory.getLogger(MemberService.class);
    
    @Autowired
    private CustomerRepository customerRepository;
    
    @Autowired
    private PointsRecordRepository pointsRecordRepository;
    
    // 模拟会员等级数据
    private static final Map<Integer, MemberLevel> memberLevels = new ConcurrentHashMap<>();
    
    static {
        // 初始化会员等级
        MemberLevel level1 = new MemberLevel();
        level1.setId(1L);
        level1.setName("普通会员");
        level1.setMinPoints(0);
        level1.setDiscountRate(BigDecimal.valueOf(1.0));
        memberLevels.put(0, level1);
        
        MemberLevel level2 = new MemberLevel();
        level2.setId(2L);
        level2.setName("银卡会员");
        level2.setMinPoints(1000);
        level2.setDiscountRate(BigDecimal.valueOf(0.95));
        memberLevels.put(1000, level2);
        
        MemberLevel level3 = new MemberLevel();
        level3.setId(3L);
        level3.setName("金卡会员");
        level3.setMinPoints(5000);
        level3.setDiscountRate(BigDecimal.valueOf(0.9));
        memberLevels.put(5000, level3);
        
        MemberLevel level4 = new MemberLevel();
        level4.setId(4L);
        level4.setName("钻石会员");
        level4.setMinPoints(10000);
        level4.setDiscountRate(BigDecimal.valueOf(0.85));
        memberLevels.put(10000, level4);
    }
    
    @Transactional
    public Map<String, Object> registerMember(String name, String phone, String email, String password) {
        logger.debug("会员注册: name={}, phone={}, email={}", name, phone, email);
        
        // 检查手机号是否已存在（使用JPA默认方法，实际项目中应该在CustomerRepository中添加findByPhone方法）
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            if (phone.equals(customer.getPhone())) {
                throw new RuntimeException("手机号已注册");
            }
        }
        
        // 创建新会员
        Customer customer = new Customer();
        customer.setName(name);
        customer.setPhone(phone);
        customer.setEmail(email);
        customer.setPoints(0);
        customer.setCreatedAt(LocalDateTime.now());
        customer.setUpdatedAt(LocalDateTime.now());
        
        Customer savedCustomer = customerRepository.save(customer);
        
        // 生成会员号
        String memberNo = "MB" + String.format("%08d", savedCustomer.getId());
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("id", savedCustomer.getId());
        response.put("memberNo", memberNo);
        response.put("name", savedCustomer.getName());
        response.put("phone", savedCustomer.getPhone());
        response.put("email", savedCustomer.getEmail());
        response.put("points", savedCustomer.getPoints());
        
        Map<String, Object> levelInfo = new HashMap<>();
        levelInfo.put("id", 1);
        levelInfo.put("name", "普通会员");
        levelInfo.put("minPoints", 0);
        levelInfo.put("discountRate", 1.0);
        response.put("level", levelInfo);
        
        response.put("createdAt", savedCustomer.getCreatedAt());
        
        logger.info("会员注册成功: id={}, phone={}", savedCustomer.getId(), phone);
        return response;
    }
    
    public Map<String, Object> getMemberDetails(Long memberId) {
        Optional<Customer> optionalCustomer = customerRepository.findById(memberId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Map<String, Object> details = new HashMap<>();
            details.put("id", customer.getId());
            details.put("memberNo", "MB" + String.format("%08d", customer.getId()));
            details.put("name", customer.getName());
            details.put("phone", customer.getPhone());
            details.put("email", customer.getEmail());
            details.put("points", customer.getPoints());
            
            // 计算会员等级
            MemberLevel level = calculateMemberLevel(customer.getPoints());
            Map<String, Object> levelInfo = new HashMap<>();
            levelInfo.put("id", level.getId());
            levelInfo.put("name", level.getName());
            levelInfo.put("minPoints", level.getMinPoints());
            levelInfo.put("discountRate", level.getDiscountRate());
            details.put("level", levelInfo);
            
            details.put("createdAt", customer.getCreatedAt());
            details.put("updatedAt", customer.getUpdatedAt());
            details.put("lastVisitAt", customer.getLastVisitAt());
            
            return details;
        }
        return null;
    }
    
    public Map<String, Object> getMemberPoints(Long memberId, int page, int size) {
        Optional<Customer> optionalCustomer = customerRepository.findById(memberId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Map<String, Object> pointsInfo = new HashMap<>();
            pointsInfo.put("totalPoints", customer.getPoints());
            
            // 获取积分记录（使用JPA默认方法，实际项目中应该在PointsRecordRepository中添加排序方法）
            List<PointsRecord> pointsRecords = pointsRecordRepository.findByCustomerId(memberId);
            
            // 手动排序
            pointsRecords.sort((r1, r2) -> r2.getCreatedAt().compareTo(r1.getCreatedAt()));
            
            // 分页
            int start = (page - 1) * size;
            int end = Math.min(start + size, pointsRecords.size());
            List<PointsRecord> paginatedRecords = pointsRecords.subList(start, end);
            
            // 转换为响应格式
            List<Map<String, Object>> recordList = new ArrayList<>();
            for (PointsRecord record : paginatedRecords) {
                Map<String, Object> recordInfo = new HashMap<>();
                recordInfo.put("id", record.getId());
                recordInfo.put("points", record.getPoints());
                recordInfo.put("type", record.getType());
                recordInfo.put("relatedId", record.getRelatedId());
                recordInfo.put("description", record.getDescription());
                recordInfo.put("createdAt", record.getCreatedAt());
                recordList.add(recordInfo);
            }
            
            pointsInfo.put("records", recordList);
            pointsInfo.put("total", pointsRecords.size());
            pointsInfo.put("pages", (pointsRecords.size() + size - 1) / size);
            pointsInfo.put("current", page);
            pointsInfo.put("size", size);
            
            return pointsInfo;
        }
        return null;
    }
    
    @Transactional
    public Map<String, Object> addMemberPoints(Long memberId, int points, String description) {
        Optional<Customer> optionalCustomer = customerRepository.findById(memberId);
        if (!optionalCustomer.isPresent()) {
            throw new RuntimeException("会员不存在");
        }
        
        Customer customer = optionalCustomer.get();
        
        // 更新会员积分
        int newPoints = customer.getPoints() + points;
        customer.setPoints(newPoints);
        customer.setUpdatedAt(LocalDateTime.now());
        customerRepository.save(customer);
        
        // 记录积分变动
        PointsRecord record = new PointsRecord();
        record.setCustomerId(memberId);
        record.setPoints(points);
        record.setType(points > 0 ? 1 : 2); // 1: 增加, 2: 减少
        record.setDescription(description);
        record.setCreatedAt(LocalDateTime.now());
        pointsRecordRepository.save(record);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("memberId", memberId);
        response.put("oldPoints", customer.getPoints() - points);
        response.put("newPoints", newPoints);
        response.put("addedPoints", points);
        response.put("description", description);
        
        return response;
    }
    
    @Transactional
    public Map<String, Object> updateMember(Long memberId, Map<String, Object> request) {
        Optional<Customer> optionalCustomer = customerRepository.findById(memberId);
        if (!optionalCustomer.isPresent()) {
            return null;
        }
        
        Customer customer = optionalCustomer.get();
        
        // 更新会员信息
        if (request.containsKey("name")) {
            customer.setName((String) request.get("name"));
        }
        if (request.containsKey("email")) {
            customer.setEmail((String) request.get("email"));
        }
        if (request.containsKey("phone")) {
                String newPhone = (String) request.get("phone");
                // 检查手机号是否已被其他会员使用（使用JPA默认方法）
                List<Customer> customers = customerRepository.findAll();
                for (Customer c : customers) {
                    if (newPhone.equals(c.getPhone()) && !c.getId().equals(memberId)) {
                        throw new RuntimeException("手机号已被其他会员使用");
                    }
                }
                customer.setPhone(newPhone);
            }
        
        customer.setUpdatedAt(LocalDateTime.now());
        Customer updatedCustomer = customerRepository.save(customer);
        
        // 构建响应
        Map<String, Object> response = new HashMap<>();
        response.put("id", updatedCustomer.getId());
        response.put("name", updatedCustomer.getName());
        response.put("phone", updatedCustomer.getPhone());
        response.put("email", updatedCustomer.getEmail());
        response.put("updatedAt", updatedCustomer.getUpdatedAt());
        
        return response;
    }
    
    private MemberLevel calculateMemberLevel(int points) {
        // 找到对应的会员等级
        List<Integer> thresholds = new ArrayList<>(memberLevels.keySet());
        Collections.sort(thresholds);
        
        for (int i = thresholds.size() - 1; i >= 0; i--) {
            if (points >= thresholds.get(i)) {
                return memberLevels.get(thresholds.get(i));
            }
        }
        
        return memberLevels.get(0); // 默认普通会员
    }
}

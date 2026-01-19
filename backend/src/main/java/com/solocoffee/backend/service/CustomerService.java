package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Customer;
import com.solocoffee.backend.entity.PointsRecord;
import com.solocoffee.backend.repository.CustomerRepository;
import com.solocoffee.backend.repository.PointsRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private PointsRecordRepository pointsRecordRepository;

    public Customer createCustomer(Customer customer) {
        // 设置初始积分和会员等级
        if (customer.getPoints() == null) {
            customer.setPoints(0);
        }
        if (customer.getMemberLevelId() == null) {
            customer.setMemberLevelId(1L); // 1: 普通会员
        }
        return customerRepository.save(customer);
    }

    public Optional<Customer> getCustomerById(Long id) {
        return customerRepository.findById(id);
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    @Transactional
    public Customer updateCustomer(Customer customer) {
        if (customer == null || customer.getId() == null) {
            return null;
        }

        Optional<Customer> existingCustomerOpt = customerRepository.findById(customer.getId());
        if (!existingCustomerOpt.isPresent()) {
            return null;
        }

        Customer existingCustomer = existingCustomerOpt.get();

        // 更新字段
        if (customer.getName() != null) {
            existingCustomer.setName(customer.getName());
        }
        if (customer.getEmail() != null) {
            existingCustomer.setEmail(customer.getEmail());
        }
        if (customer.getPhone() != null) {
            existingCustomer.setPhone(customer.getPhone());
        }
        if (customer.getWechatOpenId() != null) {
            existingCustomer.setWechatOpenId(customer.getWechatOpenId());
        }
        if (customer.getAvatarUrl() != null) {
            existingCustomer.setAvatarUrl(customer.getAvatarUrl());
        }
        if (customer.getLastVisitAt() != null) {
            existingCustomer.setLastVisitAt(customer.getLastVisitAt());
        }

        // 确保points字段不为null
        if (customer.getPoints() != null) {
            existingCustomer.setPoints(customer.getPoints());
        }

        // 确保memberLevelId字段不为null
        if (customer.getMemberLevelId() != null) {
            existingCustomer.setMemberLevelId(customer.getMemberLevelId());
        }

        // 自动更新会员等级
        updateMemberLevel(existingCustomer);
        return customerRepository.save(existingCustomer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }

    @Transactional
    public Customer addPoints(Long customerId, Integer points, Integer type, Long relatedId, String description) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            customer.setPoints(customer.getPoints() + points);

            // 创建积分记录
            PointsRecord pointsRecord = new PointsRecord();
            pointsRecord.setCustomerId(customerId);
            pointsRecord.setPoints(points);
            pointsRecord.setType(type);
            pointsRecord.setRelatedId(relatedId);
            pointsRecord.setDescription(description);
            pointsRecordRepository.save(pointsRecord);

            // 更新会员等级
            updateMemberLevel(customer);

            return customerRepository.save(customer);
        }
        return null;
    }

    @Transactional
    public Customer redeemPoints(Long customerId, Integer points, Integer type, Long relatedId, String description) {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            if (customer.getPoints() >= points) {
                customer.setPoints(customer.getPoints() - points);

                // 创建积分记录
                PointsRecord pointsRecord = new PointsRecord();
                pointsRecord.setCustomerId(customerId);
                pointsRecord.setPoints(-points); // 负数表示兑换
                pointsRecord.setType(type);
                pointsRecord.setRelatedId(relatedId);
                pointsRecord.setDescription(description);
                pointsRecordRepository.save(pointsRecord);

                // 更新会员等级
                updateMemberLevel(customer);

                return customerRepository.save(customer);
            }
        }
        return null;
    }

    private void updateMemberLevel(Customer customer) {
        if (customer == null) {
            return;
        }
        // 会员等级规则：
        // 普通会员：0-999积分
        // 银卡会员：1000-2999积分
        // 金卡会员：3000-4999积分
        // 钻石会员：5000+积分

        Integer points = customer.getPoints();
        if (points == null) {
            points = 0;
        }

        Long newLevelId;

        if (points >= 5000) {
            newLevelId = 4L; // 钻石会员
        } else if (points >= 3000) {
            newLevelId = 3L; // 金卡会员
        } else if (points >= 1000) {
            newLevelId = 2L; // 银卡会员
        } else {
            newLevelId = 1L; // 普通会员
        }

        if (customer.getMemberLevelId() == null || !newLevelId.equals(customer.getMemberLevelId())) {
            customer.setMemberLevelId(newLevelId);
        }
    }

    public Customer getCustomerWithLevel(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            // 这里可以添加会员等级信息的加载
            return customer;
        }
        return null;
    }

    public Integer getCustomerPoints(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        return optionalCustomer.map(Customer::getPoints).orElse(null);
    }

    public java.util.Map<String, Object> getCustomerMemberLevel(Long id) {
        Optional<Customer> optionalCustomer = customerRepository.findById(id);
        if (optionalCustomer.isPresent()) {
            Customer customer = optionalCustomer.get();
            Long memberLevelId = customer.getMemberLevelId();

            // 构建会员等级信息
            java.util.Map<String, Object> levelInfo = new java.util.HashMap<>();
            levelInfo.put("memberLevelId", memberLevelId);

            // 根据等级ID设置等级名称
            String levelName;
            if (memberLevelId == 4) {
                levelName = "钻石会员";
            } else if (memberLevelId == 3) {
                levelName = "金卡会员";
            } else if (memberLevelId == 2) {
                levelName = "银卡会员";
            } else {
                levelName = "普通会员";
            }
            levelInfo.put("levelName", levelName);
            levelInfo.put("points", customer.getPoints());

            return levelInfo;
        }
        return null;
    }

    public java.util.Map<String, Object> getCustomersWithFilter(int page, int size, String keyword, Long levelId) {
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page - 1,
                size);
        org.springframework.data.domain.Page<Customer> customerPage = customerRepository.findWithFilter(keyword,
                levelId, pageable);

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("records", customerPage.getContent());
        response.put("total", customerPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);
        response.put("totalPages", customerPage.getTotalPages());

        return response;
    }
}
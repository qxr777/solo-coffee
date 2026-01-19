package com.solocoffee.backend.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class AddressService {
    
    private static final Logger logger = LoggerFactory.getLogger(AddressService.class);
    
    // 模拟地址存储
    private Map<Long, Address> addresses = new HashMap<>();
    private AtomicLong addressIdGenerator = new AtomicLong(1);
    
    /**
     * 创建地址
     * @param customerId 客户ID
     * @param name 收货人姓名
     * @param phone 联系电话
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param detailAddress 详细地址
     * @param isDefault 是否默认
     * @return 创建的地址
     */
    @Transactional
    public Map<String, Object> createAddress(Long customerId, String name, String phone, 
                                           String province, String city, String district, 
                                           String detailAddress, Boolean isDefault) {
        logger.debug("创建地址，客户ID: {}, 收货人: {}, 电话: {}", customerId, name, phone);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 如果设置为默认地址，将其他地址设置为非默认
            if (isDefault) {
                setOtherAddressesNonDefault(customerId);
            }
            
            // 2. 创建地址
            Address address = new Address();
            address.setId(addressIdGenerator.incrementAndGet());
            address.setCustomerId(customerId);
            address.setName(name);
            address.setPhone(phone);
            address.setProvince(province);
            address.setCity(city);
            address.setDistrict(district);
            address.setDetailAddress(detailAddress);
            address.setIsDefault(isDefault);
            address.setStatus(1); // 正常
            address.setCreatedAt(new Date());
            address.setUpdatedAt(new Date());
            
            // 3. 保存地址
            addresses.put(address.getId(), address);
            
            // 4. 构建响应
            result.put("success", true);
            result.put("addressId", address.getId());
            result.put("customerId", customerId);
            result.put("name", name);
            result.put("phone", phone);
            result.put("province", province);
            result.put("city", city);
            result.put("district", district);
            result.put("detailAddress", detailAddress);
            result.put("isDefault", isDefault);
            result.put("createdAt", address.getCreatedAt());
            result.put("message", "地址创建成功");
            
            logger.info("地址创建成功，地址ID: {}", address.getId());
        } catch (Exception e) {
            logger.error("地址创建失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "地址创建失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 更新地址
     * @param addressId 地址ID
     * @param name 收货人姓名
     * @param phone 联系电话
     * @param province 省份
     * @param city 城市
     * @param district 区县
     * @param detailAddress 详细地址
     * @param isDefault 是否默认
     * @param customerId 客户ID
     * @return 更新后的地址
     */
    @Transactional
    public Map<String, Object> updateAddress(Long addressId, String name, String phone, 
                                           String province, String city, String district, 
                                           String detailAddress, Boolean isDefault, Long customerId) {
        logger.debug("更新地址，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取地址
            Address address = addresses.get(addressId);
            if (address == null) {
                throw new RuntimeException("地址不存在");
            }
            
            // 2. 验证权限
            if (!address.getCustomerId().equals(customerId)) {
                throw new RuntimeException("无权操作该地址");
            }
            
            // 3. 如果设置为默认地址，将其他地址设置为非默认
            if (isDefault) {
                setOtherAddressesNonDefault(customerId);
            }
            
            // 4. 更新地址
            address.setName(name);
            address.setPhone(phone);
            address.setProvince(province);
            address.setCity(city);
            address.setDistrict(district);
            address.setDetailAddress(detailAddress);
            address.setIsDefault(isDefault);
            address.setUpdatedAt(new Date());
            
            // 5. 保存地址
            addresses.put(address.getId(), address);
            
            // 6. 构建响应
            result.put("success", true);
            result.put("addressId", address.getId());
            result.put("name", name);
            result.put("phone", phone);
            result.put("province", province);
            result.put("city", city);
            result.put("district", district);
            result.put("detailAddress", detailAddress);
            result.put("isDefault", isDefault);
            result.put("updatedAt", address.getUpdatedAt());
            result.put("message", "地址更新成功");
            
            logger.info("地址更新成功，地址ID: {}", address.getId());
        } catch (Exception e) {
            logger.error("地址更新失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "地址更新失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 删除地址
     * @param addressId 地址ID
     * @param customerId 客户ID
     * @return 删除结果
     */
    @Transactional
    public Map<String, Object> deleteAddress(Long addressId, Long customerId) {
        logger.debug("删除地址，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取地址
            Address address = addresses.get(addressId);
            if (address == null) {
                throw new RuntimeException("地址不存在");
            }
            
            // 2. 验证权限
            if (!address.getCustomerId().equals(customerId)) {
                throw new RuntimeException("无权操作该地址");
            }
            
            // 3. 删除地址
            addresses.remove(addressId);
            
            // 4. 构建响应
            result.put("success", true);
            result.put("addressId", addressId);
            result.put("message", "地址删除成功");
            
            logger.info("地址删除成功，地址ID: {}", addressId);
        } catch (Exception e) {
            logger.error("地址删除失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "地址删除失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 获取客户的地址列表
     * @param customerId 客户ID
     * @return 地址列表
     */
    public List<Map<String, Object>> getCustomerAddresses(Long customerId) {
        logger.debug("获取客户地址列表，客户ID: {}", customerId);
        
        List<Map<String, Object>> addressList = new ArrayList<>();
        
        // 查找客户的地址
        for (Address address : addresses.values()) {
            if (address.getCustomerId().equals(customerId) && address.getStatus() == 1) {
                Map<String, Object> addressInfo = new HashMap<>();
                addressInfo.put("addressId", address.getId());
                addressInfo.put("name", address.getName());
                addressInfo.put("phone", address.getPhone());
                addressInfo.put("province", address.getProvince());
                addressInfo.put("city", address.getCity());
                addressInfo.put("district", address.getDistrict());
                addressInfo.put("detailAddress", address.getDetailAddress());
                addressInfo.put("isDefault", address.getIsDefault());
                addressInfo.put("createdAt", address.getCreatedAt());
                addressInfo.put("updatedAt", address.getUpdatedAt());
                addressList.add(addressInfo);
            }
        }
        
        // 按默认状态和创建时间排序
        addressList.sort((a, b) -> {
            boolean aDefault = (Boolean) a.get("isDefault");
            boolean bDefault = (Boolean) b.get("isDefault");
            if (aDefault && !bDefault) return -1;
            if (!aDefault && bDefault) return 1;
            Date aDate = (Date) a.get("createdAt");
            Date bDate = (Date) b.get("createdAt");
            return bDate.compareTo(aDate);
        });
        
        logger.info("获取客户地址列表完成，数量: {}", addressList.size());
        return addressList;
    }
    
    /**
     * 获取地址详情
     * @param addressId 地址ID
     * @param customerId 客户ID
     * @return 地址详情
     */
    public Map<String, Object> getAddressDetails(Long addressId, Long customerId) {
        logger.debug("获取地址详情，地址ID: {}, 客户ID: {}", addressId, customerId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 1. 获取地址
            Address address = addresses.get(addressId);
            if (address == null) {
                throw new RuntimeException("地址不存在");
            }
            
            // 2. 验证权限
            if (!address.getCustomerId().equals(customerId)) {
                throw new RuntimeException("无权访问该地址");
            }
            
            // 3. 构建响应
            result.put("success", true);
            result.put("addressId", address.getId());
            result.put("customerId", address.getCustomerId());
            result.put("name", address.getName());
            result.put("phone", address.getPhone());
            result.put("province", address.getProvince());
            result.put("city", address.getCity());
            result.put("district", address.getDistrict());
            result.put("detailAddress", address.getDetailAddress());
            result.put("isDefault", address.getIsDefault());
            result.put("createdAt", address.getCreatedAt());
            result.put("updatedAt", address.getUpdatedAt());
            result.put("message", "地址详情查询成功");
            
            logger.info("地址详情查询成功，地址ID: {}", addressId);
        } catch (Exception e) {
            logger.error("地址详情查询失败: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "地址详情查询失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 将其他地址设置为非默认
     */
    private void setOtherAddressesNonDefault(Long customerId) {
        for (Address address : addresses.values()) {
            if (address.getCustomerId().equals(customerId) && address.getStatus() == 1) {
                address.setIsDefault(false);
                address.setUpdatedAt(new Date());
                addresses.put(address.getId(), address);
            }
        }
    }
    
    // 地址内部类
    private static class Address {
        private Long id;
        private Long customerId;
        private String name;
        private String phone;
        private String province;
        private String city;
        private String district;
        private String detailAddress;
        private Boolean isDefault;
        private Integer status; // 1: 正常, 2: 已删除
        private Date createdAt;
        private Date updatedAt;
        
        // Getters and Setters
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public Long getCustomerId() {
            return customerId;
        }
        
        public void setCustomerId(Long customerId) {
            this.customerId = customerId;
        }
        
        public String getName() {
            return name;
        }
        
        public void setName(String name) {
            this.name = name;
        }
        
        public String getPhone() {
            return phone;
        }
        
        public void setPhone(String phone) {
            this.phone = phone;
        }
        
        public String getProvince() {
            return province;
        }
        
        public void setProvince(String province) {
            this.province = province;
        }
        
        public String getCity() {
            return city;
        }
        
        public void setCity(String city) {
            this.city = city;
        }
        
        public String getDistrict() {
            return district;
        }
        
        public void setDistrict(String district) {
            this.district = district;
        }
        
        public String getDetailAddress() {
            return detailAddress;
        }
        
        public void setDetailAddress(String detailAddress) {
            this.detailAddress = detailAddress;
        }
        
        public Boolean getIsDefault() {
            return isDefault;
        }
        
        public void setIsDefault(Boolean isDefault) {
            this.isDefault = isDefault;
        }
        
        public Integer getStatus() {
            return status;
        }
        
        public void setStatus(Integer status) {
            this.status = status;
        }
        
        public Date getCreatedAt() {
            return createdAt;
        }
        
        public void setCreatedAt(Date createdAt) {
            this.createdAt = createdAt;
        }
        
        public Date getUpdatedAt() {
            return updatedAt;
        }
        
        public void setUpdatedAt(Date updatedAt) {
            this.updatedAt = updatedAt;
        }
    }
}

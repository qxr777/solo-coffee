package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Store;
import com.solocoffee.backend.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class StoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(StoreService.class);
    
    @Autowired
    private StoreRepository storeRepository;
    
    // 模拟门店收藏数据
    private static final Map<Long, Set<Long>> favoriteStores = new ConcurrentHashMap<>();
    
    // 模拟门店位置数据
    private static final Map<Long, StoreLocation> storeLocations = new ConcurrentHashMap<>();
    
    static {
        // 初始化测试门店位置
        storeLocations.put(1L, new StoreLocation(39.9087, 116.3975));
        storeLocations.put(2L, new StoreLocation(39.9187, 116.4075));
        storeLocations.put(3L, new StoreLocation(39.8987, 116.3875));
    }
    
    @Transactional
    public Store createStore(Store store) {
        logger.debug("开始创建门店: {}", store);
        Store createdStore = storeRepository.save(store);
        
        // 为新门店生成默认位置（模拟）
        if (!storeLocations.containsKey(createdStore.getId())) {
            storeLocations.put(createdStore.getId(), new StoreLocation(
                39.9000 + Math.random() * 0.1,
                116.3900 + Math.random() * 0.1
            ));
        }
        
        return createdStore;
    }
    
    public Optional<Store> getStoreById(Long id) {
        return storeRepository.findById(id);
    }
    
    public Map<String, Object> getStoreDetails(Long id) {
        Optional<Store> optionalStore = storeRepository.findById(id);
        if (optionalStore.isPresent()) {
            Store store = optionalStore.get();
            Map<String, Object> details = new HashMap<>();
            details.put("id", store.getId());
            details.put("name", store.getName());
            details.put("address", store.getAddress());
            details.put("phone", store.getPhone());
            details.put("latitude", storeLocations.getOrDefault(id, new StoreLocation(0, 0)).getLatitude());
            details.put("longitude", storeLocations.getOrDefault(id, new StoreLocation(0, 0)).getLongitude());
            details.put("businessHours", store.getBusinessHours());
            details.put("status", store.getStatus());
            details.put("isFavorite", isStoreFavorite(id));
            details.put("imageUrl", "https://example.com/images/store" + id + ".jpg");
            details.put("description", "Solo Coffee门店，提供高品质咖啡和舒适环境");
            details.put("facilities", Arrays.asList("免费WiFi", "座位", "停车位"));
            
            // 模拟菜单分类
            List<Map<String, Object>> menuCategories = new ArrayList<>();
            Map<String, Object> category1 = new HashMap<>();
            category1.put("id", 1);
            category1.put("name", "咖啡");
            category1.put("productCount", 10);
            menuCategories.add(category1);
            
            Map<String, Object> category2 = new HashMap<>();
            category2.put("id", 2);
            category2.put("name", "甜点");
            category2.put("productCount", 5);
            menuCategories.add(category2);
            
            details.put("menuCategories", menuCategories);
            
            return details;
        }
        return null;
    }
    
    public List<Store> getAllStores() {
        return storeRepository.findAll();
    }
    
    public Map<String, Object> getNearbyStores(double latitude, double longitude, int radius, int page, int size) {
        logger.debug("获取附近门店: latitude={}, longitude={}, radius={}, page={}, size={}", 
                   latitude, longitude, radius, page, size);
        
        List<Map<String, Object>> nearbyStores = new ArrayList<>();
        List<Store> allStores = storeRepository.findAll();
        
        for (Store store : allStores) {
            StoreLocation location = storeLocations.getOrDefault(store.getId(), new StoreLocation(0, 0));
            double distance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
            
            if (distance <= radius) {
                Map<String, Object> storeInfo = new HashMap<>();
                storeInfo.put("id", store.getId());
                storeInfo.put("name", store.getName());
                storeInfo.put("address", store.getAddress());
                storeInfo.put("phone", store.getPhone());
                storeInfo.put("latitude", location.getLatitude());
                storeInfo.put("longitude", location.getLongitude());
                storeInfo.put("distance", (int) Math.round(distance));
                storeInfo.put("businessHours", store.getBusinessHours());
                storeInfo.put("status", store.getStatus());
                storeInfo.put("isFavorite", isStoreFavorite(store.getId()));
                storeInfo.put("imageUrl", "https://example.com/images/store" + store.getId() + ".jpg");
                nearbyStores.add(storeInfo);
            }
        }
        
        // 按距离排序
        nearbyStores.sort(Comparator.comparingInt(o -> (int) o.get("distance")));
        
        // 分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, nearbyStores.size());
        List<Map<String, Object>> paginatedStores = nearbyStores.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", nearbyStores.size());
        response.put("pages", (nearbyStores.size() + size - 1) / size);
        response.put("current", page);
        response.put("size", size);
        response.put("records", paginatedStores);
        
        return response;
    }
    
    public Map<String, Object> searchStores(String keyword, Double latitude, Double longitude, int page, int size) {
        logger.debug("搜索门店: keyword={}, latitude={}, longitude={}, page={}, size={}", 
                   keyword, latitude, longitude, page, size);
        
        List<Map<String, Object>> searchResults = new ArrayList<>();
        List<Store> allStores = storeRepository.findAll();
        
        for (Store store : allStores) {
            if (store.getName().contains(keyword) || store.getAddress().contains(keyword)) {
                Map<String, Object> storeInfo = new HashMap<>();
                storeInfo.put("id", store.getId());
                storeInfo.put("name", store.getName());
                storeInfo.put("address", store.getAddress());
                storeInfo.put("phone", store.getPhone());
                
                StoreLocation location = storeLocations.getOrDefault(store.getId(), new StoreLocation(0, 0));
                storeInfo.put("latitude", location.getLatitude());
                storeInfo.put("longitude", location.getLongitude());
                
                if (latitude != null && longitude != null) {
                    double distance = calculateDistance(latitude, longitude, location.getLatitude(), location.getLongitude());
                    storeInfo.put("distance", (int) Math.round(distance));
                }
                
                storeInfo.put("businessHours", store.getBusinessHours());
                storeInfo.put("status", store.getStatus());
                storeInfo.put("isFavorite", isStoreFavorite(store.getId()));
                storeInfo.put("imageUrl", "https://example.com/images/store" + store.getId() + ".jpg");
                searchResults.add(storeInfo);
            }
        }
        
        // 按名称排序
        searchResults.sort(Comparator.comparing(o -> (String) o.get("name")));
        
        // 分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, searchResults.size());
        List<Map<String, Object>> paginatedResults = searchResults.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", searchResults.size());
        response.put("pages", (searchResults.size() + size - 1) / size);
        response.put("current", page);
        response.put("size", size);
        response.put("records", paginatedResults);
        
        return response;
    }
    
    @Transactional
    public Map<String, Object> favoriteStore(Long storeId, boolean isFavorite) {
        if (!storeRepository.existsById(storeId)) {
            throw new RuntimeException("门店不存在");
        }
        
        // 模拟用户ID（实际应用中从认证信息获取）
        Long userId = 1L;
        
        if (isFavorite) {
            Set<Long> favorites = favoriteStores.computeIfAbsent(userId, k -> new HashSet<>());
            favorites.add(storeId);
        } else {
            Set<Long> favorites = favoriteStores.get(userId);
            if (favorites != null) {
                favorites.remove(storeId);
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("storeId", storeId);
        response.put("isFavorite", isFavorite);
        return response;
    }
    
    public Map<String, Object> getFavoriteStores(int page, int size) {
        // 模拟用户ID（实际应用中从认证信息获取）
        Long userId = 1L;
        Set<Long> favoriteStoreIds = favoriteStores.getOrDefault(userId, new HashSet<>());
        
        List<Map<String, Object>> favoriteStoresList = new ArrayList<>();
        for (Long storeId : favoriteStoreIds) {
            Optional<Store> optionalStore = storeRepository.findById(storeId);
            if (optionalStore.isPresent()) {
                Store store = optionalStore.get();
                Map<String, Object> storeInfo = new HashMap<>();
                storeInfo.put("id", store.getId());
                storeInfo.put("name", store.getName());
                storeInfo.put("address", store.getAddress());
                storeInfo.put("phone", store.getPhone());
                storeInfo.put("latitude", storeLocations.getOrDefault(storeId, new StoreLocation(0, 0)).getLatitude());
                storeInfo.put("longitude", storeLocations.getOrDefault(storeId, new StoreLocation(0, 0)).getLongitude());
                storeInfo.put("distance", 1000 + (int)(Math.random() * 5000)); // 模拟距离
                storeInfo.put("businessHours", store.getBusinessHours());
                storeInfo.put("status", store.getStatus());
                storeInfo.put("isFavorite", true);
                storeInfo.put("imageUrl", "https://example.com/images/store" + storeId + ".jpg");
                favoriteStoresList.add(storeInfo);
            }
        }
        
        // 分页
        int start = (page - 1) * size;
        int end = Math.min(start + size, favoriteStoresList.size());
        List<Map<String, Object>> paginatedStores = favoriteStoresList.subList(start, end);
        
        Map<String, Object> response = new HashMap<>();
        response.put("total", favoriteStoresList.size());
        response.put("pages", (favoriteStoresList.size() + size - 1) / size);
        response.put("current", page);
        response.put("size", size);
        response.put("records", paginatedStores);
        
        return response;
    }
    
    @Transactional
    public Store updateStore(Long id, Store store) {
        Optional<Store> optionalStore = storeRepository.findById(id);
        if (optionalStore.isPresent()) {
            Store existingStore = optionalStore.get();
            existingStore.setName(store.getName());
            existingStore.setAddress(store.getAddress());
            existingStore.setPhone(store.getPhone());
            existingStore.setEmail(store.getEmail());
            existingStore.setBusinessHours(store.getBusinessHours());
            existingStore.setManagerId(store.getManagerId());
            existingStore.setStatus(store.getStatus());
            return storeRepository.save(existingStore);
        }
        return null;
    }
    
    @Transactional
    public void deleteStore(Long id) {
        storeRepository.deleteById(id);
        storeLocations.remove(id);
        favoriteStores.values().forEach(favorites -> favorites.remove(id));
    }
    
    private boolean isStoreFavorite(Long storeId) {
        // 模拟用户ID（实际应用中从认证信息获取）
        Long userId = 1L;
        Set<Long> favorites = favoriteStores.get(userId);
        return favorites != null && favorites.contains(storeId);
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // 简化的距离计算（实际应用中使用Haversine公式）
        double dLat = Math.abs(lat1 - lat2);
        double dLon = Math.abs(lon1 - lon2);
        double distance = Math.sqrt(dLat * dLat + dLon * dLon) * 111000; // 转换为米
        return distance;
    }
    
    // 内部类
    private static class StoreLocation {
        private double latitude;
        private double longitude;
        
        public StoreLocation(double latitude, double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }
        
        public double getLatitude() {
            return latitude;
        }
        
        public double getLongitude() {
            return longitude;
        }
    }
}
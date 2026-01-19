package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Inventory;
import com.solocoffee.backend.repository.InventoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class InventoryServiceTest {
    
    @Mock
    private InventoryRepository inventoryRepository;
    
    @InjectMocks
    private InventoryService inventoryService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testCreateInventory() {
        // 创建测试库存
        Inventory inventory = new Inventory();
        inventory.setStoreId(1L);
        inventory.setProductId(1L);
        inventory.setQuantity(BigDecimal.valueOf(100));
        inventory.setUnit("g");
        inventory.setWarningThreshold(BigDecimal.valueOf(20));
        
        // 模拟 repository.save() 方法
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        
        // 调用服务方法
        Inventory createdInventory = inventoryService.createInventory(inventory);
        
        // 验证结果
        assertNotNull(createdInventory);
        assertEquals(1L, createdInventory.getStoreId());
        assertEquals(1L, createdInventory.getProductId());
        assertEquals(BigDecimal.valueOf(100), createdInventory.getQuantity());
        verify(inventoryRepository, times(1)).save(inventory);
    }
    
    @Test
    void testGetInventoryById() {
        // 创建测试库存
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStoreId(1L);
        inventory.setProductId(1L);
        inventory.setQuantity(BigDecimal.valueOf(100));
        
        // 模拟 repository.findById() 方法
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        
        // 调用服务方法
        Optional<Inventory> result = inventoryService.getInventoryById(1L);
        
        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals(1L, result.get().getStoreId());
        assertEquals(1L, result.get().getProductId());
        verify(inventoryRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetAllInventory() {
        // 创建测试库存列表
        List<Inventory> inventoryList = new ArrayList<>();
        Inventory inventory1 = new Inventory();
        inventory1.setId(1L);
        inventory1.setStoreId(1L);
        inventory1.setProductId(1L);
        inventoryList.add(inventory1);
        
        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setStoreId(1L);
        inventory2.setProductId(2L);
        inventoryList.add(inventory2);
        
        // 模拟 repository.findAll() 方法
        when(inventoryRepository.findAll()).thenReturn(inventoryList);
        
        // 调用服务方法
        List<Inventory> result = inventoryService.getAllInventory();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(inventoryRepository, times(1)).findAll();
    }
    
    @Test
    void testUpdateInventory() {
        // 创建测试库存
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setStoreId(1L);
        inventory.setProductId(1L);
        inventory.setQuantity(BigDecimal.valueOf(100));
        
        // 模拟 repository.findById() 和 repository.save() 方法
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        
        // 调用服务方法
        Inventory updatedInventory = inventoryService.updateInventory(inventory);
        
        // 验证结果
        assertNotNull(updatedInventory);
        assertEquals(1L, updatedInventory.getId());
        assertEquals(BigDecimal.valueOf(100), updatedInventory.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(inventory);
    }
    
    @Test
    void testUpdateInventoryQuantity() {
        // 创建测试库存
        Inventory inventory = new Inventory();
        inventory.setId(1L);
        inventory.setQuantity(BigDecimal.valueOf(100));
        
        // 模拟 repository.findById() 和 repository.save() 方法
        when(inventoryRepository.findById(1L)).thenReturn(Optional.of(inventory));
        when(inventoryRepository.save(inventory)).thenReturn(inventory);
        
        // 调用服务方法
        Inventory updatedInventory = inventoryService.updateInventoryQuantity(1L, BigDecimal.valueOf(150));
        
        // 验证结果
        assertNotNull(updatedInventory);
        assertEquals(BigDecimal.valueOf(150), updatedInventory.getQuantity());
        verify(inventoryRepository, times(1)).findById(1L);
        verify(inventoryRepository, times(1)).save(inventory);
    }
    
    @Test
    void testGetLowInventory() {
        // 创建测试库存列表
        List<Inventory> inventoryList = new ArrayList<>();
        Inventory inventory1 = new Inventory();
        inventory1.setId(1L);
        inventory1.setQuantity(BigDecimal.valueOf(15));
        inventory1.setWarningThreshold(BigDecimal.valueOf(20));
        inventoryList.add(inventory1);
        
        Inventory inventory2 = new Inventory();
        inventory2.setId(2L);
        inventory2.setQuantity(BigDecimal.valueOf(25));
        inventory2.setWarningThreshold(BigDecimal.valueOf(20));
        inventoryList.add(inventory2);
        
        // 模拟 repository.findAll() 方法
        when(inventoryRepository.findAll()).thenReturn(inventoryList);
        
        // 调用服务方法
        List<Inventory> result = inventoryService.getLowInventory();
        
        // 验证结果
        assertNotNull(result);
        verify(inventoryRepository, times(1)).findAll();
    }
    
    @Test
    void testDeleteInventory() {
        // 调用服务方法
        inventoryService.deleteInventory(1L);
        
        // 验证 repository.deleteById() 方法被调用
        verify(inventoryRepository, times(1)).deleteById(1L);
    }
}
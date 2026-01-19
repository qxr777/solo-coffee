package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.RawMaterialInventory;
import com.solocoffee.backend.repository.RawMaterialInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class RawMaterialInventoryService {
    @Autowired
    private RawMaterialInventoryRepository inventoryRepository;

    // 库存预警阈值
    private static final int LOW_STOCK_THRESHOLD = 10;

    // 自动补货阈值
    private static final int REORDER_THRESHOLD = 5;

    // 自动补货数量
    private static final int REORDER_QUANTITY = 50;

    // 获取所有原料库存
    public List<RawMaterialInventory> getAllRawMaterialInventory() {
        return inventoryRepository.findAll();
    }

    // 根据门店ID获取原料库存
    public List<RawMaterialInventory> getRawMaterialInventoryByStoreId(Long storeId) {
        return inventoryRepository.findByStoreId(storeId);
    }

    // 根据门店ID和原料ID获取原料库存
    public Optional<RawMaterialInventory> getRawMaterialInventoryByStoreIdAndMaterialId(Long storeId, Long materialId) {
        return inventoryRepository.findByStoreIdAndMaterialId(storeId, materialId);
    }

    // 更新原料库存
    @Transactional
    public RawMaterialInventory updateRawMaterialInventory(RawMaterialInventory inventory) {
        if (inventory == null || inventory.getId() == null) {
            return null;
        }

        Optional<RawMaterialInventory> existingInventoryOpt = inventoryRepository.findById(inventory.getId());
        if (!existingInventoryOpt.isPresent()) {
            return null;
        }

        RawMaterialInventory existingInventory = existingInventoryOpt.get();

        // 更新字段
        if (inventory.getStoreId() != null) {
            existingInventory.setStoreId(inventory.getStoreId());
        }
        if (inventory.getMaterialId() != null) {
            existingInventory.setMaterialId(inventory.getMaterialId());
        }
        if (inventory.getQuantity() != null) {
            existingInventory.setQuantity(inventory.getQuantity());
        }
        if (inventory.getWarningThreshold() != null) {
            existingInventory.setWarningThreshold(inventory.getWarningThreshold());
        }
        if (inventory.getLastPurchaseAt() != null) {
            existingInventory.setLastPurchaseAt(inventory.getLastPurchaseAt());
        }
        if (inventory.getLastStocktakingAt() != null) {
            existingInventory.setLastStocktakingAt(inventory.getLastStocktakingAt());
        }

        return inventoryRepository.save(existingInventory);
    }

    // 更新原料库存数量
    @Transactional
    public RawMaterialInventory updateRawMaterialInventoryQuantity(Long id, BigDecimal quantity) {
        Optional<RawMaterialInventory> optionalInventory = inventoryRepository.findById(id);
        if (optionalInventory.isPresent()) {
            RawMaterialInventory inventory = optionalInventory.get();
            inventory.setQuantity(quantity);
            return inventoryRepository.save(inventory);
        }
        return null;
    }

    // 扣减原料库存
    @Transactional
    public boolean deductRawMaterialInventory(Long storeId, Long materialId, BigDecimal quantity) {
        Optional<RawMaterialInventory> optionalInventory = inventoryRepository.findByStoreIdAndMaterialId(storeId, materialId);
        if (optionalInventory.isPresent()) {
            RawMaterialInventory inventory = optionalInventory.get();
            if (inventory.getQuantity().compareTo(quantity) >= 0) {
                inventory.setQuantity(inventory.getQuantity().subtract(quantity));
                inventoryRepository.save(inventory);
                
                // 检查是否需要自动补货
                checkAndReorder(inventory);
                
                return true;
            }
        }
        return false;
    }

    // 检查原料库存是否充足
    public boolean checkRawMaterialInventory(Long storeId, Long materialId, BigDecimal quantity) {
        Optional<RawMaterialInventory> optionalInventory = inventoryRepository.findByStoreIdAndMaterialId(storeId, materialId);
        if (optionalInventory.isPresent()) {
            RawMaterialInventory inventory = optionalInventory.get();
            return inventory.getQuantity().compareTo(quantity) >= 0;
        }
        return false;
    }

    // 检查是否需要自动补货
    @Transactional
    public List<RawMaterialInventory> checkAndReorder(RawMaterialInventory inventory) {
        List<RawMaterialInventory> reorderedInventory = new java.util.ArrayList<>();
        
        // 检查是否需要自动补货
        if (inventory.getQuantity().intValue() <= REORDER_THRESHOLD) {
            // 计算需要补货的数量
            int currentQuantity = inventory.getQuantity().intValue();
            int reorderAmount = REORDER_QUANTITY - currentQuantity;
            
            if (reorderAmount > 0) {
                // 更新库存数量
                inventory.setQuantity(BigDecimal.valueOf(REORDER_QUANTITY));
                inventoryRepository.save(inventory);
                reorderedInventory.add(inventory);
                
                // 这里可以添加补货记录或通知逻辑
                System.out.println("自动补货：原料ID " + inventory.getMaterialId() + "，门店ID：" + inventory.getStoreId() + "，补货数量：" + reorderAmount);
            }
        }
        
        return reorderedInventory;
    }

    // 添加原料库存
    @Transactional
    public RawMaterialInventory addRawMaterialInventory(RawMaterialInventory inventory) {
        // 设置创建时间和更新时间
        inventory.setCreatedAt(LocalDateTime.now());
        inventory.setUpdatedAt(LocalDateTime.now());
        return inventoryRepository.save(inventory);
    }

    // 删除原料库存
    @Transactional
    public void deleteRawMaterialInventory(Long id) {
        inventoryRepository.deleteById(id);
    }

    // 增加原料库存（用于退款等场景）
    @Transactional
    public boolean addRawMaterialInventory(Long storeId, Long materialId, BigDecimal quantity) {
        Optional<RawMaterialInventory> optionalInventory = inventoryRepository.findByStoreIdAndMaterialId(storeId, materialId);
        if (optionalInventory.isPresent()) {
            RawMaterialInventory inventory = optionalInventory.get();
            // 增加原料库存
            inventory.setQuantity(inventory.getQuantity().add(quantity));
            inventoryRepository.save(inventory);
            return true;
        }
        return false;
    }

    // 获取低于预警阈值的原料库存
    public List<RawMaterialInventory> getLowStockRawMaterialInventory() {
        return inventoryRepository.findByQuantityLessThanEqual((double) LOW_STOCK_THRESHOLD);
    }

    // 根据门店ID获取低于预警阈值的原料库存
    public List<RawMaterialInventory> getLowStockRawMaterialInventoryByStoreId(Long storeId) {
        return inventoryRepository.findByStoreIdAndQuantityLessThanEqual(storeId, (double) LOW_STOCK_THRESHOLD);
    }
}
package com.solocoffee.backend.service;

import com.solocoffee.backend.dto.InventoryDTO;
import com.solocoffee.backend.entity.Inventory;
import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.repository.InventoryRepository;
import com.solocoffee.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private ProductRepository productRepository;

    // 库存预警阈值
    private static final int LOW_STOCK_THRESHOLD = 10;

    // 自动补货阈值
    private static final int REORDER_THRESHOLD = 5;

    // 自动补货数量
    private static final int REORDER_QUANTITY = 50;

    public Inventory createInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Optional<Inventory> getInventoryById(Long id) {
        return inventoryRepository.findById(id);
    }

    public Optional<Inventory> getInventoryByProductId(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    public List<InventoryDTO> getAllInventoryDTOs() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        List<Product> productList = productRepository.findAll();
        Map<Long, Product> productMap = productList.stream()
                .collect(Collectors.toMap(Product::getId, p -> p, (a, b) -> a));

        return inventoryList.stream().map(inv -> {
            InventoryDTO dto = new InventoryDTO();
            dto.setId(inv.getId());
            dto.setProductId(inv.getProductId());
            dto.setQuantity(inv.getQuantity());
            dto.setUnit(inv.getUnit());
            dto.setWarningThreshold(inv.getWarningThreshold());

            Product p = productMap.get(inv.getProductId());
            if (p != null) {
                dto.setProductName(p.getName());
                dto.setProductNo(p.getProductNo());
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Inventory updateInventory(Inventory inventory) {
        if (inventory == null || inventory.getId() == null) {
            return null;
        }

        Optional<Inventory> existingInventoryOpt = inventoryRepository.findById(inventory.getId());
        if (!existingInventoryOpt.isPresent()) {
            return null;
        }

        Inventory existingInventory = existingInventoryOpt.get();

        // 更新字段
        if (inventory.getProductId() != null) {
            existingInventory.setProductId(inventory.getProductId());
        }
        if (inventory.getStoreId() != null) {
            existingInventory.setStoreId(inventory.getStoreId());
        }
        if (inventory.getQuantity() != null) {
            existingInventory.setQuantity(inventory.getQuantity());
        }
        if (inventory.getUnit() != null) {
            existingInventory.setUnit(inventory.getUnit());
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

    @Transactional
    public Inventory updateInventoryQuantity(Long id, BigDecimal quantity) {
        Optional<Inventory> optionalInventory = inventoryRepository.findById(id);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            inventory.setQuantity(quantity);
            return inventoryRepository.save(inventory);
        }
        return null;
    }

    @Transactional
    public boolean checkInventory(Long productId, BigDecimal quantity) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(productId);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            return inventory.getQuantity().compareTo(quantity) >= 0;
        }
        return false;
    }

    @Transactional
    public boolean deductInventory(Long productId, BigDecimal quantity) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(productId);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            if (inventory.getQuantity().compareTo(quantity) >= 0) {
                inventory.setQuantity(inventory.getQuantity().subtract(quantity));

                // 检查是否需要自动补货
                checkAndReorder(inventory);

                return inventoryRepository.save(inventory) != null;
            }
        }
        return false;
    }

    public Inventory updateInventoryByProductId(Inventory inventory) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(inventory.getProductId());
        if (optionalInventory.isPresent()) {
            Inventory existingInventory = optionalInventory.get();
            existingInventory.setQuantity(inventory.getQuantity());
            existingInventory.setUnit(inventory.getUnit());
            existingInventory.setWarningThreshold(inventory.getWarningThreshold());
            return inventoryRepository.save(existingInventory);
        } else {
            return inventoryRepository.save(inventory);
        }
    }

    @Transactional
    public boolean addInventory(Long productId, BigDecimal quantity) {
        Optional<Inventory> optionalInventory = inventoryRepository.findByProductId(productId);
        if (optionalInventory.isPresent()) {
            Inventory inventory = optionalInventory.get();
            inventory.setQuantity(inventory.getQuantity().add(quantity));
            return inventoryRepository.save(inventory) != null;
        }
        return false;
    }

    public List<Inventory> getLowInventory() {
        List<Inventory> allInventory = inventoryRepository.findAll();
        List<Inventory> lowInventory = new ArrayList<>();

        for (Inventory inventory : allInventory) {
            if (inventory.getQuantity().compareTo(inventory.getWarningThreshold()) <= 0) {
                lowInventory.add(inventory);
            }
        }

        return lowInventory;
    }

    @Transactional
    public List<Inventory> checkAndReorder(Inventory inventory) {
        List<Inventory> reorderedInventory = new ArrayList<>();

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
                System.out.println("自动补货：商品ID " + inventory.getProductId() + "，补货数量：" + reorderAmount);
            }
        }

        return reorderedInventory;
    }

    @Transactional
    public List<Inventory> processAutoReorder() {
        List<Inventory> allInventory = inventoryRepository.findAll();
        List<Inventory> reorderedInventory = new ArrayList<>();

        for (Inventory inventory : allInventory) {
            reorderedInventory.addAll(checkAndReorder(inventory));
        }

        return reorderedInventory;
    }

    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
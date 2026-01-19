package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.RawMaterialInventory;
import com.solocoffee.backend.service.RawMaterialInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/raw-material-inventory")
public class RawMaterialInventoryController {

    @Autowired
    private RawMaterialInventoryService rawMaterialInventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<RawMaterialInventory>> createRawMaterialInventory(@RequestBody RawMaterialInventory inventory) {
        try {
            // 设置创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            inventory.setCreatedAt(now);
            inventory.setUpdatedAt(now);
            
            RawMaterialInventory createdInventory = rawMaterialInventoryService.addRawMaterialInventory(inventory);
            return ResponseEntity.ok(ApiResponse.success("原材料库存创建成功", createdInventory));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料库存创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterialInventory>> getRawMaterialInventoryById(@PathVariable Long id) {
        try {
            // 这里应该添加通过ID查询的方法到服务层
            // 暂时返回404，后续可以实现
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.notFound("原材料库存不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取原材料库存失败: " + e.getMessage()));
        }
    }

    @GetMapping("/store/{storeId}")
    public ResponseEntity<ApiResponse<List<RawMaterialInventory>>> getRawMaterialInventoryByStoreId(@PathVariable Long storeId) {
        try {
            List<RawMaterialInventory> inventoryList = rawMaterialInventoryService.getRawMaterialInventoryByStoreId(storeId);
            return ResponseEntity.ok(ApiResponse.success(inventoryList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取原材料库存失败: " + e.getMessage()));
        }
    }

    @GetMapping("/store/{storeId}/material/{materialId}")
    public ResponseEntity<ApiResponse<RawMaterialInventory>> getRawMaterialInventoryByStoreIdAndMaterialId(
            @PathVariable Long storeId, @PathVariable Long materialId) {
        try {
            Optional<RawMaterialInventory> inventory = rawMaterialInventoryService.getRawMaterialInventoryByStoreIdAndMaterialId(storeId, materialId);
            return inventory.map(inv -> ResponseEntity.ok(ApiResponse.success(inv)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.notFound("原材料库存不存在")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取原材料库存失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterialInventory>> updateRawMaterialInventory(@PathVariable Long id, @RequestBody RawMaterialInventory inventory) {
        try {
            inventory.setId(id);
            RawMaterialInventory updatedInventory = rawMaterialInventoryService.updateRawMaterialInventory(inventory);
            return updatedInventory != null ? ResponseEntity.ok(ApiResponse.success("原材料库存更新成功", updatedInventory))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("原材料库存不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料库存更新失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<RawMaterialInventory>> updateRawMaterialInventoryQuantity(@PathVariable Long id, @RequestParam BigDecimal quantity) {
        try {
            RawMaterialInventory updatedInventory = rawMaterialInventoryService.updateRawMaterialInventoryQuantity(id, quantity);
            return updatedInventory != null ? ResponseEntity.ok(ApiResponse.success("原材料库存数量更新成功", updatedInventory))
                    : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("原材料库存不存在"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料库存数量更新失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRawMaterialInventory(@PathVariable Long id) {
        try {
            rawMaterialInventoryService.deleteRawMaterialInventory(id);
            return ResponseEntity.ok(ApiResponse.success("原材料库存删除成功", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料库存删除失败: " + e.getMessage()));
        }
    }

    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<RawMaterialInventory>>> getLowStockRawMaterialInventory() {
        try {
            List<RawMaterialInventory> lowStockInventory = rawMaterialInventoryService.getLowStockRawMaterialInventory();
            return ResponseEntity.ok(ApiResponse.success(lowStockInventory));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取低库存原材料失败: " + e.getMessage()));
        }
    }

    @GetMapping("/low-stock/store/{storeId}")
    public ResponseEntity<ApiResponse<List<RawMaterialInventory>>> getLowStockRawMaterialInventoryByStoreId(@PathVariable Long storeId) {
        try {
            List<RawMaterialInventory> lowStockInventory = rawMaterialInventoryService.getLowStockRawMaterialInventoryByStoreId(storeId);
            return ResponseEntity.ok(ApiResponse.success(lowStockInventory));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取低库存原材料失败: " + e.getMessage()));
        }
    }
}

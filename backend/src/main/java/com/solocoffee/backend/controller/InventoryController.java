package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.dto.InventoryDTO;
import com.solocoffee.backend.entity.Inventory;
import com.solocoffee.backend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    @PostMapping
    public ResponseEntity<ApiResponse<Inventory>> createInventory(@RequestBody Inventory inventory) {
        Inventory createdInventory = inventoryService.createInventory(inventory);
        return ResponseEntity.ok(ApiResponse.success("库存创建成功", createdInventory));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryById(@PathVariable Long id) {
        Optional<Inventory> inventory = inventoryService.getInventoryById(id);
        return inventory.map(inv -> ResponseEntity.ok(ApiResponse.success(inv)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InventoryDTO>>> getAllInventory() {
        List<InventoryDTO> inventoryList = inventoryService.getAllInventoryDTOs();
        return ResponseEntity.ok(ApiResponse.success(inventoryList));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Inventory>> updateInventory(@PathVariable Long id,
            @RequestBody Inventory inventory) {
        inventory.setId(id);
        Inventory updatedInventory = inventoryService.updateInventory(inventory);
        return updatedInventory != null ? ResponseEntity.ok(ApiResponse.success("库存更新成功", updatedInventory))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在"));
    }

    @PutMapping("/{id}/quantity")
    public ResponseEntity<ApiResponse<Inventory>> updateInventoryQuantity(@PathVariable Long id,
            @RequestParam BigDecimal quantity) {
        Inventory updatedInventory = inventoryService.updateInventoryQuantity(id, quantity);
        return updatedInventory != null ? ResponseEntity.ok(ApiResponse.success("库存数量更新成功", updatedInventory))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在"));
    }

    @GetMapping("/warning")
    public ResponseEntity<ApiResponse<List<Inventory>>> getLowInventory() {
        List<Inventory> lowInventory = inventoryService.getLowInventory();
        return ResponseEntity.ok(ApiResponse.success("低库存查询成功", lowInventory));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> getInventoryByProductId(@PathVariable Long productId) {
        Optional<Inventory> inventory = inventoryService.getInventoryByProductId(productId);
        return inventory.map(inv -> ResponseEntity.ok(ApiResponse.success(inv)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在")));
    }

    @PutMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<Inventory>> updateInventoryByProductId(@PathVariable Long productId,
            @RequestBody Inventory inventory) {
        inventory.setProductId(productId);
        Inventory updatedInventory = inventoryService.updateInventoryByProductId(inventory);
        return updatedInventory != null ? ResponseEntity.ok(ApiResponse.success("库存更新成功", updatedInventory))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.ok(ApiResponse.success("库存删除成功", null));
    }

    @PostMapping("/reduce")
    public ResponseEntity<ApiResponse<Inventory>> reduceInventory(@RequestBody java.util.Map<String, Object> request) {
        Long productId = Long.valueOf(request.get("productId").toString());
        java.math.BigDecimal quantity = java.math.BigDecimal
                .valueOf(Double.valueOf(request.get("quantity").toString()));

        boolean success = inventoryService.deductInventory(productId, quantity);
        if (success) {
            // 获取更新后的库存信息
            java.util.Optional<Inventory> updatedInventory = inventoryService.getInventoryByProductId(productId);
            return updatedInventory.map(inv -> ResponseEntity.ok(ApiResponse.success("库存减少成功", inv)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("库存不存在")));
        } else {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("库存不足或操作失败"));
        }
    }

    @PostMapping("/auto-reorder")
    public ResponseEntity<ApiResponse<List<Inventory>>> autoReorder() {
        List<Inventory> reorderedInventory = inventoryService.processAutoReorder();
        return ResponseEntity.ok(ApiResponse.success("自动补货完成", reorderedInventory));
    }
}
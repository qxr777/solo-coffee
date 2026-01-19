package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.RawMaterial;
import com.solocoffee.backend.repository.RawMaterialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/raw-materials")
public class RawMaterialController {

    @Autowired
    private RawMaterialRepository rawMaterialRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<RawMaterial>> createRawMaterial(@RequestBody RawMaterial rawMaterial) {
        try {
            // 设置创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            rawMaterial.setCreatedAt(now);
            rawMaterial.setUpdatedAt(now);
            
            // 生成物料编号
            if (rawMaterial.getMaterialNo() == null || rawMaterial.getMaterialNo().isEmpty()) {
                rawMaterial.setMaterialNo("MAT" + System.currentTimeMillis());
            }
            
            RawMaterial createdRawMaterial = rawMaterialRepository.save(rawMaterial);
            return ResponseEntity.ok(ApiResponse.success("原材料创建成功", createdRawMaterial));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterial>> getRawMaterialById(@PathVariable Long id) {
        Optional<RawMaterial> rawMaterial = rawMaterialRepository.findById(id);
        return rawMaterial.map(rm -> ResponseEntity.ok(ApiResponse.success(rm)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("原材料不存在")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<RawMaterial>>> getAllRawMaterials() {
        List<RawMaterial> rawMaterials = rawMaterialRepository.findAll();
        return ResponseEntity.ok(ApiResponse.success(rawMaterials));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RawMaterial>> updateRawMaterial(@PathVariable Long id, @RequestBody RawMaterial rawMaterial) {
        try {
            Optional<RawMaterial> existingRawMaterialOpt = rawMaterialRepository.findById(id);
            if (!existingRawMaterialOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("原材料不存在"));
            }
            
            RawMaterial existingRawMaterial = existingRawMaterialOpt.get();
            existingRawMaterial.setName(rawMaterial.getName());
            existingRawMaterial.setCategory(rawMaterial.getCategory());
            existingRawMaterial.setUnit(rawMaterial.getUnit());
            existingRawMaterial.setStatus(rawMaterial.getStatus());
            existingRawMaterial.setSupplierId(rawMaterial.getSupplierId());
            existingRawMaterial.setDescription(rawMaterial.getDescription());
            existingRawMaterial.setUpdatedAt(LocalDateTime.now());
            
            RawMaterial updatedRawMaterial = rawMaterialRepository.save(existingRawMaterial);
            return ResponseEntity.ok(ApiResponse.success("原材料更新成功", updatedRawMaterial));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料更新失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRawMaterial(@PathVariable Long id) {
        try {
            Optional<RawMaterial> rawMaterial = rawMaterialRepository.findById(id);
            if (!rawMaterial.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("原材料不存在"));
            }
            
            rawMaterialRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("原材料删除成功", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("原材料删除失败: " + e.getMessage()));
        }
    }
}

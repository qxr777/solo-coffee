package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.ProductBOM;
import com.solocoffee.backend.repository.ProductBOMRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/product-bom")
public class ProductBOMController {

    @Autowired
    private ProductBOMRepository productBOMRepository;

    @PostMapping
    public ResponseEntity<ApiResponse<ProductBOM>> createProductBOM(@RequestBody ProductBOM productBOM) {
        try {
            // 设置创建和更新时间
            LocalDateTime now = LocalDateTime.now();
            productBOM.setCreatedAt(now);
            productBOM.setUpdatedAt(now);
            
            ProductBOM createdBOM = productBOMRepository.save(productBOM);
            return ResponseEntity.ok(ApiResponse.success("产品BOM创建成功", createdBOM));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("产品BOM创建失败: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductBOM>> getProductBOMById(@PathVariable Long id) {
        try {
            Optional<ProductBOM> bom = productBOMRepository.findById(id);
            return bom.map(b -> ResponseEntity.ok(ApiResponse.success(b)))
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(ApiResponse.notFound("产品BOM不存在")));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取产品BOM失败: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<ApiResponse<List<ProductBOM>>> getProductBOMByProductId(@PathVariable Long productId) {
        try {
            List<ProductBOM> bomList = productBOMRepository.findByProductId(productId);
            return ResponseEntity.ok(ApiResponse.success(bomList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取产品BOM失败: " + e.getMessage()));
        }
    }

    @GetMapping("/material/{materialId}")
    public ResponseEntity<ApiResponse<List<ProductBOM>>> getProductBOMByMaterialId(@PathVariable Long materialId) {
        try {
            List<ProductBOM> bomList = productBOMRepository.findByMaterialId(materialId);
            return ResponseEntity.ok(ApiResponse.success(bomList));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("获取产品BOM失败: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductBOM>> updateProductBOM(@PathVariable Long id, @RequestBody ProductBOM productBOM) {
        try {
            Optional<ProductBOM> existingBOMOpt = productBOMRepository.findById(id);
            if (!existingBOMOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("产品BOM不存在"));
            }
            
            ProductBOM existingBOM = existingBOMOpt.get();
            existingBOM.setProductId(productBOM.getProductId());
            existingBOM.setMaterialId(productBOM.getMaterialId());
            existingBOM.setQuantity(productBOM.getQuantity());
            existingBOM.setUnit(productBOM.getUnit());
            existingBOM.setIsMain(productBOM.getIsMain());
            existingBOM.setDescription(productBOM.getDescription());
            existingBOM.setUpdatedAt(LocalDateTime.now());
            
            ProductBOM updatedBOM = productBOMRepository.save(existingBOM);
            return ResponseEntity.ok(ApiResponse.success("产品BOM更新成功", updatedBOM));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("产品BOM更新失败: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProductBOM(@PathVariable Long id) {
        try {
            Optional<ProductBOM> bom = productBOMRepository.findById(id);
            if (!bom.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.notFound("产品BOM不存在"));
            }
            
            productBOMRepository.deleteById(id);
            return ResponseEntity.ok(ApiResponse.success("产品BOM删除成功", null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.internalError("产品BOM删除失败: " + e.getMessage()));
        }
    }
}

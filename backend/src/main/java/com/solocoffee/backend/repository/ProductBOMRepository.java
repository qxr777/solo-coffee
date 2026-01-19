package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.ProductBOM;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductBOMRepository extends JpaRepository<ProductBOM, Long> {
    List<ProductBOM> findByProductId(Long productId);
    List<ProductBOM> findByMaterialId(Long materialId);
}
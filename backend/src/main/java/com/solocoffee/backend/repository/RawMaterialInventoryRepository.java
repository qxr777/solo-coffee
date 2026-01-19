package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.RawMaterialInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RawMaterialInventoryRepository extends JpaRepository<RawMaterialInventory, Long> {
    List<RawMaterialInventory> findByStoreId(Long storeId);
    Optional<RawMaterialInventory> findByStoreIdAndMaterialId(Long storeId, Long materialId);
    List<RawMaterialInventory> findByQuantityLessThanEqual(Double threshold);
    List<RawMaterialInventory> findByStoreIdAndQuantityLessThanEqual(Long storeId, Double threshold);
}
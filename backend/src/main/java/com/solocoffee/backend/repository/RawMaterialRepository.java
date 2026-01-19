package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.RawMaterial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RawMaterialRepository extends JpaRepository<RawMaterial, Long> {
    List<RawMaterial> findByStatus(Integer status);
    List<RawMaterial> findByCategory(String category);
    List<RawMaterial> findBySupplierId(Long supplierId);
}
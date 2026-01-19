package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    Optional<Inventory> findByProductId(Long productId);

    @org.springframework.data.jpa.repository.Query("SELECT COUNT(i) FROM Inventory i WHERE i.quantity <= i.warningThreshold")
    long countLowStock();
}
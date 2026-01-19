package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    java.util.List<Order> findByCreatedAtAfter(java.time.LocalDateTime createdAt);

    long countByCreatedAtAfter(java.time.LocalDateTime createdAt);
}
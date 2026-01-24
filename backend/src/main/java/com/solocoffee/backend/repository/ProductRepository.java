package com.solocoffee.backend.repository;

import com.solocoffee.backend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByCategoryId(Long categoryId);

    List<Product> findByStatus(Integer status);

    List<Product> findByNameContainingIgnoreCaseOrProductNoContainingIgnoreCase(String name, String productNo);

    java.util.Optional<Product> findByProductNo(String productNo);
}
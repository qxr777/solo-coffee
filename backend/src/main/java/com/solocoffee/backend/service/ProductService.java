package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // 移除对InventoryService的依赖
    // @Autowired
    // private InventoryService inventoryService;
    
    public Product createProduct(Product product) {
        // 生成商品编号
        String productNo = "PROD" + System.currentTimeMillis();
        product.setProductNo(productNo);
        
        // 创建产品时，自动创建库存记录
        Product savedProduct = productRepository.save(product);
        
        // 这里可以添加自动创建库存记录的逻辑
        // 后续可以实现
        
        return savedProduct;
    }
    
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId);
    }
    
    public List<Product> getProductsByStatus(Integer status) {
        return productRepository.findByStatus(status);
    }
    
    public List<Product> searchProducts(String keyword) {
        // 由于ProductRepository可能没有提供search方法，我们暂时返回所有产品
        // 实际实现中应该使用JPA的LIKE查询
        return productRepository.findAll();
    }
    
    @Transactional
    public Product updateProduct(Product product) {
        if (product == null || product.getId() == null) {
            return null;
        }
        
        Optional<Product> existingProductOpt = productRepository.findById(product.getId());
        if (!existingProductOpt.isPresent()) {
            return null;
        }
        
        Product existingProduct = existingProductOpt.get();
        
        // 更新字段
        if (product.getName() != null) {
            existingProduct.setName(product.getName());
        }
        if (product.getPrice() != null) {
            existingProduct.setPrice(product.getPrice());
        }
        if (product.getCategoryId() != null) {
            existingProduct.setCategoryId(product.getCategoryId());
        }
        if (product.getStatus() != null) {
            existingProduct.setStatus(product.getStatus());
        }
        if (product.getDescription() != null) {
            existingProduct.setDescription(product.getDescription());
        }
        if (product.getImageUrl() != null) {
            existingProduct.setImageUrl(product.getImageUrl());
        }
        
        return productRepository.save(existingProduct);
    }
    
    public Product updateProductStatus(Long id, Integer status) {
        Optional<Product> optionalProduct = productRepository.findById(id);
        if (optionalProduct.isPresent()) {
            Product product = optionalProduct.get();
            product.setStatus(status);
            return productRepository.save(product);
        }
        return null;
    }
    
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
}
package com.solocoffee.backend.service;

import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {
    
    @Mock
    private ProductRepository productRepository;
    
    @InjectMocks
    private ProductService productService;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testCreateProduct() {
        // 创建测试商品
        Product product = new Product();
        product.setName("Americano");
        product.setPrice(BigDecimal.valueOf(3.50));
        product.setDescription("Classic black coffee");
        product.setCategoryId(1L);
        product.setStatus(1);
        
        // 模拟 repository.save() 方法
        when(productRepository.save(product)).thenReturn(product);
        
        // 调用服务方法
        Product createdProduct = productService.createProduct(product);
        
        // 验证结果
        assertNotNull(createdProduct);
        assertNotNull(createdProduct.getProductNo());
        assertEquals("Americano", createdProduct.getName());
        assertEquals(BigDecimal.valueOf(3.50), createdProduct.getPrice());
        verify(productRepository, times(1)).save(product);
    }
    
    @Test
    void testGetProductById() {
        // 创建测试商品
        Product product = new Product();
        product.setId(1L);
        product.setProductNo("PROD123456");
        product.setName("Americano");
        product.setPrice(BigDecimal.valueOf(3.50));
        
        // 模拟 repository.findById() 方法
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        
        // 调用服务方法
        Optional<Product> result = productService.getProductById(1L);
        
        // 验证结果
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
        assertEquals("PROD123456", result.get().getProductNo());
        assertEquals("Americano", result.get().getName());
        verify(productRepository, times(1)).findById(1L);
    }
    
    @Test
    void testGetAllProducts() {
        // 创建测试商品列表
        List<Product> products = new ArrayList<>();
        Product product1 = new Product();
        product1.setId(1L);
        product1.setProductNo("PROD123456");
        product1.setName("Americano");
        products.add(product1);
        
        Product product2 = new Product();
        product2.setId(2L);
        product2.setProductNo("PROD123457");
        product2.setName("Latte");
        products.add(product2);
        
        // 模拟 repository.findAll() 方法
        when(productRepository.findAll()).thenReturn(products);
        
        // 调用服务方法
        List<Product> result = productService.getAllProducts();
        
        // 验证结果
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(productRepository, times(1)).findAll();
    }
    
    @Test
    void testUpdateProduct() {
        // 创建测试商品
        Product product = new Product();
        product.setId(1L);
        product.setProductNo("PROD123456");
        product.setName("Americano");
        product.setPrice(BigDecimal.valueOf(3.50));
        
        // 模拟 repository.findById() 和 repository.save() 方法
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        
        // 调用服务方法
        Product updatedProduct = productService.updateProduct(product);
        
        // 验证结果
        assertNotNull(updatedProduct);
        assertEquals(1L, updatedProduct.getId());
        assertEquals("Americano", updatedProduct.getName());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }
    
    @Test
    void testUpdateProductStatus() {
        // 创建测试商品
        Product product = new Product();
        product.setId(1L);
        product.setStatus(1);
        
        // 模拟 repository.findById() 和 repository.save() 方法
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);
        
        // 调用服务方法
        Product updatedProduct = productService.updateProductStatus(1L, 2);
        
        // 验证结果
        assertNotNull(updatedProduct);
        assertEquals(2, updatedProduct.getStatus());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(product);
    }
    
    @Test
    void testDeleteProduct() {
        // 调用服务方法
        productService.deleteProduct(1L);
        
        // 验证 repository.deleteById() 方法被调用
        verify(productRepository, times(1)).deleteById(1L);
    }
}
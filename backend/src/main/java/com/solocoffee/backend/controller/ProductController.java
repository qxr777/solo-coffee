package com.solocoffee.backend.controller;

import com.solocoffee.backend.common.ApiResponse;
import com.solocoffee.backend.entity.Product;
import com.solocoffee.backend.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<ApiResponse<Product>> createProduct(@RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        return ResponseEntity.ok(ApiResponse.success("商品创建成功", createdProduct));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.getProductById(id);
        return product.map(p -> ResponseEntity.ok(ApiResponse.success(p)))
                .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("商品不存在")));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllProducts(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Integer status) {
        // 由于ProductService暂无带参数的获取方法，此处封装一个简单的模拟分页逻辑，或调用搜索接口
        // 实际上建议在ProductService中实现getProductsWithFilter
        // 为了快速修复，我们先实现一个简单的过滤
        List<Product> allProducts = productService.getAllProducts();
        java.util.stream.Stream<Product> stream = allProducts.stream();

        if (keyword != null && !keyword.isEmpty()) {
            stream = stream.filter(p -> p.getName().contains(keyword) || p.getProductNo().contains(keyword));
        }
        if (categoryId != null) {
            stream = stream.filter(p -> p.getCategoryId().equals(categoryId));
        }
        if (status != null) {
            stream = stream.filter(p -> p.getStatus().equals(status));
        }

        List<Product> filtered = stream.collect(java.util.stream.Collectors.toList());
        int total = filtered.size();
        int start = (page - 1) * size;
        int end = Math.min(start + size, total);
        List<Product> paginated = start < total ? filtered.subList(start, end) : new java.util.ArrayList<>();

        java.util.Map<String, Object> response = new java.util.HashMap<>();
        response.put("records", paginated);
        response.put("total", total);
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Product>> updateProduct(@PathVariable Long id, @RequestBody Product product) {
        if (product == null) {
            return ResponseEntity.badRequest().body(ApiResponse.badRequest("产品信息不能为空"));
        }
        product.setId(id);
        Product updatedProduct = productService.updateProduct(product);
        return updatedProduct != null ? ResponseEntity.ok(ApiResponse.success("产品更新成功", updatedProduct))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("产品不存在"));
    }

    @PutMapping("/{id}/status/{status}")
    public ResponseEntity<ApiResponse<Product>> updateProductStatus(@PathVariable Long id,
            @PathVariable Integer status) {
        Product updatedProduct = productService.updateProductStatus(id, status);
        return updatedProduct != null ? ResponseEntity.ok(ApiResponse.success("商品状态更新成功", updatedProduct))
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.notFound("商品不存在"));
    }

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<ApiResponse<List<Product>>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<Product> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(ApiResponse.success("按分类查询商品成功", products));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Product>>> searchProducts(@RequestParam String keyword) {
        List<Product> products = productService.searchProducts(keyword);
        return ResponseEntity.ok(ApiResponse.success("商品搜索成功", products));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("商品删除成功", null));
    }
}
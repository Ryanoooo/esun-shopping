package com.esun.shopping.controller;

import com.esun.shopping.common.ApiResponse;
import com.esun.shopping.dto.ProductDto;
import com.esun.shopping.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/in-stock")
    public ResponseEntity<ApiResponse<List<ProductDto>>> getProductsInStock() {
        List<ProductDto> products = productService.getProductsInStock();
        return ResponseEntity.ok(ApiResponse.success("查詢成功", products));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ApiResponse<ProductDto>> getById(@PathVariable String productId) {
        ProductDto product = productService.getById(productId);
        if (product == null) {
            return ResponseEntity.ok(ApiResponse.error("404", "商品不存在"));
        }
        return ResponseEntity.ok(ApiResponse.success("查詢成功", product));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<ProductDto>>> search(
            @RequestParam String keyword) {
        List<ProductDto> products = productService.search(keyword);
        return ResponseEntity.ok(ApiResponse.success("查詢成功", products));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> addProduct(
            @Valid @RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        return ResponseEntity.ok(ApiResponse.success("商品新增成功", productDto));
    }
}
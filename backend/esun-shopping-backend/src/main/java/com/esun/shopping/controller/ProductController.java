package com.esun.shopping.controller;

import com.esun.shopping.common.ApiResponse;
import com.esun.shopping.common.dto.ProductDto;
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

    @PostMapping
    public ResponseEntity<ApiResponse<ProductDto>> addProduct(
            @Valid @RequestBody ProductDto productDto) {
        productService.addProduct(productDto);
        return ResponseEntity.ok(ApiResponse.success("商品新增成功", productDto));
    }
}
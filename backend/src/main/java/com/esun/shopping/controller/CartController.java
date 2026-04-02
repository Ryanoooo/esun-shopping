package com.esun.shopping.controller;

import com.esun.shopping.common.ApiResponse;
import com.esun.shopping.dto.CartDto;
import com.esun.shopping.dto.CartItemRequest;
import com.esun.shopping.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<CartDto>> getCart(
            @AuthenticationPrincipal String memberId) {
        CartDto cart = cartService.getCart(memberId);
        return ResponseEntity.ok(ApiResponse.success("查詢成功", cart));
    }

    @PostMapping("/items")
    public ResponseEntity<ApiResponse<CartDto>> addItem(
            @AuthenticationPrincipal String memberId,
            @Valid @RequestBody CartItemRequest request) {
        CartDto cart = cartService.addItem(memberId, request);
        return ResponseEntity.ok(ApiResponse.success("已加入購物車", cart));
    }

    @DeleteMapping("/items/{productId}")
    public ResponseEntity<ApiResponse<Void>> removeItem(
            @AuthenticationPrincipal String memberId,
            @PathVariable String productId) {
        cartService.removeItem(memberId, productId);
        return ResponseEntity.ok(ApiResponse.success("已移除商品", null));
    }
}

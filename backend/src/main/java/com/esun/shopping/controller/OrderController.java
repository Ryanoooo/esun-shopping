package com.esun.shopping.controller;

import com.esun.shopping.common.ApiResponse;
import com.esun.shopping.dto.CreateOrderRequest;
import com.esun.shopping.dto.OrderDto;
import com.esun.shopping.service.CartService;
import com.esun.shopping.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
public class OrderController {

    private final OrderService orderService;
    private final CartService cartService;

    public OrderController(OrderService orderService, CartService cartService) {
        this.orderService = orderService;
        this.cartService = cartService;
    }

    // 會員：查自己的訂單
    @GetMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getMyOrders(
            @AuthenticationPrincipal String memberId) {
        List<OrderDto> orders = orderService.getMyOrders(memberId);
        return ResponseEntity.ok(ApiResponse.success("查詢成功", orders));
    }

    // 會員：查單筆訂單
    @GetMapping("/api/v1/orders/{orderId}")
    public ResponseEntity<ApiResponse<OrderDto>> getById(@PathVariable String orderId) {
        OrderDto order = orderService.getById(orderId);
        return ResponseEntity.ok(ApiResponse.success("查詢成功", order));
    }

    // 會員：建立訂單
    @PostMapping("/api/v1/orders")
    public ResponseEntity<ApiResponse<OrderDto>> createOrder(
            @AuthenticationPrincipal String memberId,
            @Valid @RequestBody CreateOrderRequest request) {
        request.setMemberId(memberId);
        OrderDto order = orderService.createOrder(request);
        cartService.clearCart(memberId);
        return ResponseEntity.ok(ApiResponse.success("訂單建立成功", order));
    }

    // 管理者：查所有訂單（含會員名稱與明細）
    @GetMapping("/api/v1/admin/orders")
    public ResponseEntity<ApiResponse<List<OrderDto>>> getAllOrders() {
        List<OrderDto> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ApiResponse.success("查詢成功", orders));
    }

    // 管理者：更新付款狀態
    @PatchMapping("/api/v1/admin/orders/{orderId}/pay-status")
    public ResponseEntity<ApiResponse<Void>> updatePayStatus(
            @PathVariable String orderId,
            @RequestParam int payStatus) {
        if (payStatus != 0 && payStatus != 1) {
            throw new IllegalArgumentException("付款狀態只能為 0（未付款）或 1（已付款）");
        }
        orderService.updatePayStatus(orderId, payStatus);
        return ResponseEntity.ok(ApiResponse.success("付款狀態更新成功", null));
    }
}
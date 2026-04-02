package com.esun.shopping.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {

    private String memberId;  // 由 JWT 注入，不需要前端傳

    @Valid
    @NotEmpty(message = "訂單至少需要一個商品")
    private List<OrderItemDto> items;
}
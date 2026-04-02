package com.esun.shopping.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class CartItemRequest {

    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @NotNull(message = "數量不能為空")
    @Min(value = 1, message = "數量至少為1")
    private Integer quantity;
}

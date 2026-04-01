package com.esun.shopping.common.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class OrderItemDto {

    @NotBlank(message = "商品編號不能為空")
    private String productId;

    @NotNull(message = "購買數量不能為空")
    @Min(value = 1, message = "購買數量至少為1")
    private Integer quantity;
}
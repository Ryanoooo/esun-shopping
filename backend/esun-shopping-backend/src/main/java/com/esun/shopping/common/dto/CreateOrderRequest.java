package com.esun.shopping.common.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class CreateOrderRequest {

    @NotBlank(message = "會員編號不能為空")
    private String memberId;

    @Valid
    @NotEmpty(message = "訂單至少需要一個商品")
    private List<OrderItemDto> items;
}
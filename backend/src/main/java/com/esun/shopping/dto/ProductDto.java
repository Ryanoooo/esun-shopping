package com.esun.shopping.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductDto {

    @NotBlank(message = "商品編號不能為空")
    @Size(max = 10, message = "商品編號最多10個字元")
    @Pattern(regexp = "^[A-Za-z0-9]+$", message = "商品編號只能包含英數字")
    private String productId;

    @NotBlank(message = "商品名稱不能為空")
    @Size(max = 100, message = "商品名稱最多100個字元")
    private String productName;

    @NotNull(message = "售價不能為空")
    @DecimalMin(value = "0.0", inclusive = true, message = "售價不能小於0")
    private BigDecimal price;

    @NotNull(message = "庫存不能為空")
    @Min(value = 0, message = "庫存不能小於0")
    private Integer quantity;
}
package com.esun.shopping.entity;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItem {
    private String productId;
    private String productName;
    private BigDecimal price;
    private Integer quantity;
    private BigDecimal subtotal;
}

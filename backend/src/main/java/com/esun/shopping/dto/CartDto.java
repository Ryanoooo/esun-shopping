package com.esun.shopping.dto;

import com.esun.shopping.entity.CartItem;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class CartDto {
    private List<CartItem> items;
    private BigDecimal totalAmount;
}

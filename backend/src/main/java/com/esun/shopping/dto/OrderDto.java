package com.esun.shopping.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto {

    private String orderId;
    private String memberId;
    private String memberName;
    private BigDecimal totalPrice;
    private Integer payStatus;
    private String createdAt;
    private List<OrderDetailItemDto> items;

    @Data
    public static class OrderDetailItemDto {
        private Integer orderItemSn;
        private String productId;
        private String productName;
        private Integer quantity;
        private BigDecimal standPrice;
        private BigDecimal itemPrice;
    }
}
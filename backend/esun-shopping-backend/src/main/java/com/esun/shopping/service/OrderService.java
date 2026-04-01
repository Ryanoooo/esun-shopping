package com.esun.shopping.service;

import com.esun.shopping.common.dto.CreateOrderRequest;
import com.esun.shopping.common.dto.OrderDto;
import com.esun.shopping.repository.OrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<OrderDto> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional
    public OrderDto createOrder(CreateOrderRequest request) {
        String orderId = generateOrderId();

        Map<String, Object> result = orderRepository.createOrder(
            orderId, request.getMemberId(), request
        );

        Boolean success = (Boolean) result.get("p_success");
        String message  = (String)  result.get("p_message");

        if (!Boolean.TRUE.equals(success)) {
            throw new IllegalArgumentException(message);
        }

        OrderDto dto = new OrderDto();
        dto.setOrderId(orderId);
        dto.setMemberId(request.getMemberId());
        dto.setPayStatus(0);
        return dto;
    }

    private String generateOrderId() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = new Random().nextInt(900000) + 100000;
        return "Ms" + date + random;
    }
}
package com.esun.shopping.service;

import com.esun.shopping.dto.CreateOrderRequest;
import com.esun.shopping.dto.OrderDto;
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

    public List<OrderDto> getMyOrders(String memberId) {
        return orderRepository.findByMemberId(memberId);
    }

    public OrderDto getById(String orderId) {
        OrderDto order = orderRepository.findById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("訂單不存在: " + orderId);
        }
        return order;
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

    public void updatePayStatus(String orderId, int payStatus) {
        orderRepository.updatePayStatus(orderId, payStatus);
    }

    private String generateOrderId() {
        String date = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        int random = new Random().nextInt(900000) + 100000;
        return "Ms" + date + random;
    }
}
package com.esun.shopping.service;

import com.esun.shopping.dto.CartDto;
import com.esun.shopping.dto.CartItemRequest;
import com.esun.shopping.dto.ProductDto;
import com.esun.shopping.entity.CartItem;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CartService {

    private final StringRedisTemplate redisTemplate;
    private final ProductService productService;

    public CartService(StringRedisTemplate redisTemplate, ProductService productService) {
        this.redisTemplate = redisTemplate;
        this.productService = productService;
    }

    private String cartKey(String memberId) {
        return "cart:" + memberId;
    }

    public CartDto getCart(String memberId) {
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(cartKey(memberId));
        return buildCartDto(entries);
    }

    public CartDto addItem(String memberId, CartItemRequest request) {
        ProductDto product = productService.getById(request.getProductId());
        if (product == null) {
            throw new IllegalArgumentException("商品不存在: " + request.getProductId());
        }
        if (product.getQuantity() < request.getQuantity()) {
            throw new IllegalArgumentException("商品庫存不足");
        }

        redisTemplate.opsForHash().put(
            cartKey(memberId),
            request.getProductId(),
            String.valueOf(request.getQuantity())
        );

        return getCart(memberId);
    }

    public void removeItem(String memberId, String productId) {
        redisTemplate.opsForHash().delete(cartKey(memberId), productId);
    }

    public void clearCart(String memberId) {
        redisTemplate.delete(cartKey(memberId));
    }

    private CartDto buildCartDto(Map<Object, Object> entries) {
        List<CartItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String productId = (String) entry.getKey();
            int quantity = Integer.parseInt((String) entry.getValue());

            ProductDto product = productService.getById(productId);
            if (product == null) continue;

            CartItem item = new CartItem();
            item.setProductId(productId);
            item.setProductName(product.getProductName());
            item.setPrice(product.getPrice());
            item.setQuantity(quantity);
            item.setSubtotal(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
            items.add(item);

            total = total.add(item.getSubtotal());
        }

        CartDto dto = new CartDto();
        dto.setItems(items);
        dto.setTotalAmount(total);
        return dto;
    }
}

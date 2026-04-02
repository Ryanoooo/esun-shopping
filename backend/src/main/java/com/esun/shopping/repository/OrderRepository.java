package com.esun.shopping.repository;

import com.esun.shopping.dto.CreateOrderRequest;
import com.esun.shopping.dto.OrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    public OrderRepository(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    // 呼叫 sp_create_order（PROCEDURE 含 OUT 參數）
    // 用 execute + CallableStatement 手動處理 OUT 參數
    public Map<String, Object> createOrder(String orderId,
                                           String memberId,
                                           CreateOrderRequest request) {
        try {
            String itemsJson = objectMapper.writeValueAsString(request.getItems());

            // CALL 語法：5 個參數（3 IN + 2 OUT）
            String sql = "CALL sp_create_order(?, ?, ?::jsonb, ?, ?)";

            Map<String, Object> result = new HashMap<>();

            jdbcTemplate.execute((java.sql.Connection conn) -> {
                // 用 CallableStatement 執行，可以取得 OUT 參數
                CallableStatement cs = conn.prepareCall(sql);

                // 設定 IN 參數
                cs.setString(1, orderId);
                cs.setString(2, memberId);
                cs.setString(3, itemsJson);

                // 註冊 OUT 參數
                cs.registerOutParameter(4, Types.BOOLEAN); // p_success
                cs.registerOutParameter(5, Types.VARCHAR); // p_message

                cs.execute();

                // 取得 OUT 參數結果
                result.put("p_success", cs.getBoolean(4));
                result.put("p_message", cs.getString(5));

                return null;
            });

            return result;

        } catch (Exception e) {
            throw new RuntimeException("建立訂單失敗: " + e.getMessage(), e);
        }
    }

    public List<OrderDto> findAll() {
        String sql = "SELECT o.order_id, o.member_id, m.member_name, o.total_price, o.pay_status, " +
                     "TO_CHAR(o.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
                     "d.order_item_sn, d.product_id, p.product_name, " +
                     "d.quantity, d.stand_price, d.item_price " +
                     "FROM orders o " +
                     "JOIN member m ON o.member_id = m.member_id " +
                     "LEFT JOIN order_detail d ON o.order_id = d.order_id " +
                     "LEFT JOIN product p ON d.product_id = p.product_id " +
                     "ORDER BY o.created_at DESC, d.order_item_sn";

        return jdbcTemplate.query(sql, rs -> {
            Map<String, OrderDto> orderMap = new LinkedHashMap<>();
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                if (!orderMap.containsKey(orderId)) {
                    OrderDto dto = new OrderDto();
                    dto.setOrderId(orderId);
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setMemberName(rs.getString("member_name"));
                    dto.setTotalPrice(rs.getBigDecimal("total_price"));
                    dto.setPayStatus(rs.getInt("pay_status"));
                    dto.setCreatedAt(rs.getString("created_at"));
                    dto.setItems(new ArrayList<>());
                    orderMap.put(orderId, dto);
                }
                if (rs.getString("product_id") != null) {
                    orderMap.get(orderId).getItems().add(mapItem(rs));
                }
            }
            return new ArrayList<>(orderMap.values());
        });
    }

    public List<OrderDto> findByMemberId(String memberId) {
        String sql = "SELECT o.order_id, o.member_id, o.total_price, o.pay_status, " +
                     "TO_CHAR(o.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
                     "d.order_item_sn, d.product_id, p.product_name, " +
                     "d.quantity, d.stand_price, d.item_price " +
                     "FROM orders o " +
                     "LEFT JOIN order_detail d ON o.order_id = d.order_id " +
                     "LEFT JOIN product p ON d.product_id = p.product_id " +
                     "WHERE o.member_id = ? " +
                     "ORDER BY o.created_at DESC, d.order_item_sn";

        return jdbcTemplate.query(sql, rs -> {
            Map<String, OrderDto> orderMap = new LinkedHashMap<>();
            while (rs.next()) {
                String orderId = rs.getString("order_id");
                if (!orderMap.containsKey(orderId)) {
                    OrderDto dto = new OrderDto();
                    dto.setOrderId(orderId);
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setTotalPrice(rs.getBigDecimal("total_price"));
                    dto.setPayStatus(rs.getInt("pay_status"));
                    dto.setCreatedAt(rs.getString("created_at"));
                    dto.setItems(new ArrayList<>());
                    orderMap.put(orderId, dto);
                }
                if (rs.getString("product_id") != null) {
                    orderMap.get(orderId).getItems().add(mapItem(rs));
                }
            }
            return new ArrayList<>(orderMap.values());
        }, memberId);
    }

    public OrderDto findById(String orderId) {
        String sql = "SELECT o.order_id, o.member_id, o.total_price, o.pay_status, " +
                     "TO_CHAR(o.created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at, " +
                     "d.order_item_sn, d.product_id, p.product_name, " +
                     "d.quantity, d.stand_price, d.item_price " +
                     "FROM orders o " +
                     "JOIN order_detail d ON o.order_id = d.order_id " +
                     "JOIN product p ON d.product_id = p.product_id " +
                     "WHERE o.order_id = ?";

        return jdbcTemplate.query(sql, rs -> {
            OrderDto dto = null;
            java.util.List<OrderDto.OrderDetailItemDto> items = new java.util.ArrayList<>();

            while (rs.next()) {
                if (dto == null) {
                    dto = new OrderDto();
                    dto.setOrderId(rs.getString("order_id"));
                    dto.setMemberId(rs.getString("member_id"));
                    dto.setTotalPrice(rs.getBigDecimal("total_price"));
                    dto.setPayStatus(rs.getInt("pay_status"));
                    dto.setCreatedAt(rs.getString("created_at"));
                }
                OrderDto.OrderDetailItemDto item = new OrderDto.OrderDetailItemDto();
                item.setOrderItemSn(rs.getInt("order_item_sn"));
                item.setProductId(rs.getString("product_id"));
                item.setProductName(rs.getString("product_name"));
                item.setQuantity(rs.getInt("quantity"));
                item.setStandPrice(rs.getBigDecimal("stand_price"));
                item.setItemPrice(rs.getBigDecimal("item_price"));
                items.add(item);
            }

            if (dto != null) dto.setItems(items);
            return dto;
        }, orderId);
    }

    public void updatePayStatus(String orderId, int payStatus) {
        String sql = "UPDATE orders SET pay_status = ?, updated_at = NOW() WHERE order_id = ?";
        jdbcTemplate.update(sql, payStatus, orderId);
    }

    private OrderDto.OrderDetailItemDto mapItem(ResultSet rs) throws SQLException {
        OrderDto.OrderDetailItemDto item = new OrderDto.OrderDetailItemDto();
        item.setOrderItemSn(rs.getInt("order_item_sn"));
        item.setProductId(rs.getString("product_id"));
        item.setProductName(rs.getString("product_name"));
        item.setQuantity(rs.getInt("quantity"));
        item.setStandPrice(rs.getBigDecimal("stand_price"));
        item.setItemPrice(rs.getBigDecimal("item_price"));
        return item;
    }
}
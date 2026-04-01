package com.esun.shopping.repository;

import com.esun.shopping.common.dto.CreateOrderRequest;
import com.esun.shopping.common.dto.OrderDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.CallableStatement;
import java.sql.Types;
import java.util.HashMap;
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

    // 查詢所有訂單
    public List<OrderDto> findAll() {
        String sql = "SELECT order_id, member_id, total_price, pay_status, " +
                     "TO_CHAR(created_at, 'YYYY-MM-DD HH24:MI:SS') AS created_at " +
                     "FROM orders ORDER BY created_at DESC";

        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            OrderDto dto = new OrderDto();
            dto.setOrderId(rs.getString("order_id"));
            dto.setMemberId(rs.getString("member_id"));
            dto.setTotalPrice(rs.getBigDecimal("total_price"));
            dto.setPayStatus(rs.getInt("pay_status"));
            dto.setCreatedAt(rs.getString("created_at"));
            return dto;
        });
    }
}
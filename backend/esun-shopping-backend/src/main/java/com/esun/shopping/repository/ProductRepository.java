package com.esun.shopping.repository;

import com.esun.shopping.common.dto.ProductDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 呼叫 FUNCTION：用 SELECT
    public List<ProductDto> findInStock() {
        String sql = "SELECT * FROM sp_get_products_in_stock()";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            ProductDto dto = new ProductDto();
            dto.setProductId(rs.getString("product_id"));
            dto.setProductName(rs.getString("product_name"));
            dto.setPrice(rs.getBigDecimal("price"));
            dto.setQuantity(rs.getInt("quantity"));
            return dto;
        });
    }

    // 呼叫 PROCEDURE：用 CALL
    public void insertProduct(ProductDto dto) {
        String sql = "CALL sp_insert_product(?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            dto.getProductId(),
            dto.getProductName(),
            dto.getPrice(),
            dto.getQuantity()
        );
    }
}
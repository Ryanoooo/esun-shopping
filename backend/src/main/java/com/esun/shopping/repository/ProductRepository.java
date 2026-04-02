package com.esun.shopping.repository;

import com.esun.shopping.dto.ProductDto;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
public class ProductRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<ProductDto> findInStock() {
        String sql = "SELECT * FROM sp_get_products_in_stock()";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs));
    }

    public ProductDto findById(String productId) {
        String sql = "SELECT product_id, product_name, price, quantity " +
                     "FROM product WHERE product_id = ?";
        return jdbcTemplate.query(sql, rs -> rs.next() ? mapRow(rs) : null, productId);
    }

    public List<ProductDto> search(String keyword) {
        String sql = "SELECT product_id, product_name, price, quantity " +
                     "FROM product WHERE quantity > 0 AND product_name ILIKE ? " +
                     "ORDER BY product_id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> mapRow(rs), "%" + keyword + "%");
    }

    public void insertProduct(ProductDto dto) {
        String sql = "CALL sp_insert_product(?, ?, ?, ?)";
        jdbcTemplate.update(sql,
            dto.getProductId(),
            dto.getProductName(),
            dto.getPrice(),
            dto.getQuantity()
        );
    }

    private ProductDto mapRow(ResultSet rs) throws SQLException {
        ProductDto dto = new ProductDto();
        dto.setProductId(rs.getString("product_id"));
        dto.setProductName(rs.getString("product_name"));
        dto.setPrice(rs.getBigDecimal("price"));
        dto.setQuantity(rs.getInt("quantity"));
        return dto;
    }
}
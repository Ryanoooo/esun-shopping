package com.esun.shopping.service;

import com.esun.shopping.common.dto.ProductDto;
import com.esun.shopping.repository.ProductRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDto> getProductsInStock() {
        return productRepository.findInStock();
    }

    public void addProduct(ProductDto dto) {
        productRepository.insertProduct(dto);
    }
}
package com.esun.shopping.service;

import com.esun.shopping.dto.ProductDto;
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

    public ProductDto getById(String productId) {
        return productRepository.findById(productId);
    }

    public List<ProductDto> search(String keyword) {
        return productRepository.search(keyword);
    }

    public void addProduct(ProductDto dto) {
        productRepository.insertProduct(dto);
    }
}
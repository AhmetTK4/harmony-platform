package com.atk.productservice.service;

import com.atk.productservice.dto.ProductDto;
import com.atk.productservice.entity.Product;

import java.util.List;

public interface ProductService {
    List<ProductDto> getAllProducts();
    ProductDto getProductById(Long id);
    ProductDto createProduct(ProductDto dto);
}

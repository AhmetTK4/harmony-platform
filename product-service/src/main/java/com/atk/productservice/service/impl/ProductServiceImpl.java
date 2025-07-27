package com.atk.productservice.service.impl;

import com.atk.productservice.dto.ProductDto;
import com.atk.productservice.entity.Product;
import com.atk.productservice.repository.ProductRepository;
import com.atk.productservice.service.ProductService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repository;

    public ProductServiceImpl(ProductRepository repository) {
        this.repository = repository;
    }


    @Override
    public List<ProductDto> getAllProducts() {
        return repository.findAll().stream()
                .map(p -> new ProductDto(p.getId(),p.getName(),p.getDescription(), p.getPrice()))
                .toList();
    }

    @Override
    public ProductDto getProductById(Long id) {
        Product p = repository.findById(id).orElseThrow();
        return new ProductDto(p.getId(), p.getName(), p.getDescription(), p.getPrice());
    }

    @Override
    public ProductDto createProduct(ProductDto dto) {
        Product p = Product.builder()
                .name(dto.name())
                .description(dto.description())
                .price(dto.price())
                .build();
        Product saved = repository.save(p);
        return new ProductDto(saved.getId(), saved.getName(), saved.getDescription(), saved.getPrice());
    }
}

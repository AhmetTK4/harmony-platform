package com.atk.productservice.controller;

import com.atk.productservice.dto.ProductDto;
import com.atk.productservice.service.ProductService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService service;

    public ProductController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductDto> getAllProducts(){
        return service.getAllProducts();
    }

    @GetMapping("/{id}")
    public ProductDto getProductById(@PathVariable Long id){
        return service.getProductById(id);
    }

    @PostMapping
    public ProductDto createProduct(@RequestBody ProductDto dto){
        return service.createProduct(dto);
    }
}

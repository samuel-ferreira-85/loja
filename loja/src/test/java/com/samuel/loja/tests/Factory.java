package com.samuel.loja.tests;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.dto.ProductListDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.entities.Product;

import java.math.BigDecimal;
import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1L, "PS5 Ultimate", "The new generation PS5 video game", BigDecimal.valueOf(4987.90),
                "http://img.com.br/ps5.jpg", Instant.parse("2023-12-20T10:00:00Z"));
        product.getCategories().add(new Category(2L, "Eletronicos"));
        return product;
    }

    public static ProductDto createProductDto() {
        Product product = createProduct();
        return new ProductDto(product, product.getCategories());
    }

    public static ProductListDto createProductListDto() {
        Product product = createProduct();
        return new ProductListDto(product);
    }
}

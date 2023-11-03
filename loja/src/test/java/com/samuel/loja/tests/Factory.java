package com.samuel.loja.tests;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.entities.Product;

import java.math.BigDecimal;
import java.time.Instant;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(1l, "Phone", "bom phone", BigDecimal.valueOf(10.90),
                "http://img.com.br/img.jpg", Instant.parse("2023-11-02T01:30:00Z"));
        product.getCategories().add(new Category(1L, "Livros"));
        return product;
    }

    public static ProductDto createProductDto() {
        Product product = createProduct();
        return new ProductDto(product, product.getCategories());
    }
}

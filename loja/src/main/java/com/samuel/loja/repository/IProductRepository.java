package com.samuel.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samuel.loja.entities.Product;

public interface IProductRepository extends JpaRepository<Product, Long> {
    
}

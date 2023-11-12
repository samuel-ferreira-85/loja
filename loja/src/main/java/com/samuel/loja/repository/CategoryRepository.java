package com.samuel.loja.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samuel.loja.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    
}

package com.samuel.loja.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.samuel.loja.entities.Category;

public interface ICategoryRepository extends JpaRepository<Category, UUID> {
    
}

package com.samuel.loja.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.ICategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    private ICategoryRepository categoryRepository;

    public List<Category> findAll() {
        return categoryRepository.findAll();
    }
}

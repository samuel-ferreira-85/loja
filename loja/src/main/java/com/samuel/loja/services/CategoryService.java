package com.samuel.loja.services;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.ICategoryRepository;

@Service
public class CategoryService {
    
    @Autowired
    private ICategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<CategoryDto> findAll() {
        List<Category> list = categoryRepository.findAll();

        return list.stream().map(c -> new CategoryDto(c))
            .collect(Collectors.toList());
    }
}

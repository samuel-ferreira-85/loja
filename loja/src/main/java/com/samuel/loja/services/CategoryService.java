package com.samuel.loja.services;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.ICategoryRepository;
import com.samuel.loja.services.exceptions.EntityNotFoundException;

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

    @Transactional(readOnly = true)
    public CategoryDto findById(UUID id) {
        Optional<Category> categoryOptional = categoryRepository.findById(id);

        Category category = categoryOptional
            .orElseThrow(() -> new EntityNotFoundException("Entity not found."));

        return new CategoryDto(category);
    }
}

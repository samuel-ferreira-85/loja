package com.samuel.loja.services;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.ICategoryRepository;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;

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
        Category category = getCategory(id);

        return new CategoryDto(category);
    }

    @Transactional
    public CategoryDto insert(CategoryDto categoryDto) {
        var category = Category.builder().name(categoryDto.getName()).build();

        Category categorySaved = categoryRepository.save(category);

        return new CategoryDto(categorySaved);
    }

    @Transactional
    public CategoryDto update(UUID id, CategoryDto categoryDto) {
        Category category = getCategory(id);

        BeanUtils.copyProperties(categoryDto, category, "id");

        return new CategoryDto(category);
    }

    public Category getCategory(UUID id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Não há recurso para o id: " + id));
    }
}

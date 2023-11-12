package com.samuel.loja.services;

import java.time.Instant;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.CategoryRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository repository;

    @Transactional(readOnly = true)
    public Page<CategoryDto> findAllPaged(PageRequest pageRequest) {
        Page<Category> list = repository.findAll(pageRequest);

        return list.map(c -> new CategoryDto(c));
    }

    @Transactional(readOnly = true)
    public CategoryDto findById(Long id) {
        Category category = getCategory(id);

        return new CategoryDto(category);
    }

    @Transactional
    public CategoryDto insert(CategoryDto categoryDto) {
        var category = Category.builder()
            .name(categoryDto.getName())
            .createdAt(Instant.now())
            .build();

        Category categorySaved = repository.save(category);

        return new CategoryDto(categorySaved);
    }

    @Transactional
    public CategoryDto update(Long id, CategoryDto categoryDto) {
        Category category = getCategory(id);

        BeanUtils.copyProperties(categoryDto, category, "id");

        return new CategoryDto(category);
    }

    public Category getCategory(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Não há recurso para o id: " + id));
    }

    public void delete(Long id) {        
        
        try {
            Category category = getCategory(id);
            repository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation.");
        } 
    }
}

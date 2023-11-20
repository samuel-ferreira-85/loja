package com.samuel.loja.services;

import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.dto.ProductListDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.repository.CategoryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.entities.Product;
import com.samuel.loja.repository.ProductRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;

import java.util.HashSet;
import java.util.Set;

@Service
@Slf4j
public class ProductService {
    
    @Autowired
    private ProductRepository repository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<ProductListDto> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);

        return list.map(c -> new ProductListDto(c));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Product product = getProduct(id);

        return new ProductDto(product, product.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto productDto) {
        Product product = new Product();
        copyDtoToEntity(productDto, product);

        Product productSaved = repository.save(product);
        return new ProductDto(productSaved, productSaved.getCategories());
    }

    @Transactional
    public ProductDto update(Long id, ProductDto productDto) {
        Product product = getProduct(id);

        BeanUtils.copyProperties(productDto, product, "id");

        return new ProductDto(product, product.getCategories());
    }

    public Product getProduct(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Não há recurso para o id: " + id));
    }

    public void delete(Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
                return;
            } else {
                throw new ResourceNotFoundException("Resource not found.");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation.");
        }
    }

    private void copyDtoToEntity(ProductDto dto, Product entity) {

        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());
        entity.setPrice(dto.getPrice());

        entity.getCategories().clear();
        for (CategoryDto catDto : dto.getCategories()) {
            Category category = categoryRepository.getReferenceById(catDto.getId());
            entity.getCategories().add(category);
        }
    }
}

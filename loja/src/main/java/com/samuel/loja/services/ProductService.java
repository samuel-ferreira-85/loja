package com.samuel.loja.services;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.entities.Product;
import com.samuel.loja.repository.IProductRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {
    
    @Autowired
    private IProductRepository repository;

    @Transactional(readOnly = true)
    public Page<ProductDto> findAllPaged(PageRequest pageRequest) {
        Page<Product> list = repository.findAll(pageRequest);

        return list.map(c -> new ProductDto(c, c.getCategories()));
    }

    @Transactional(readOnly = true)
    public ProductDto findById(Long id) {
        Product product = getProduct(id);

        return new ProductDto(product, product.getCategories());
    }

    @Transactional
    public ProductDto insert(ProductDto productDto) {
        var product = Product.builder()
            .name(productDto.getName())
            // .createdAt(Instant.now())
            .build();

        Product productSaved = repository.save(product);

        return new ProductDto(productSaved);
    }

    @Transactional
    public ProductDto update(Long id, ProductDto productDto) {
        Product product = getProduct(id);

        BeanUtils.copyProperties(productDto, product, "id");

        return new ProductDto(product);
    }

    public Product getProduct(Long id) {
        return repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Não há recurso para o id: " + id));
    }

    public void delete(Long id) {
        try {
            if (repository.existsById(id)) {
                repository.deleteById(id);
            } else {
                throw new ResourceNotFoundException("Resource not found.");
            }
        } catch (DataIntegrityViolationException e) {
            throw new DataBaseException("Integrity violation.");
        }
    }
}

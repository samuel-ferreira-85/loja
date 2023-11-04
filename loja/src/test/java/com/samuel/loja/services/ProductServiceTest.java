package com.samuel.loja.services;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.entities.Product;
import com.samuel.loja.repository.IProductRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import com.samuel.loja.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private IProductRepository productRepository;

    private long existingId;
    private long nonExistingID;
    private long dependentID;
    private PageImpl<Product> page;
    private Product product;
    private ProductDto productDto;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingID = 2L;
        dependentID = 3L;

        product = Factory.createProduct();
        productDto = Factory.createProductDto();

        page = new PageImpl<>(List.of(product));

        when(productRepository.findAll((Pageable) ArgumentMatchers.any())).thenReturn(page);

        when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        when(productRepository.findById(nonExistingID)).thenReturn(Optional.empty());

        doNothing().when(productRepository).deleteById(existingId);
        doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingID);
        doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentID);
    }

    @Test
    void findByIdShouldReturnProductDtoWhenIdExists() {
        ProductDto result = productService.findById(existingId);
        assertNotNull(result);
        verify(productRepository).findById(existingId);
    }

    @Test
    void findByIdShouldThrowResourceNotFoundExceptionWhenNonExistingID() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingID);
        });
        verify(productRepository).findById(nonExistingID);
    }

    @Test
    void updateShouldReturnProductDtoWhenIdExists() {
        ProductDto result = productService.update(existingId, productDto);
        assertNotNull(result);
        verify(productRepository).findById(existingId);
    }

    @Test
    void updateShouldReturnResourceNotFoundExceptionWhenNonExistingID() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingID, productDto);
        });
        verify(productRepository).findById(nonExistingID);
    }

    @Test
    void findAllPagedShouldReturnPage() {
        PageRequest pageable = PageRequest.of(0, 10);
        Page<ProductDto> result = productService.findAllPaged(pageable);

        assertNotNull(result);
        verify(productRepository).findAll(pageable);

    }

    @Test
    void deleteShouldThrowDataBaseExceptionWhenDependentId() {
        assertThrows(DataBaseException.class, () -> {
            productService.delete(dependentID);
        });

        verify(productRepository, times(1))
                .deleteById(dependentID);
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenNonExistingID() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingID);
        });

        verify(productRepository, times(1))
                .deleteById(nonExistingID);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {
        assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });

        verify(productRepository, times(1))
                .deleteById(existingId);
    }

}
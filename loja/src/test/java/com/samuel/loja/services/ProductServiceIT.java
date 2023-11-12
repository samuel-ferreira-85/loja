package com.samuel.loja.services;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.repository.ProductRepository;
import static org.junit.jupiter.api.Assertions.*;

import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void findAllPagedShouldReturnPageWhenPage0Size10() {
        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        assertFalse(page.isEmpty());
        assertEquals(0, page.getNumber());
        assertEquals(10, page.getSize());
        assertEquals(countTotalProducts, page.getTotalElements());
    }

    @Test
    void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExist() {
        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        assertTrue(page.isEmpty());
    }

    @Test
    void findAllPagedShouldReturnSortedPageWhenSortByName() {
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDto> page = productService.findAllPaged(pageRequest);

        assertFalse(page.isEmpty());
        assertEquals("Macbook Pro", page.getContent().get(0).getName());
        assertEquals("PC Gamer", page.getContent().get(1).getName());
        assertEquals("PC Gamer Alfa", page.getContent().get(2).getName());
    }

    @Test
    void deleteShouldDeleteResourceWhenIdExists() {
        productService.delete(existingId);

        assertEquals(countTotalProducts -1, productRepository.count());
    }

    @Test
    void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }
}

package com.samuel.loja.services;

import com.samuel.loja.repository.IProductRepository;
import com.samuel.loja.services.exceptions.DataBaseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private IProductRepository productRepository;

    private long existingId;
    private long nonExistingID;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingID = 100;
        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingID);
    }

    @Test
    void deleteShouldDoNothingWhenNonExistingID() {

        assertDoesNotThrow(() -> {
            productService.delete(nonExistingID);
        });
        Mockito.verify(productRepository, Mockito.times(1))
                .deleteById(existingId);
    }

    @Test
    void deleteShouldDoNothingWhenIdExists() {

        assertThrows(DataBaseException.class, () -> {
            productService.delete(nonExistingID);
        });
        Mockito.verify(productRepository, Mockito.times(1))
                .deleteById(nonExistingID);
    }

}
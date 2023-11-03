package com.samuel.loja.repository;

import com.samuel.loja.entities.Product;
import com.samuel.loja.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class IProductRepositoryTest {

    long existingId;
    long nonExistingId;
    long countTotalProducts;

    @Autowired
    private IProductRepository productRepository;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
    }

    @Test
    void saveShouldPersistWithAutoincrementWhenIdIsNull() {
        Product product = Factory.createProduct();
        product.setId(null);

        product = productRepository.save(product);

        assertNotNull(product.getId());
        assertEquals(countTotalProducts + 1, product.getId());
    }

    @Test
    void findByIdShouldRetunNonEmptyWhenIdExists() {
        Optional<Product> productOptional = productRepository.findById(existingId);
        assertTrue(productOptional.isPresent());
    }

    @Test
    void findByIdShouldRetunEmptyWhenIdDoesNotExist() {
        Optional<Product> productOptional = productRepository.findById(nonExistingId);
        //assertFalse(productOptional.isPresent());
         assertTrue(productOptional.isEmpty());
    }

    @Test
    void deleteShouldDeleteObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);

        assertFalse(result.isPresent());
    }

//    deleteById(id) - com um Id que não existe não lança exceção nas atualizações
//    @Test
//    void deleteShouldThrowEmptyResultDataAccessExceptionWhenIdDoesNotExist() {
//        assertThrows(IllegalArgumentException.class, () -> {
//            productRepository.deleteById(nonExistingId);
//        });
//    }


}
package com.samuel.loja.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.dto.ProductListDto;
import com.samuel.loja.services.ProductService;
import com.samuel.loja.services.exceptions.DataBaseException;
import com.samuel.loja.services.exceptions.ResourceNotFoundException;
import com.samuel.loja.tests.Factory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.TestExecutionEvent;
import org.springframework.security.test.context.support.WithMockUser;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(ProductController.class)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext context;

    @MockBean
    private ProductService productService;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingID;
    private Long dependentID;
    private ProductDto productDto;
    private ProductListDto productListDto;
    private PageImpl<ProductListDto> page;

    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingID = 2L;
        dependentID = 3L;

        adminUsername = "maria@gmail.com";
        adminPassword = "123456";

        mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        productDto = Factory.createProductDto();
        System.out.println(productDto);
        productListDto = Factory.createProductListDto();
        page = new PageImpl<>(List.of(productListDto));

        when(productService.findAllPaged(any())).thenReturn(page);

        when(productService.findById(existingId)).thenReturn(productDto);
        when(productService.findById(nonExistingID))
                .thenThrow(ResourceNotFoundException.class);

        when(productService.insert(any())).thenReturn(productDto);

        when(productService.update(eq(existingId), any())).thenReturn(productDto);
        when(productService.update(eq(nonExistingID), any()))
                .thenThrow(ResourceNotFoundException.class);

        doNothing().when(productService).delete(existingId);
        doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingID);
        doThrow(DataBaseException.class).when(productService).delete(dependentID);

    }

    @Test
    void mustNotAllowAccessToProductsWithoutAuthorization() throws Exception {
        mockMvc.perform(get("/products"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(get("/products")
                        .with(user(adminUsername).password(adminPassword))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findByIdShouldReturnProductWhenIdExists() throws Exception {
        mockMvc.perform(get("/products/{id}", existingId)
                        .with(user(adminUsername).password(adminPassword))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void findByIdShouldReturnNotFoundWhenIdDoesNoTExist() throws Exception {
        mockMvc.perform(get("/products/{id}", nonExistingID)
                        .with(user(adminUsername).password(adminPassword))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturnProductDtoCreated() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        System.out.println(jsonBody);

        mockMvc.perform(post("/products")
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com").roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }


    @Test
    void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com").roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").exists());
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(put("/products/{id}", nonExistingID)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com").roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteShouldReturnNoContentWhenIdExists() throws Exception {
        mockMvc.perform(delete("/products/{id}", existingId)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com").roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        mockMvc.perform(delete("/products/{id}", nonExistingID)
                        .with(SecurityMockMvcRequestPostProcessors.user("maria@gmail.com").roles("OPERATOR", "ADMIN"))
                        .with(SecurityMockMvcRequestPostProcessors.csrf())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}
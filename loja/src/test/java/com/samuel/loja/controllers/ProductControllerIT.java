package com.samuel.loja.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.samuel.loja.dto.CategoryDto;
import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.entities.Category;
import com.samuel.loja.tests.Factory;
import com.samuel.loja.tests.TokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countTotalProducts;
    private ProductDto productDto;

    private String operatorUsername;
    private String operatorPassword;
    private String adminUsername;
    private String adminPassword;

    @BeforeEach
    void setUp() {
        existingId = 1L;
        nonExistingId = 1000L;
        countTotalProducts = 25L;
        productDto = Factory.createProductDto();

        operatorUsername = "alex@gmail.com";
        operatorPassword = "123456";
        adminUsername = "maria@gmail.com";
        adminPassword = "123456";
    }

    @Test
    void insertShouldReturn403WhenOperatorLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(post("/products")
                .header("Authorization", "Bearer " + getTokenOperator())
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void updateShouldReturn403WhenOperatorLoggedAndIdExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(put("/products/"+existingId)
                        .header("Authorization", "Bearer " + getTokenOperator())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    @Test
    void insertShouldReturn401WhenNoUserLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(post("/products")
                .content(jsonBody)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void updateShouldReturn401WhenNoUserLogged() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(put("/products/{id}", existingId)
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void insertShouldInsertResourceWhenAdminLoggedAndCorrectData() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("PS5 Ultimate"))
                .andExpect(jsonPath("$.description").value("The new generation PS5 video game"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(4987.90)))
                .andExpect(jsonPath("$.imgUrl").value("http://img.com.br/ps5.jpg"))
                .andExpect(jsonPath("$.date").value("2023-12-20T10:00:00Z"))
                .andExpect(jsonPath("$.categories[0].id").value(2L))
                .andExpect(jsonPath("$.categories[0].name").value("Eletronicos"));
    }

    @Test
    void updateShouldUpdateResourceWhenAdminLoggedAndCorrectDataAndIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value("PS5 Ultimate"))
                .andExpect(jsonPath("$.description").value("The new generation PS5 video game"))
                .andExpect(jsonPath("$.price").value(BigDecimal.valueOf(4987.90)))
                .andExpect(jsonPath("$.imgUrl").value("http://img.com.br/ps5.jpg"))
                .andExpect(jsonPath("$.date").value("2023-12-20T10:00:00Z"))
                .andExpect(jsonPath("$.categories[0].id").value(2L))
                .andExpect(jsonPath("$.categories[0].name").value("Eletronicos"));
    }

    @Test
    void updateShouldReturn404WhenAdminLoggedAndCorrectDataAndIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);
        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void insertShouldReturn400WhenAdminLoggedAndBlankName() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("  ")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Required field"));

    }

    @Test
    void updateShouldReturn400WhenAdminLoggedAndBlankName() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("  ")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("name"))
                .andExpect(jsonPath("$.errors[0].message").value("Required field"));
    }

    @Test
    void insertShouldReturn400WhenAdminLoggedAndDateInThePast() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-10-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("date"))
                .andExpect(jsonPath("$.errors[0].message").value("the date cannot be in the past"));
    }

    @Test
    void updateShouldReturn400WhenAdminLoggedAndDateInThePast() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-10-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("date"))
                .andExpect(jsonPath("$.errors[0].message").value("the date cannot be in the past"));
    }

    @Test
    void insertShouldReturn400WhenAdminLoggedAndNegativePrice() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.ZERO)
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
                .andExpect(jsonPath("$.errors[0].message").value("the price field must be positive"));

    }

    @Test
    void updateShouldReturn400WhenAdminLoggedAndNegativePrice() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.ZERO)
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();
        dto.getCategories().clear();
        dto.getCategories().add(new CategoryDto(2L, "Eletronicos"));

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("price"))
                .andExpect(jsonPath("$.errors[0].message").value("the price field must be positive"));

    }

    @Test
    void insertShouldReturn400WhenAdminLoggedAndNullCategories() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("categories"))
                .andExpect(jsonPath("$.errors[0].message").value("Must contain at least one category"));
    }

    @Test
    void updateShouldReturn400WhenAdminLoggedAndNullCategories() throws Exception {
        var dto = ProductDto.builder()
                .id(null)
                .name("PS5 Ultimate")
                .description("The new generation PS5 video game")
                .price(BigDecimal.valueOf(4987.90))
                .imgUrl("http://img.com.br/ps5.jpg")
                .date(Instant.parse("2023-12-20T10:00:00Z"))
                .categories(new ArrayList<>())
                .build();

        String jsonBody = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].fieldName").value("categories"))
                .andExpect(jsonPath("$.errors[0].message").value("Must contain at least one category"));
    }

    @Test
    void findAllShouldReturnSortedPageWhenSortByName() throws Exception {
        ResultActions result = mockMvc.perform(
                get("/products?page=0&size=12&sort=name,asc")
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .contentType(MediaType.APPLICATION_JSON));
//                        .accept(MediaType.APPLICATION_JSON));
        result.andExpect(status().isOk());
        result.andExpect(jsonPath("$.totalElements").value(countTotalProducts));
        result.andExpect(jsonPath("$.content").exists());
        result.andExpect(jsonPath("$.content[0].name").value("Macbook Pro"));
        result.andExpect(jsonPath("$.content[1].name").value("PC Gamer"));
        result.andExpect(jsonPath("$.content[2].name").value("PC Gamer Alfa"));
    }

    @Test
    void updateShouldReturnProductDtoWhenIdExists() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        String expectedName = productDto.getName();
        String expectedDescription = productDto.getDescription();

        mockMvc.perform(put("/products/{id}", existingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(existingId))
                .andExpect(jsonPath("$.name").value(expectedName))
                .andExpect(jsonPath("$.description").value(expectedDescription));
    }

    @Test
    void updateShouldReturnNotFoundWhenIdDoesNotExist() throws Exception {
        String jsonBody = objectMapper.writeValueAsString(productDto);

        mockMvc.perform(put("/products/{id}", nonExistingId)
                        .header("Authorization", "Bearer " + getTokenAdmin())
                        .content(jsonBody)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    private String getTokenAdmin() throws Exception {
        return tokenUtil.obtainAccessToken(mockMvc, adminUsername, adminPassword);
    }
    private String getTokenOperator() throws Exception {
        return tokenUtil.obtainAccessToken(mockMvc, operatorUsername, operatorPassword);
    }
}

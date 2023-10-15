package com.samuel.loja.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.samuel.loja.entities.Category;

@RestController
@RequestMapping("/categories")
public class CategoryController {
    
    @GetMapping
    public ResponseEntity<List<Category>> findAll() {
        List<Category> list = new ArrayList<>();
        list.add(new Category(UUID.randomUUID(), "books"));
        list.add(new Category(UUID.randomUUID(), "eletronics"));

        return ResponseEntity.ok().body(list);
    }
}

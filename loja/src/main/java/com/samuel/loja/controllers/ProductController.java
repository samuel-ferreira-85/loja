package com.samuel.loja.controllers;

import com.samuel.loja.dto.ProductDto;
import com.samuel.loja.dto.ProductListDto;
import com.samuel.loja.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    
    @GetMapping
    public ResponseEntity<Page<ProductListDto>> findAll(
        @RequestParam(value = "page", defaultValue = "0") Integer page,
        @RequestParam(value = "linesPerPage", defaultValue = "12") Integer linesPerPage,
        @RequestParam(value = "direction", defaultValue = "ASC") String direction,
        @RequestParam(value = "orderBy", defaultValue = "name") String orderBy) {

        PageRequest pageRequest = PageRequest.of(page, linesPerPage, Direction.valueOf(direction), orderBy);
        Page<ProductListDto> list = productService.findAllPaged(pageRequest);
        return ResponseEntity.ok().body(list);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_OPERATOR')")
    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> findById(@PathVariable Long id) {
        ProductDto product = productService.findById(id);
        return ResponseEntity.ok().body(product);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<ProductDto> insert(@Valid @RequestBody ProductDto productDto) {
        var dto = productService.insert(productDto);

        URI uri = ServletUriComponentsBuilder
            .fromCurrentContextPath().path("/{id}")
            .buildAndExpand(dto.getId())
            .toUri();

        return ResponseEntity.created(uri).body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> update(@PathVariable Long id,
                                             @Valid @RequestBody ProductDto productDto) {
        ProductDto dto = productService.update(id, productDto);
        return ResponseEntity.ok().body(dto);
    }

    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

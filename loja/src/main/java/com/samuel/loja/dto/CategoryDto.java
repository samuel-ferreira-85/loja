package com.samuel.loja.dto;

import com.samuel.loja.entities.Category;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@Builder
@ToString
public class CategoryDto {
       
    private Long id;
    private String name;

    public CategoryDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }

}

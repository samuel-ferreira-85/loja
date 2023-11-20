package com.samuel.loja.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.samuel.loja.entities.Category;
import com.samuel.loja.entities.Product;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDto {
    private Long id;
	@NotBlank
	@Size(min = 5, max = 60, message = "the name field must be between 5 and 60 characters")
	private String name;
	@NotBlank
	private String description;
	@Positive
	private BigDecimal price;
	@NotBlank
	private String imgUrl;
	@FutureOrPresent
	private Instant date;
	@NotEmpty(message = "Must contain at least one category")
	private List<CategoryDto> categories = new ArrayList<>();

    public ProductDto(Product entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.description = entity.getDescription();
		this.price = entity.getPrice();
		this.imgUrl = entity.getImgUrl();
		this.date = entity.getDate();
	}
	
	public ProductDto(Product entity, Set<Category> categories) {
		this(entity);
		categories.forEach(c -> this.categories.add(new CategoryDto(c)));
	}
}

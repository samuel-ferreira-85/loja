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
	@NotBlank(message = "Required field")
	private String name;
	@NotBlank(message = "Required field")
	private String description;
	@Positive(message = "the price field must be positive")
	private BigDecimal price;
	@NotBlank(message = "Required field")
	private String imgUrl;
	@FutureOrPresent( message = "the date cannot be in the past")
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

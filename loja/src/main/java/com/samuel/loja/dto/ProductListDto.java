package com.samuel.loja.dto;

import com.samuel.loja.entities.Product;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductListDto {
    private Long id;
	private String name;
	private BigDecimal price;
	private String imgUrl;

    public ProductListDto(Product entity) {
		this.id = entity.getId();
		this.name = entity.getName();
		this.price = entity.getPrice();
		this.imgUrl = entity.getImgUrl();
	}
}

package com.samuel.loja.entities;

import java.util.UUID;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Entity(name = "tb_category")
public class Category {
    
    @EqualsAndHashCode.Include
    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    private String name;    
}

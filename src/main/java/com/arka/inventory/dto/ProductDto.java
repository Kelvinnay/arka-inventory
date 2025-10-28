package com.arka.inventory.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class ProductDto {
    Long id;
    String sku;
    String name;
    String description;
    Double price;
    Integer stock;
}

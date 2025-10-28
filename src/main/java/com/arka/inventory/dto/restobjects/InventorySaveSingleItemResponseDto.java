package com.arka.inventory.dto.restobjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventorySaveSingleItemResponseDto {
    String message;
    private String codResponse;
    InventorySaveSingleItemResponseDetailsDto body;
}

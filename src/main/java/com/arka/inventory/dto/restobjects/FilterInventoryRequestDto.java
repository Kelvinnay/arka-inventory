package com.arka.inventory.dto.restobjects;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class FilterInventoryRequestDto {
    private String status;
    private LocalDate starDate;
    private LocalDate finishDate;
    private Long branchId;
}

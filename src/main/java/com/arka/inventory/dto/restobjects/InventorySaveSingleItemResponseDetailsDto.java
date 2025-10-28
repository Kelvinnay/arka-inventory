package com.arka.inventory.dto.restobjects;

import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventorySaveSingleItemResponseDetailsDto {
    private Long productId;
    private LocalDate dateReceived;
    private LocalDate manufactureDate;
    private LocalDate warrantyEndDate;
    private String status;
    private BigDecimal acquisitionCost;
    private Integer quantity;
    private Long branchId;


    public static InventorySaveSingleItemResponseDetailsDto fromEntity(Inventory inventory) {
        InventorySaveSingleItemResponseDetailsDto dto = new InventorySaveSingleItemResponseDetailsDto();
        dto.setProductId(inventory.getProductId());
        dto.setDateReceived(inventory.getDateReceived());
        dto.setManufactureDate(inventory.getManufactureDate());
        dto.setWarrantyEndDate(inventory.getWarrantyEndDate());
        dto.setStatus(inventory.getStatus());
        dto.setAcquisitionCost(inventory.getAcquisitionCost());
        dto.setQuantity(inventory.getQuantity());
        dto.setBranchId(inventory.getBranchId());
        return dto;
    }
}

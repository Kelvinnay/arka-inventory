package com.arka.inventory.dto.restobjects;

import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventorySaveSingleItemRequestDto {
    private Long productId;
    private LocalDate dateReceived;
    private LocalDate manufactureDate;
    private LocalDate warrantyEndDate;
    private String status;
    private BigDecimal acquisitionCost;
    private Integer quantity;
    private Long branchId;

    // You will need this method in your DTO class:
    public Inventory toEntity() {
        Inventory inventory = new Inventory();
        inventory.setProductId(this.productId);
        inventory.setDateReceived(this.dateReceived);
        inventory.setManufactureDate(this.manufactureDate);
        inventory.setWarrantyEndDate(this.warrantyEndDate);
        inventory.setStatus(this.status);
        inventory.setAcquisitionCost(this.acquisitionCost);
        inventory.setQuantity(this.quantity);
        inventory.setBranchId(this.branchId);
        return inventory;
    }

}

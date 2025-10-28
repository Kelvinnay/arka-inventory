package com.arka.inventory.dto;

import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class InventoryDtoResp {
    private Long id;
    private Long productId;
    private String productName;
//    private String lotNumber;
//    private String manufacturerLotNumber;
    private LocalDate dateReceived;
    private LocalDate manufactureDate;
    private LocalDate warrantyEndDate;
    private String status;
    private String warehouseLocation;
    private BigDecimal acquisitionCost;
    private Integer quantity;
    private Long branchId;
    private String branchName;
    public static InventoryDtoResp  fromEntity(Inventory inventory) {
        InventoryDtoResp dto = new InventoryDtoResp();
        dto.setId(inventory.getId());
        dto.setProductId(inventory.getProductId());
//        dto.setSerialNumber(inventory.getSerialNumber());
//        dto.setLotNumber(inventory.getLotNumber());
//        dto.setManufacturerLotNumber(inventory.getManufacturerLotNumber());
        dto.setDateReceived(inventory.getDateReceived());
        dto.setManufactureDate(inventory.getManufactureDate());
        dto.setWarrantyEndDate(inventory.getWarrantyEndDate());
        dto.setStatus(inventory.getStatus());
        dto.setWarehouseLocation(inventory.getWarehouseLocation());
        dto.setAcquisitionCost(inventory.getAcquisitionCost());
        dto.setQuantity(inventory.getQuantity());
        dto.setBranchId(inventory.getBranchId());
        return dto;
    }
}

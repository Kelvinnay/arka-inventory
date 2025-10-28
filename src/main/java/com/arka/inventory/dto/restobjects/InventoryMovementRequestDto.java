package com.arka.inventory.dto.restobjects;

import com.arka.inventory.entity.InventoryTransaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InventoryMovementRequestDto {
    private Long inventoryUnitId;
    private String movementType;
    private LocalDate movementDate;
    private String documentReference;
    private Long idOperator;
    private String nameOperator;
    private String quantityType;
    private Integer quantity;
    private Long fromBranch;
    private Long toBranch;
    public InventoryTransaction toEntity() {
        InventoryTransaction entity = new InventoryTransaction();

        // --- Mapping fields directly ---
        entity.setInventoryUnitId(this.inventoryUnitId);
        entity.setMovementDate(this.movementDate);
        entity.setMovementType(this.getMovementType());
        entity.setDocumentReference(this.documentReference);
        entity.setIdOperator(this.idOperator);
        entity.setNameOperator(this.nameOperator);
        entity.setQuantity(this.quantity);
        entity.setFromBranch(this.fromBranch);
        entity.setToBranch(this.toBranch);
        entity.setQuantityType(this.quantityType);

        return entity;
    }
    // Note: In a real scenario, you would also add validation annotations here (e.g., @NotNull, @Min)
}
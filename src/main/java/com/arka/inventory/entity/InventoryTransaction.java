package com.arka.inventory.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "inventory_transaction", schema = "inventory")
@Getter
@Setter
public class InventoryTransaction {

    @Id
    private Long id;

    @Column("inventory_unit_id")
    private Long inventoryUnitId;

    @Column("movement_type")
    private String movementType;

    @Column("movement_date")
    private LocalDate movementDate;

    @Column("document_reference")
    private String documentReference;

    @Column("id_operator")
    private Long idOperator;

    @Column("name_operator")
    private String nameOperator;

    @Column("quantity_type")
    private String quantityType;

    @Column("quantity")
    private Integer quantity;

    @Column("from_branch")
    private Long fromBranch;
//
    @Column("to_branch")
    private Long toBranch;
}

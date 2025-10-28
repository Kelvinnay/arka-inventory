package com.arka.inventory.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
/// /////////////////////////// ADD FOREIGN KEY TO SUPLIERS  AND BRANCHID TO SQL DDL
@Table(name = "inventory", schema = "inventory")
@Getter
@Setter
public class Inventory {

    @Id
    private Long id;

    @Column("product_id")
    private Long productId;

//    @Column("serial_number")
//    private String serialNumber;
//
//    @Column("lot_number")
//    private String lotNumber;
//
//    @Column("manufacturer_lot_number")
//    private String manufacturerLotNumber;

    @Column("date_received")
    private LocalDate dateReceived;

    @Column("manufacture_date")
    private LocalDate manufactureDate;

    @Column("warranty_end_date")
    private LocalDate warrantyEndDate;

    @Column("status")
    private String status;

    @Column("warehouse_location")
    private String warehouseLocation;

    @Column("acquisition_cost")
    private BigDecimal acquisitionCost;

    @Column("quantity")
    private Integer quantity;

    @Column("branch_id")
    private Long branchId;

}

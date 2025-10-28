package com.arka.inventory.entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDate;

@Table(name = "branch", schema = "inventory")
@Getter
@Setter
public class Branch {

    @Id
    private Long id;

    @Column("branch_name")
    private String branchName;

    @Column("address")
    private String address;

    @Column("city")
    private String city;

    @Column("province_state")
    private String provinceState;

    @Column("country")
    private String country;

    @Column("phone")
    private String phone;

    @Column("email")
    private String email;

//    @Column("manager_id")
//    private int managerId;

    @Column("manager_name")
    private String managerName;

    @Column("is_active")
    private boolean isActive;

    @Column("date_opened")
    private LocalDate dateOpened;

    @Column("warehouse_type")
    private String warehouseType;


}
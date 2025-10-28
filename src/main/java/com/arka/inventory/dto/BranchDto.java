package com.arka.inventory.dto;

import com.arka.inventory.entity.Branch;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class BranchDto {
    Long id;
    String branchName;
    String address;
    String city;
    String provinceState;
    String country;
    String phone;
    String email;
//    int manager_id;
    String managerName;
    boolean isActive;
    LocalDate dateOpened;
    String warehouseType;


    public static BranchDto fromEntity(Branch branch) {
        if (branch == null) {
            return null;
        }
        BranchDto dto = new BranchDto();
        dto.setId(branch.getId());
        dto.setBranchName(branch.getBranchName());
        dto.setAddress(branch.getAddress());
        dto.setCity(branch.getCity());
        dto.setProvinceState(branch.getProvinceState());
        dto.setCountry(branch.getCountry());
        dto.setPhone(branch.getPhone());
        dto.setEmail(branch.getEmail());
        dto.setManagerName(branch.getManagerName());
        dto.setActive(branch.isActive());
        dto.setDateOpened(branch.getDateOpened());
        dto.setWarehouseType(branch.getWarehouseType());
        return dto;
    }
}

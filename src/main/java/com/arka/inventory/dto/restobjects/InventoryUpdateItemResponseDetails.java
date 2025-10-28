package com.arka.inventory.dto.restobjects;

import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

@Getter
@Setter
public class InventoryUpdateItemResponseDetails {
    private List<Inventory> updatedInventories;
    private List<Long> notFoundStockForProduct;
    private Map<Long, String> errorProductIds;
}

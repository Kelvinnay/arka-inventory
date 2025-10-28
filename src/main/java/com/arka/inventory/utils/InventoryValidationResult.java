package com.arka.inventory.utils;

import com.arka.inventory.entity.Inventory;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.Map;

// Used to pass data and errors between steps
@Getter
@Setter
public class InventoryValidationResult {
    List<Inventory> validatedInventories;
    List<Long> notFoundProductIds;
    Map<Long, String> validationErrors;

    // Constructor to initialize lists
    public InventoryValidationResult(List<Inventory> validatedInventories, List<Long> notFoundProductIds, Map<Long, String> validationErrors) {
        this.validatedInventories = validatedInventories;
        this.notFoundProductIds = notFoundProductIds;
        this.validationErrors = validationErrors;
    }

    // Getters (omitted for brevity)
}

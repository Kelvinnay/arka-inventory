package com.arka.inventory.dto.enums;

public enum WarehouseType {

    CENTRAL_DISTRIBUTION_CENTER("Central Distribution Center"),
    RETAIL_STORE("Retail Store"),
    RETURNS_CENTER("Returns Center");

    private final String label;

    WarehouseType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

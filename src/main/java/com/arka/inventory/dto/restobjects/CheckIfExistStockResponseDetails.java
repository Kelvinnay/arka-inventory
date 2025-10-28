package com.arka.inventory.dto.restobjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckIfExistStockResponseDetails {
    private Long inventoryId;
    private Long productId;
    private Integer quantity;
    private Boolean isStockAvailable;
}

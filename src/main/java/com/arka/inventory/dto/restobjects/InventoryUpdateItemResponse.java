package com.arka.inventory.dto.restobjects;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateItemResponse {
    String messsage;
    String codResponse;
    InventoryUpdateItemResponseDetails body;
}

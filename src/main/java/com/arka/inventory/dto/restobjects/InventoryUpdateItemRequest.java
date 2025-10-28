package com.arka.inventory.dto.restobjects;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryUpdateItemRequest {
    private Long productId;
    private Integer type;
    private Integer quantity;
//    private String documentId;

}
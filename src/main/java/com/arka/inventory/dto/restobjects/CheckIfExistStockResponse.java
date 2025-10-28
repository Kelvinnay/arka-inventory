package com.arka.inventory.dto.restobjects;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CheckIfExistStockResponse {// This holds the list of items found (using the DTO format you provided)
    private String message;
    private String codResponse;
    private List<CheckIfExistStockResponseDetails> foundInventories;
    private List<CheckIfExistStockResponseDetails> notFoundProductStock;
}

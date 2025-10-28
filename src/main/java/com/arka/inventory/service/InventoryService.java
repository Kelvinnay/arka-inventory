package com.arka.inventory.service;

import com.arka.inventory.dto.InventoryDto;
import com.arka.inventory.dto.InventoryDtoResp;
import com.arka.inventory.dto.restobjects.CheckIfExistStockResponse;
import com.arka.inventory.dto.restobjects.FilterInventoryRequestDto;
import com.arka.inventory.dto.restobjects.InventoryUpdateItemRequest;
import com.arka.inventory.dto.restobjects.InventoryUpdateItemResponse;
import com.arka.inventory.entity.Inventory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface InventoryService {
    Mono<Inventory> save(Inventory inventory);
    Flux<Inventory> saveAllInventory(Flux<Inventory> inventoryFlux);
    Mono<Inventory> findById(Long id);
    Mono<CheckIfExistStockResponse> checkIfExistStockOfListProduct(List<Long> productIds, Integer quantity);
    Mono<InventoryDtoResp> findByIdCustom(Long id);
    Flux<Inventory> findAll();
    Flux<Inventory> findInventoryByDateReceivedBetween(LocalDate startDate, LocalDate endDate);
    Flux<InventoryDto> findByFilters(FilterInventoryRequestDto filters);
    Mono<InventoryUpdateItemResponse> updateQuantities(List<InventoryUpdateItemRequest> updateItems);
}

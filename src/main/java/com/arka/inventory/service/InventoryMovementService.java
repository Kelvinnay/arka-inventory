package com.arka.inventory.service;

import com.arka.inventory.dto.restobjects.InventoryMovementRequestDto;
import com.arka.inventory.dto.restobjects.InventoryUpdateItemRequest;
import com.arka.inventory.entity.Inventory;
import com.arka.inventory.entity.InventoryTransaction;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface InventoryMovementService {
    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<InventoryTransaction> insert(InventoryMovementRequestDto transaction);

    /**
     * Finds all inventory transactions that occurred within the specified date range.
     * @param startDate The start date (inclusive).
     * @param endDate The end date (inclusive).
     * @return A Flux emitting the matching transactions.
     */
    Flux<InventoryTransaction> findByMovementDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<Boolean> saveList(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests, List<Inventory> transaction);
    /**
     * Inserts a new inventory transaction.
     * @param transaction The transaction to be saved.
     * @return A Mono emitting the saved transaction.
     */
    Mono<Boolean> senListToAWSQueue(List<InventoryUpdateItemRequest> inventoryUpdateItemRequests, List<Inventory> transaction);
}

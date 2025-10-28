package com.arka.inventory.repository;
import com.arka.inventory.entity.Inventory;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

public interface InventoryRepository extends ReactiveCrudRepository<Inventory, Long> {
    Mono<Inventory> findByProductId(Long productId);

    // New method for date range query
    Flux<Inventory> findByDateReceivedBetween(LocalDate startDate, LocalDate endDate);

    // New method to query by date range AND status
    Flux<Inventory> findByDateReceivedBetweenAndBranchId(LocalDate startDate, LocalDate endDate,  Long branchId);

    // New method to query by date range, status, AND branchId
    Flux<Inventory> findByDateReceivedBetweenAndStatusAndBranchId(LocalDate startDate, LocalDate endDate, String status, Long branchId);

    // New method to find multiple inventories by a list of productIds
    Flux<Inventory> findByProductIdIn(List<Long> productIds);

    Flux<Inventory> findByProductIdInAndQuantityGreaterThan(List<Long> productIds, Integer quantity);
}
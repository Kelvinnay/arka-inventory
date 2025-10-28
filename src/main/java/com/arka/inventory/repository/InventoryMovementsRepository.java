package com.arka.inventory.repository;

import com.arka.inventory.entity.InventoryTransaction;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface InventoryMovementsRepository extends R2dbcRepository<InventoryTransaction, Long> {
    Flux<InventoryTransaction> findByMovementDateBetween(LocalDate startDate, LocalDate endDate);
}

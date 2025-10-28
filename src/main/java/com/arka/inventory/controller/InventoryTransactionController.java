package com.arka.inventory.controller;

import com.arka.inventory.dto.restobjects.InventoryMovementRequestDto;
import com.arka.inventory.entity.InventoryTransaction;
import com.arka.inventory.service.InventoryMovementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/v1/inventory-movements")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200") // Apply CORS only to this controller
public class InventoryTransactionController {
    private final InventoryMovementService service;

    /**
     * POST /api/v1/inventory-movements
     * Inserts a new inventory transaction.
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<InventoryTransaction> insertTransaction(@RequestBody InventoryMovementRequestDto transaction) {
        System.out.println("insertTransaction");
        return service.insert(transaction);
    }

    /**
     * GET /api/v1/inventory-movements/range
     * Finds transactions within a specified date range.
     * Example: GET /api/v1/inventory-movements/range?start=2023-10-01&end=2023-10-31
     */
    @GetMapping("/range")
    public Flux<InventoryTransaction> findByDateRange(
            @RequestParam("start") LocalDate startDate,
            @RequestParam("end") LocalDate endDate) {

        return service.findByMovementDateBetween(startDate, endDate);
    }
}

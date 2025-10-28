package com.arka.inventory.controller;
import com.arka.inventory.dto.InventoryDto;
import com.arka.inventory.dto.InventoryDtoResp;
import com.arka.inventory.dto.restobjects.*;
import com.arka.inventory.entity.Inventory;
import com.arka.inventory.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryServiceImpl;

    @GetMapping
    public Flux<Inventory> getAllInventory() {
        return inventoryServiceImpl.findAll();
    }

//    @GetMapping("/{id}")
//    public Mono<InventoryDto> getInventoryById(@PathVariable Long id) {
//        return inventoryService.findById(id).map(InventoryDto::fromEntity);
//    }
    @GetMapping("/product/{id}")
    public Mono<InventoryDtoResp> getInventoryByIdCustom(@PathVariable Long id) {
        return inventoryServiceImpl.findByIdCustom(id);
    }

    @PostMapping(value = "/batch", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Inventory> createInventoryBatch(@RequestBody Flux<Inventory> inventoryFlux) {
        return inventoryServiceImpl.saveAllInventory(inventoryFlux).delayElements(Duration.ofSeconds(2)).doOnNext(Inventory -> System.out.println("productDto onNext -> "+Inventory.getQuantity())).log();
    }
    @PostMapping
    public Mono<InventorySaveSingleItemResponseDto> createInventory(@RequestBody Mono<InventorySaveSingleItemRequestDto> inventoryMono) {
//        return inventoryMono.flatMap(inventoryServiceImpl::save);
        // 1. Use flatMap to unwrap the DTO from the Mono
        return inventoryMono
                .flatMap(requestDto -> {
                    // 2. Convert the DTO to the Inventory Entity
                    Inventory inventoryEntity = requestDto.toEntity();

                    // 3. Pass the Inventory Entity to the service layer for saving
                    //    The service layer should return a Mono<Inventory>
                    //    which is then mapped to the Response DTO.
                    return inventoryServiceImpl.save(inventoryEntity);
                })
                .map(response -> {
                    InventorySaveSingleItemResponseDto  monoRespnse = new InventorySaveSingleItemResponseDto();
                    monoRespnse.setMessage("Ingreso Exitoso");
                    monoRespnse.setCodResponse("0000");
                    monoRespnse.setBody(new InventorySaveSingleItemResponseDetailsDto().fromEntity(response));
                    return monoRespnse;
                });
    }

    @PutMapping("/{id}")
    public Mono<Inventory> updateInventory(@PathVariable Long id, @RequestBody Mono<Inventory> inventoryMono) {
        return inventoryServiceImpl.findById(id)
                .flatMap(existingInventory -> inventoryMono.map(
                        updatedInventory -> {
                            // Update fields from the new data
                            existingInventory.setProductId(updatedInventory.getProductId());
                            existingInventory.setQuantity(updatedInventory.getQuantity());
                            // ... other fields
                            return existingInventory;
                        }
                ))
                .flatMap(inventoryServiceImpl::save);
    }
    @GetMapping("/ejemplo")
    public Mono<String> holaMundo() {

        WebClient client = WebClient.create("https://jsonplaceholder.typicode.com");

        // Llamada REST que devuelve un Mono<String>
        Mono<String> response = client.get()
                .uri("/posts/1") // recurso dummy
                .retrieve()
                .bodyToMono(String.class);

        // Suscripción al Mono
        response.subscribe(
                valor -> System.out.println("onNext: " + valor),   // cuando llega la respuesta
                error -> System.err.println("onError: " + error), // si ocurre error
                () -> System.out.println("onComplete: flujo terminado") // cuando finaliza

        );

        // ⚠️ Como es un flujo reactivo asíncrono, damos un pequeño sleep
        try { Thread.sleep(3000); } catch (InterruptedException e) { }
        return Mono.just("null");
    }

    @GetMapping("/by-date-range")
    public Flux<Inventory> getInventoryByDateRange(@RequestParam LocalDate startDate, @RequestParam LocalDate endDate) {
        return inventoryServiceImpl.findInventoryByDateReceivedBetween(startDate, endDate);
    }

    @PostMapping("/search-by-filters")
    public Flux<InventoryDto> getByFilters(@RequestBody FilterInventoryRequestDto filters) {
        return inventoryServiceImpl.findByFilters(filters);
    }



    // New endpoint to handle different decrease values per product
    @PostMapping("/update-quantities")
    public Mono<InventoryUpdateItemResponse> updateInventoryQuantities(@RequestBody List<InventoryUpdateItemRequest> updateItems) {
        try {
            return inventoryServiceImpl.updateQuantities(updateItems);
        }catch (Exception ex){
            InventoryUpdateItemResponse inventoryUpdateItemResponse = new InventoryUpdateItemResponse();
            return Mono.just(inventoryUpdateItemResponse);
        }
    }

    @PostMapping("/check-if-Stock")
    public Mono<CheckIfExistStockResponse> getProductIdsFromRequest(@RequestBody List<CheckIfExistStockRequest> request) {

        // Use a stream to map the List of DTOs to a List of Longs
        List<Long> productIds = request.stream()
                .map(CheckIfExistStockRequest::getProductId)
                .collect(Collectors.toList());

        return inventoryServiceImpl.checkIfExistStockOfListProduct(productIds,0);
    }
}
package com.arka.inventory.service;
import com.arka.inventory.dto.InventoryDto;
import com.arka.inventory.dto.InventoryDtoResp;
import com.arka.inventory.dto.ProductDto;
import com.arka.inventory.dto.restobjects.*;
import com.arka.inventory.entity.Branch;
import com.arka.inventory.entity.Inventory;
import com.arka.inventory.repository.BranchRepository;
import com.arka.inventory.repository.InventoryRepository;
import com.arka.inventory.service.external.ExternalService;
import com.arka.inventory.utils.InventoryValidationResult;
import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService{

    private final WebClient webClient = WebClient.create("https://avm02saaxg.execute-api.us-east-2.amazonaws.com/v1/api/products");

    private final InventoryRepository inventoryRepository;
    private final BranchRepository branchRepository;
    private final ExternalService externalServices;
    private final InventoryMovementService inventoryMovementService;


    public Mono<Inventory> save(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    public Flux<Inventory> saveAllInventory(Flux<Inventory> inventoryFlux) {
        return inventoryRepository.saveAll(inventoryFlux);
    }
    public Mono<Inventory> findById(Long id) {
        return inventoryRepository.findById(id);
    }
    public Mono<CheckIfExistStockResponse> checkIfExistStockOfListProduct(List<Long> updateItems, Integer quantity) {
        // 1. Get a Flux of Inventory items that match the criteria
        Flux<Inventory> inventoryFlux = inventoryRepository.findByProductIdInAndQuantityGreaterThan(updateItems, quantity);

        return inventoryFlux
                // 2. Enforce distinctness by productId.
                .distinct(Inventory::getProductId)

                // 3. Map to the response DTO
                .map(foundInventory -> {
                    CheckIfExistStockResponseDetails inventoryDto = new CheckIfExistStockResponseDetails();
                    inventoryDto.setProductId(foundInventory.getProductId());
                    inventoryDto.setQuantity(foundInventory.getQuantity());
                    inventoryDto.setInventoryId(foundInventory.getId());
                    // Stock is available if it came from the query result
                    inventoryDto.setIsStockAvailable(true);
                    return inventoryDto;
                })

                // 4. Collect all found DTOs into a List<CheckIfExistStockResponseDetails>
                .collectList()

                // 5. Transform the List<CheckIfExistStockResponseDetails> into the final Mono<CheckIfExistStockResponse>
                .map(foundInventories -> {
                    // A. Create the set of all requested product IDs for efficient lookup
                    Set<Long> requestedProductIds = updateItems.stream().collect(Collectors.toSet());

                    // B. Create the set of product IDs that were actually found
                    Set<Long> foundProductIds = foundInventories.stream()
                            .map(CheckIfExistStockResponseDetails::getProductId)
                            .collect(Collectors.toSet());

                    // C. Calculate the 'not found' list by removing found IDs from the requested IDs set
                    requestedProductIds.removeAll(foundProductIds);
                    List<Long> notFoundProductIds = List.copyOf(requestedProductIds);
                    List<CheckIfExistStockResponseDetails> checkIfExistStockResponseDetails = new ArrayList<>();
                    for(Long productId:notFoundProductIds){

                        CheckIfExistStockResponseDetails inventoryDto = new CheckIfExistStockResponseDetails();
                        inventoryDto.setProductId(productId);
                        inventoryDto.setQuantity(0);
                        inventoryDto.setInventoryId(null);
                        // Stock is available if it came from the query result
                        inventoryDto.setIsStockAvailable(false);
                        checkIfExistStockResponseDetails.add(inventoryDto);
                    }

                    // D. Build the final response object
                    CheckIfExistStockResponse response = new CheckIfExistStockResponse();
                    if(checkIfExistStockResponseDetails.isEmpty()){
                        response.setCodResponse("000");
                        response.setMessage("Mensaje Exitoso");
                    }else{
                        response.setCodResponse("001");
                        response.setMessage("Productos sin stock en el Inventario");
                    }
                    response.setFoundInventories(foundInventories);
                    response.setNotFoundProductStock(checkIfExistStockResponseDetails);
                    return response;
                });
    }
    /**
     * Correctly fetches three pieces of data (Inventory, Product, Branch) in parallel
     * and combines them into a single DTO.
     * * @param id The ID of the Inventory item to fetch.
     * @return A Mono emitting the fully populated InventoryDtoResp.
     */
    public Mono<InventoryDtoResp> findByIdCustom(Long id) {
        // 1. Start by fetching the initial inventory item
        return inventoryRepository.findByProductId(id)
                .flatMap(inventory -> {
                    // 2. Define the two asynchronous operations that can run in parallel
                    // A. External call to the Product API
                    Mono<ProductDto> productMono = externalServices.fetchProductDetails(id);
                    // B. Internal call to the Branch repository
                    Mono<Branch> branchMono = branchRepository.findById(inventory.getBranchId())
                            // If branch is not found, return an empty Branch object (or null equivalent)
                            .defaultIfEmpty(new Branch());
                    // 3. Combine the two parallel Monos and the original inventory item
                    // Mono.zip combines up to 8 Monos into a single Mono<TupleN>
                    return Mono.zip(
                                    Mono.just(inventory), // T1: The original Inventory item
                                    productMono,         // T2: The result of the WebClient call
                                    branchMono           // T3: The result of the Branch repository call
                            )
                            // 4. Map the Tuple of results (T1, T2, T3) into the final DTO
                            .map(tuple -> {
                                Inventory inv = tuple.getT1();
                                ProductDto product = tuple.getT2();
                                Branch branch = tuple.getT3();

                                // Create and populate the final DTO
                                InventoryDtoResp inventoryDto = InventoryDtoResp.fromEntity(inv);
                                inventoryDto.setBranchName(branch.getBranchName());
                                inventoryDto.setProductName(product.getName());
                                return inventoryDto;
                            });
                });
    }
    public Flux<Inventory> findAll() {
        return inventoryRepository.findAll();
    }

    public Flux<Inventory> findInventoryByDateReceivedBetween(LocalDate startDate, LocalDate endDate) {
        return inventoryRepository.findByDateReceivedBetween(startDate, endDate);
    }

    public Flux<InventoryDto> findByFilters(FilterInventoryRequestDto filters) {
        Flux<Inventory> inventoryFlux;

        if (!StringUtils.isNotBlank(filters.getStatus()) && filters.getBranchId()==null ){
            inventoryFlux = inventoryRepository.findByDateReceivedBetween(
                    filters.getStarDate(),
                    filters.getFinishDate());//.skip(2).take(10);
            // Apply pagination: skip the first 10 records and take the next 10.

        }else if (!StringUtils.isNotBlank(filters.getStatus())) {
            inventoryFlux = inventoryRepository.findByDateReceivedBetweenAndBranchId(
                    filters.getStarDate(),
                    filters.getFinishDate(),
                    filters.getBranchId()
            );//.skip(2).take(10);
        }else {
            inventoryFlux = inventoryRepository.findByDateReceivedBetweenAndStatusAndBranchId(
                    filters.getStarDate(),
                    filters.getFinishDate(),
                    filters.getStatus(),
                    filters.getBranchId()
            );//.skip(2).take(10);
        }
        return addBranchToInventoryDto(inventoryFlux);
    }

//    public Mono<InventoryDto> getInventoryDetails(Long id) {
//        return inventoryRepository.findById(id)
//                .flatMap(inventory -> {
//                    Mono<Branch> branchMono = branchRepository.findById(inventory.getBranchId());
//                    Mono<Supplier> supplierMono = supplierRepository.findById(inventory.getSupplierId());
//
//                    return Mono.zip(branchMono, supplierMono)
//                            .map(tuple -> {
//                                Branch branch = tuple.getT1();
//                                Supplier supplier = tuple.getT2();
//                                InventoryDto dto = InventoryDto.fromEntity(inventory);
//                                if (branch != null) {
//                                    dto.setBranchName(branch.getBranchName());
//                                }
//                                if (supplier != null) {
//                                    dto.setSupplierName(supplier.getName());
//                                }
//                                return dto;
//                            })
//                            .defaultIfEmpty(InventoryDto.fromEntity(inventory));
//                });
//    }

    private Flux<InventoryDto> addBranchToInventoryDto(Flux<Inventory> inventoryFlux) {
        return inventoryFlux.flatMap(inventory -> {
            return branchRepository.findById(inventory.getBranchId())
                    .map(branch -> {
                        //https://avm02saaxg.execute-api.us-east-2.amazonaws.com/v1/api/products/1
                        InventoryDto inventoryDto = InventoryDto.fromEntity(inventory);
                        inventoryDto.setBranchName(branch.getBranchName());
                        System.out.println("branchname" + branch.getBranchName());
                        return inventoryDto;
                    })
                    .defaultIfEmpty(InventoryDto.fromEntity(inventory));
        });
    }

    private Flux<InventoryDto> getBranchToDto(Flux<Inventory> inventoryFlux) {
        return inventoryFlux.flatMap(inventory -> {
            return branchRepository.findById(inventory.getBranchId())
                    .map(branch -> {
                        InventoryDto inventoryDto = InventoryDto.fromEntity(inventory);
                        inventoryDto.setBranchName(branch.getBranchName());
                        System.out.println("branchname" + branch.getBranchName());
                        return inventoryDto;
                    })
                    .defaultIfEmpty(InventoryDto.fromEntity(inventory));
        });
    }
//
//    // Used to pass data and errors between steps
//    private static class InventoryValidationResult {
//        List<Inventory> validatedInventories;
//        List<Long> notFoundProductIds;
//        Map<Long, String> validationErrors;
//
//        // Constructor to initialize lists
//        public InventoryValidationResult(List<Inventory> validatedInventories, List<Long> notFoundProductIds, Map<Long, String> validationErrors) {
//            this.validatedInventories = validatedInventories;
//            this.notFoundProductIds = notFoundProductIds;
//            this.validationErrors = validationErrors;
//        }
//
//        // Getters (omitted for brevity)
//    }

    public Mono<InventoryValidationResult> checkNotFound(List<InventoryUpdateItemRequest> updateItems) {
        List<Long> requestedProductIds = updateItems.stream()
                .map(InventoryUpdateItemRequest::getProductId)
                .collect(Collectors.toList());
        boolean isZero= updateItems.get(0).getType() == 0; //Check if is substracting
        int quantity = -1; //Search Greather than
        if(isZero){
            quantity=0;
        }
        return inventoryRepository.findByProductIdInAndQuantityGreaterThan(requestedProductIds,quantity)
        .collectList()
//        return inventoryValidationResultMono
//                .collectList()
                .map(foundInventories -> {
                    // Determine found IDs
                    Set<Long> foundProductIds = foundInventories.stream()
                            .map(Inventory::getProductId)
                            .collect(Collectors.toSet());
                    // Determine NOT found IDs
                    List<Long> notFoundProductIds = requestedProductIds.stream()
                            .filter(id -> !foundProductIds.contains(id))
                            .collect(Collectors.toList());

                    // Return the result with found items and the list of not-founds
                    return new InventoryValidationResult(
                            foundInventories,
                            notFoundProductIds,
                            Collections.emptyMap()
                    );
                });
    }

    /**
     * Service 2: Performs the business rule validation (new quantity < 0).
     * Updates the inventory object in-memory.
     */
    public Mono<InventoryValidationResult> validateQuantities(List<Inventory> foundInventories, List<InventoryUpdateItemRequest> updateItems, List<Long> notFoundProductIds) {
        List<Inventory> validInventories = new ArrayList<>();
        Map<Long, String> errors = new HashMap<>();

        System.out.println("foundInventories----------------->");
        for (Inventory found:foundInventories){
            System.out.println("foundInventories----------------->ProductId= "+found.getProductId()+" , IdInventory= "+found.getId());
        }

//        for (Inventory inventory : foundInventories) {
        for (Inventory inventory : getDistinctInventoriesByProductId(foundInventories)) {
                InventoryUpdateItemRequest updateItem = updateItems.stream()
                        .filter(item -> item.getProductId().equals(inventory.getProductId()))
                        .findFirst()
                        .orElse(null);

                if (updateItem != null) {
                    int newQuantity;
                    if(updateItem.getType()==0) {
                        newQuantity = inventory.getQuantity() - updateItem.getQuantity();
                    }else {
                        newQuantity = inventory.getQuantity() + updateItem.getQuantity();
                    }
                    if (newQuantity < 0) {
                        // Add to errors map and SKIP saving this item
                        errors.put(inventory.getProductId(), "Cantidad en el Inventario no puede ser menor a cero ");
                    } else {
                        // Valid: update the quantity in-memory and add to the list of items to be saved
                        inventory.setQuantity(newQuantity);
                        validInventories.add(inventory);
                    }
                }
        }


        System.out.println("Inventory----------------->");
        for (Inventory found:validInventories){
            System.out.println("Inventory----------------->ProductId= "+found.getProductId()+" , IdInventory= "+found.getId());
        }

        // Return the result containing validated items (ready for save) and any errors
        return Mono.just(new InventoryValidationResult(
                validInventories,
                notFoundProductIds,
                errors
        ));
    }

    public List<Inventory> getDistinctInventoriesByProductId(List<Inventory> foundInventories) {
        if (foundInventories == null || foundInventories.isEmpty()) {
            return Collections.emptyList();
        }

        return foundInventories.stream()
                .collect(Collectors.toMap(
                        Inventory::getProductId,      // Key mapper: use productId
                        Function.identity(),          // Value mapper: use the Inventory object itself
                        (existing, replacement) -> existing // Merge function: If duplicate, keep the 'existing' (first) item
                ))
                // Get the distinct Inventory objects from the Map's values
                .values().stream()
                // Collect them into a new List
                .collect(Collectors.toList());
    }


    /**
     * Service 3: Saves the validated inventory items to the database.
     */
    @Transactional
    public Mono<List<Inventory>> saveInventories(List<Inventory> inventoriesToSave) {
        if (inventoriesToSave.isEmpty()) {
            return Mono.just(Collections.emptyList());
        }
        // Perform the batch save
        return inventoryRepository.saveAll(inventoriesToSave).collectList();
    }

    @Transactional
    public Mono<InventoryUpdateItemResponse> updateQuantities(List<InventoryUpdateItemRequest> updateItems) {
        // 1. Check for Not Found items (Service 1)
        return checkNotFound(updateItems)
                // 2. Validate Quantities (Service 2)
                .flatMap(result1 ->
                        validateQuantities(result1.getValidatedInventories(), updateItems, result1.getNotFoundProductIds())
                )
                // 3. Save Valid items (Service 3)
                .flatMap(result2 -> {
                    // If there are ANY validation errors, we skip the save operation
                    if (!result2.getValidationErrors().isEmpty()) {
                        // Return the result immediately to map to the final error response
                        return Mono.just(new InventoryValidationResult(
                                Collections.emptyList(),
                                result2.getNotFoundProductIds(),
                                result2.getValidationErrors()
                        ));
                    }
                    if (!result2.getNotFoundProductIds().isEmpty()) {
                        // Return the result immediately to map to the final error response
                        return Mono.just(new InventoryValidationResult(
                                Collections.emptyList(),
                                result2.getNotFoundProductIds(),
                                result2.getValidationErrors()
                        ));
                    }
                    // Only proceed to save if no validation errors were found
                    return saveInventories(result2.getValidatedInventories())
                            .map(updatedList -> new InventoryValidationResult(
                                    updatedList,
                                    result2.getNotFoundProductIds(),
                                    result2.getValidationErrors()
                            ));
                })
                // 4. Map to the final public response object
                .map(finalResult -> {
                    InventoryUpdateItemResponse resp= new InventoryUpdateItemResponse();
                    InventoryUpdateItemResponseDetails response = new InventoryUpdateItemResponseDetails();
                    response.setUpdatedInventories(finalResult.getValidatedInventories());
                    response.setNotFoundStockForProduct(finalResult.getNotFoundProductIds());
                    response.setErrorProductIds(finalResult.getValidationErrors());
                    resp.setBody(response);
                    if (!finalResult.getValidationErrors().isEmpty() || !finalResult.getNotFoundProductIds().isEmpty()){
                        resp.setMesssage("Ocurrio un Error");
                        resp.setCodResponse("001");
                    }else{
                        resp.setMesssage("Mensaje exitoso ");
                        resp.setCodResponse("000");
                        System.out.println("Enviando Movimiento de Inventario");
                        inventoryMovementService.senListToAWSQueue(updateItems,finalResult.getValidatedInventories());
                    }
                    return resp;
                });
    }
    // Additional business logic methods could go here
    // e.g., decreaseStock(productId, quantity)
}
package com.arka.inventory.service.external;

import com.arka.inventory.configs.KeysConfiguration;
import com.arka.inventory.dto.ProductDto;
import com.arka.inventory.entity.Branch;
import com.arka.inventory.repository.BranchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class ExternalServiceImpl implements ExternalService{
    private final int apiTimeout;
    private final WebClient webClient;
    private  final KeysConfiguration configs;
    private  final BranchRepository branchRepository;

    public ExternalServiceImpl(
            WebClient.Builder webClientBuilder,
            KeysConfiguration configs,
            BranchRepository branchRepository
    ) {
        this.apiTimeout = configs.getApiTimeout();
        this.configs = configs;
        this.webClient = webClientBuilder.baseUrl(configs.getProductBaseUrl()).build();
        this.branchRepository = branchRepository;
        // Optional: Log the initialized base URL for confirmation
        System.out.println("WebClient initialized with base URL: " + configs.getProductBaseUrl());
        System.out.println("WebClient initialized with apitimeout: " + configs.getApiTimeout());
    }

    /**
     * 2. Creates a reusable method for the internal Branch repository call.
     * * @param productId The ID of the product to fetch.
     * @return A Mono that emits the Products object, or an empty Product on not found.
     */
    public Mono<ProductDto> fetchProductDetails(Long productId) {
        System.out.println("fetchProductDetails(Long productId)"+configs.getProductBaseUrl());
        // 2. Define the asynchronous operation
        return webClient.get()
                .uri("/v1/api/products/{productId}", productId) // Use a clearer path
                .retrieve()
                .bodyToMono(ProductDto.class)
                .doOnError(e -> System.out.println("Status Error: " + e.getMessage())) // ADD THIS LINE
                // 3. Handle errors: print the error and return a default DTO
                .onErrorResume(e -> {
                    System.err.println("Product API failed for ID " + productId + ": " + e.getMessage());
                    // Return a default/empty DTO to allow the composition to proceed
                    return Mono.just(new ProductDto());
                });
    }

    /**
     * 2. Creates a reusable method for the internal Branch repository call.
     * * @param branchId The ID of the branch to fetch.
     * @return A Mono that emits the Branch object, or an empty Branch on not found.
     */
    public Mono<Branch> fetchBranchDetails(Long branchId) {
        // 3. Define the asynchronous operation using the reactive repository
        return branchRepository.findById(branchId)
                // 4. Handle not found: return a default empty Branch object
                .defaultIfEmpty(new Branch());
    }
}

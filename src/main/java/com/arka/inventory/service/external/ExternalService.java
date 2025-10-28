package com.arka.inventory.service.external;

import com.arka.inventory.dto.ProductDto;
import reactor.core.publisher.Mono;

public interface ExternalService {
    Mono<ProductDto> fetchProductDetails(Long productId);
}

package com.arka.inventory.aws;

import com.arka.inventory.dto.restobjects.InventoryMovementRequestDto;
import com.arka.inventory.entity.InventoryTransaction;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

public class SqsTransformer {
    private final ObjectMapper objectMapper;
    /**
     * Initializes the ObjectMapper with support for Java 8 Date/Time types (like LocalDate).
     */
    public SqsTransformer() {
        this.objectMapper = JsonMapper.builder()
                .addModule(new JavaTimeModule())
                .build();
    }
    /**
     * Transforms a single JSON string message into an InventoryMovementRequestDto object.
     * It uses Mono.fromCallable for safe, non-blocking execution of the deserialization.
     * * @param jsonString The raw message body received from SQS.
     * @return A Mono emitting the deserialized DTO.
     */
    public Mono<InventoryMovementRequestDto> toDto(String jsonString) {
        return Mono.fromCallable(() -> {
            try {
                // Deserialize the JSON string into the target DTO class
                return objectMapper.readValue(jsonString, InventoryMovementRequestDto.class);
            } catch (IOException e) {
                // Wrap the checked exception in a runtime exception for the reactive stream
                throw new IllegalStateException("Failed to deserialize SQS message body to DTO: " + jsonString, e);
            }
        });
    }

    /**
     * Transforms a raw JSON string containing an array of objects into a Flux of DTOs.
     * * @param jsonArrayString The raw message body (expected to be a JSON array).
     * @return A Flux emitting the deserialized DTOs one by one.
     */
    public Flux<InventoryMovementRequestDto> convertJsonStringToFlux(String jsonString) {

        // 1. Define the target type: a List of your DTO.
        TypeReference<List<InventoryMovementRequestDto>> listType =
                new TypeReference<>() {};
        try {
            // 2. Deserialize the JSON string into the List.
            List<InventoryMovementRequestDto> dtoList =
                    objectMapper.readValue(jsonString, listType);
            // 3. Convert the List (Iterable) into a Flux.
            return Flux.fromIterable(dtoList);

        } catch (IOException e) {
            // Handle deserialization errors (e.g., malformed JSON, incorrect date format)
            System.err.println("Error deserializing JSON string: " + e.getMessage());
            // It's best practice to throw a meaningful exception or return Flux.error()
            return Flux.error(new IllegalArgumentException("Invalid JSON payload for movement requests.", e));
        }
    }
    /**
     * Transforms a single DTO object into a JSON string message.
     * It uses Mono.fromCallable for safe, non-blocking execution of the serialization.
     * @param dto The DTO object to be serialized.
     * @return A Mono emitting the resulting JSON string.
     */
    public Mono<String> toString(InventoryMovementRequestDto dto) {
        return Mono.fromCallable(() -> {
            try {
                // Serialize the DTO object into a JSON string
                // use writeValueAsString for single object serialization
                return objectMapper.writeValueAsString(dto);
            } catch (IOException e) {
                // Handle serialization errors (e.g., circular references, mapping issues)
                throw new IllegalStateException("Failed to serialize DTO to JSON string: " + dto.toString(), e);
            }
        });
    }

    /**
     * Transforms a Flux of DTO objects into a single JSON array string.
     * This is useful if you want to send a batch of transactions as one message.
     * @param dtoList The List of DTO objects to be serialized.
     * @return A Mono emitting the resulting JSON array string.
     */
    public Mono<String> listToString(List<InventoryTransaction> dtoList) {
        return Mono.fromCallable(() -> {
            try {
                // Serialize the List object into a JSON array string
                return objectMapper.writeValueAsString(dtoList);
            } catch (IOException e) {
                throw new IllegalStateException("Failed to serialize DTO List to JSON string.", e);
            }
        });
    }
}
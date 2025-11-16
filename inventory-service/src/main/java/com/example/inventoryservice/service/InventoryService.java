package com.example.inventoryservice.service;

import com.example.inventoryservice.client.UserClient;
import com.example.inventoryservice.domain.InventoryItem;
import com.example.inventoryservice.mapping.InventoryItemMapper;
import com.example.inventoryservice.publisher.InventoryEventPublisher;
import com.example.inventoryservice.repository.InventoryItemRepository;
import com.example.inventoryservice.web.dto.InventoryItemCreateRequest;
import com.example.inventoryservice.web.dto.InventoryItemResponse;
import com.example.inventoryservice.web.dto.InventoryItemUpdateRequest;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repository;
    private final InventoryItemMapper mapper;
    private final UserClient userClient;
    private final InventoryEventPublisher eventPublisher;

    /**
     * Create new inventory item.
     * 1. Validate owner user via user-service (Feign client)
     * 2. Map DTO to entity and persist
     * 3. Publish ITEM_CREATED event to Kafka
     */
    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackCreateWithUnknownUser")
    public InventoryItemResponse createItem(InventoryItemCreateRequest request) {
        // 1. Call user-service to validate owner exists.
        userClient.getUserById(request.getOwnerUserId());

        // 2. Map request to entity and set timestamps.
        InventoryItem entity = mapper.toEntity(request);
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());

        InventoryItem saved = repository.save(entity);

        // 3. Publish domain event.
        eventPublisher.publishItemCreated(saved);

        return mapper.toResponse(saved);
    }

    /**
     * Circuit breaker fallback if user-service is down when creating item.
     */
    public InventoryItemResponse fallbackCreateWithUnknownUser(
            InventoryItemCreateRequest request,
            Throwable t
    ) {
        throw new IllegalStateException("Cannot validate owner user. user-service unavailable.", t);
    }

    /**
     * Get single item by id.
     */
    @Transactional(readOnly = true)
    public InventoryItemResponse getItem(Long id) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));
        return mapper.toResponse(item);
    }

    /**
     * List all items belonging to a specific user.
     */
    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsForUser(Long userId) {
        return repository.findByOwnerUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    /**
     * Update item data.
     * 1. Load entity
     * 2. Apply changes from request
     * 3. Save and publish ITEM_UPDATED event
     */
    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemUpdateRequest request) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));

        mapper.updateEntity(request, item);
        item.setUpdatedAt(Instant.now());

        InventoryItem saved = repository.save(item);

        // Publish domain event.
        eventPublisher.publishItemUpdated(saved);

        return mapper.toResponse(saved);
    }

    /**
     * Delete item and publish ITEM_DELETED event.
     */
    @Transactional
    public void deleteItem(Long id) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));

        repository.delete(item);

        // Publish domain event with id and name for traceability.
        eventPublisher.publishItemDeleted(item.getId(), item.getName());
    }
}

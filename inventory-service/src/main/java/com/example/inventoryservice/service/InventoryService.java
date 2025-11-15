package com.example.inventoryservice.service;

import com.example.inventoryservice.client.UserClient;
import com.example.inventoryservice.domain.InventoryItem;
import com.example.inventoryservice.mapping.InventoryItemMapper;
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

    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackCreateWithUnknownUser")
    public InventoryItemResponse createItem(InventoryItemCreateRequest request) {
        // Call user-service to validate owner exists
        userClient.getUserById(request.getOwnerUserId());

        InventoryItem entity = mapper.toEntity(request);
        InventoryItem saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public InventoryItemResponse fallbackCreateWithUnknownUser(InventoryItemCreateRequest request, Throwable t) {
        throw new IllegalStateException("Cannot validate owner user. user-service unavailable.", t);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse getItem(Long id) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));
        return mapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsForUser(Long userId) {
        return repository.findByOwnerUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemUpdateRequest request) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));
        mapper.updateEntity(request, item);
        item.setUpdatedAt(Instant.now());
        InventoryItem saved = repository.save(item);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!repository.existsById(id)) {
            throw new InventoryItemNotFoundException(id);
        }
        repository.deleteById(id);
    }
}

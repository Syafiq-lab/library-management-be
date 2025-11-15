package com.example.inventoryservice.web;

import com.example.common.api.PageResponse;
import com.example.inventoryservice.service.InventoryService;
import com.example.inventoryservice.web.dto.InventoryItemCreateRequest;
import com.example.inventoryservice.web.dto.InventoryItemResponse;
import com.example.inventoryservice.web.dto.InventoryItemUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Tag(name = "Inventory", description = "Inventory management APIs")
public class InventoryController {

    private final InventoryService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new inventory item")
    public InventoryItemResponse createItem(@Valid @RequestBody InventoryItemCreateRequest request) {
        return service.createItem(request);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get inventory item by id")
    public InventoryItemResponse getItem(@PathVariable Long id) {
        return service.getItem(id);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get inventory items for user")
    public PageResponse<InventoryItemResponse> getItemsForUser(@PathVariable Long userId) {
        List<InventoryItemResponse> items = service.getItemsForUser(userId);
        PageImpl<InventoryItemResponse> page = new PageImpl<>(items);
        return PageResponse.<InventoryItemResponse>builder()
                .content(page.getContent())
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update inventory item")
    public InventoryItemResponse updateItem(@PathVariable Long id,
                                            @Valid @RequestBody InventoryItemUpdateRequest request) {
        return service.updateItem(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete inventory item")
    public void deleteItem(@PathVariable Long id) {
        service.deleteItem(id);
    }
}

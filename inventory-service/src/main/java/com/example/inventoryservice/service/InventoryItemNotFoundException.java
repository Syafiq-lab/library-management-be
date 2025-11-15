package com.example.inventoryservice.service;

public class InventoryItemNotFoundException extends RuntimeException {
    public InventoryItemNotFoundException(Long id) {
        super("Inventory item with id " + id + " not found");
    }
}

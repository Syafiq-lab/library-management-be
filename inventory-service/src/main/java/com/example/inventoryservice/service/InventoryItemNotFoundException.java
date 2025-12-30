package com.example.inventoryservice.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InventoryItemNotFoundException extends RuntimeException {

	private static final Logger log = LoggerFactory.getLogger(InventoryItemNotFoundException.class);

    public InventoryItemNotFoundException(Long id) {
        super("Inventory item with id " + id + " not found");
    }
}
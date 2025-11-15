package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByOwnerUserId(Long ownerUserId);
}

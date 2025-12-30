package com.example.inventoryservice.repository;

import com.example.inventoryservice.domain.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {

	Logger log = LoggerFactory.getLogger(InventoryItemRepository.class);

    List<InventoryItem> findByOwnerUserId(Long ownerUserId);
}
package com.example.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {

	private static final Logger log = LoggerFactory.getLogger(InventoryEvent.class);


	private String eventId;
	private InventoryEventType eventType;

	private Long itemId;
	private String itemName;
	private Integer quantityChange;

	private String source;        // for example "inventory-service"
	private String reason;        // for example "CREATE", "UPDATE", "RESERVE", etc

	private Instant createdAt;
}
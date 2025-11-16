package com.example.inventoryservice.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryEvent {

	private String eventId;
	private InventoryEventType eventType;

	private Long itemId;
	private String itemName;
	private Integer quantityChange;

	private String source;        // for example "inventory-service"
	private String reason;        // for example "CREATE", "UPDATE", "RESERVE", etc

	private Instant createdAt;
}

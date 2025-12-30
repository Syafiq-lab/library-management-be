package com.example.inventoryservice.publisher;

import com.example.inventoryservice.domain.InventoryItem;
import com.example.inventoryservice.event.InventoryEvent;
import com.example.inventoryservice.event.InventoryEventType;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class InventoryEventPublisher {

	private static final Logger log = LoggerFactory.getLogger(InventoryEventPublisher.class);


	private final StreamBridge streamBridge;

	public InventoryEventPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	public void publishItemCreated(InventoryItem item) {
		InventoryEvent event = baseEventBuilder(item)
				.eventType(InventoryEventType.ITEM_CREATED)
				.quantityChange(item.getQuantity())
				.reason("CREATE")
				.build();

		streamBridge.send("inventory-events-out-0", event);
	}

	public void publishItemUpdated(InventoryItem item) {
		InventoryEvent event = baseEventBuilder(item)
				.eventType(InventoryEventType.ITEM_UPDATED)
				.reason("UPDATE")
				.build();

		streamBridge.send("inventory-events-out-0", event);
	}

	public void publishStockIncreased(InventoryItem item, int delta) {
		InventoryEvent event = baseEventBuilder(item)
				.eventType(InventoryEventType.STOCK_INCREASED)
				.quantityChange(delta)
				.reason("INCREASE_STOCK")
				.build();

		streamBridge.send("inventory-events-out-0", event);
	}

	public void publishStockDecreased(InventoryItem item, int delta) {
		InventoryEvent event = baseEventBuilder(item)
				.eventType(InventoryEventType.STOCK_DECREASED)
				.quantityChange(delta)
				.reason("DECREASE_STOCK")
				.build();

		streamBridge.send("inventory-events-out-0", event);
	}

	public void publishItemDeleted(Long itemId, String itemName) {
		InventoryEvent event = InventoryEvent.builder()
				.eventId(UUID.randomUUID().toString())
				.eventType(InventoryEventType.ITEM_DELETED)
				.itemId(itemId)
				.itemName(itemName)
				.source("inventory-service")
				.reason("DELETE")
				.createdAt(Instant.now())
				.build();

		streamBridge.send("inventory-events-out-0", event);
	}

	private InventoryEvent.InventoryEventBuilder baseEventBuilder(InventoryItem item) {
		return InventoryEvent.builder()
				.eventId(UUID.randomUUID().toString())
				.itemId(item.getId())
				.itemName(item.getName())
				.source("inventory-service")
				.createdAt(Instant.now());
	}
}
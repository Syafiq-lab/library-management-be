package com.example.inventoryservice.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum InventoryEventType {

	ITEM_CREATED,
	ITEM_UPDATED,
	STOCK_INCREASED,
	STOCK_DECREASED,
	ITEM_DELETED;

	private static final Logger log = LoggerFactory.getLogger(InventoryEventType.class);
}

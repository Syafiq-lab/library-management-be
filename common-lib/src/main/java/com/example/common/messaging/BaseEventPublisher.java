// common-lib/src/main/java/com/example/common/messaging/BaseEventPublisher.java
package com.example.common.messaging;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

public abstract class BaseEventPublisher<T> {

	private final StreamBridge streamBridge;

	protected BaseEventPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	protected void send(String bindingName, T payload) {
		Message<T> message = MessageBuilder.withPayload(payload).build();
		streamBridge.send(bindingName, message);
	}
}

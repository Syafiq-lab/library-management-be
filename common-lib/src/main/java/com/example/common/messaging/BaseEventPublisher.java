// common-lib/src/main/java/com/example/common/messaging/BaseEventPublisher.java
package com.example.common.messaging;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseEventPublisher<T> {

	private final StreamBridge streamBridge;

	protected BaseEventPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	protected void send(String bindingName, T payload) {
		String payloadType = (payload == null) ? "null" : payload.getClass().getSimpleName();
		log.debug("Sending event | binding={} | payloadType={}", bindingName, payloadType);

		Message<T> message = MessageBuilder.withPayload(payload).build();
		boolean sent = streamBridge.send(bindingName, message);

		if (sent) {
			log.debug("Event sent | binding={} | payloadType={}", bindingName, payloadType);
		} else {
			log.warn("Event send returned false | binding={} | payloadType={}", bindingName, payloadType);
		}
	}
}

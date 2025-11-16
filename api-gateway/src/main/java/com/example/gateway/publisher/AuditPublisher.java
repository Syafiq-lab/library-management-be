package com.example.gateway.publisher;

import com.example.common.event.EventEnvelope;
import com.example.gateway.event.AuditEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditPublisher {

	private final StreamBridge streamBridge;

	public AuditPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	public void publish(ServerHttpRequest request,
						int responseStatusCode,
						String traceId) {

		AuditEvent event = AuditEvent.builder()
				.path(request.getURI().getPath())
				.method(request.getMethod() != null ? request.getMethod().name() : "UNKNOWN")
				.statusCode(responseStatusCode)
				.timestamp(Instant.now())
				.build();

		EventEnvelope<AuditEvent> envelope = EventEnvelope.success(
				"AUDIT_HTTP_CALL",          // eventName
				"GATEWAY",                  // aggregateType
				null,                       // aggregateId not always needed here
				"HTTP request audited",     // message
				HttpStatus.OK,              // semantic status of the event itself
				event,                      // payload
				"api-gateway",              // sourceService
				traceId                     // traceId
		);

		streamBridge.send("audit-out-0", envelope);
	}
}

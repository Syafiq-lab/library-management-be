package com.example.gateway.publisher;

import com.example.common.event.EventEnvelope;
import com.example.gateway.event.AuditEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class AuditPublisher {

	private final StreamBridge streamBridge;

	public AuditPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	public void publish(ServerHttpRequest request,
						int responseStatusCode,
						String traceId) {

		HttpStatus resolved = HttpStatus.resolve(responseStatusCode);
		String statusForLog = (resolved != null) ? resolved.toString() : String.valueOf(responseStatusCode);

		String method = (request != null && request.getMethod() != null) ? request.getMethod().name() : null;
		String path = (request != null && request.getURI() != null) ? request.getURI().getPath() : null;

		log.debug("Preparing audit event traceId={}, method={}, path={}, status={}", traceId, method, path, statusForLog);

		AuditEvent event = AuditEvent.builder()
				.path(path)
				.method(method)
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

		boolean sent = streamBridge.send("audit-out-0", envelope);
		if (sent) {
			log.debug("Audit event sent to binding audit-out-0 traceId={} status={}", traceId, statusForLog);
		} else {
			log.warn("Failed to send audit event to binding audit-out-0 traceId={} status={}", traceId, statusForLog);
		}
	}
}

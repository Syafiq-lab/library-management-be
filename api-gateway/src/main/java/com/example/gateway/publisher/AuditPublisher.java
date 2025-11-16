package com.example.gateway.publisher;

import com.example.gateway.event.AuditEvent;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class AuditPublisher {

	private final StreamBridge streamBridge;

	public AuditPublisher(StreamBridge streamBridge) {
		this.streamBridge = streamBridge;
	}

	public void publish(ServerHttpRequest request, String traceId) {
		String method = request.getMethod() != null
				? request.getMethod().name()
				: "UNKNOWN";

		String clientIp = request.getHeaders().getFirst("X-Forwarded-For");
		if (clientIp == null || clientIp.isBlank()) {
			clientIp = request.getRemoteAddress() != null
					? request.getRemoteAddress().getAddress().getHostAddress()
					: "UNKNOWN";
		}

		AuditEvent event = AuditEvent.builder()
				.traceId(traceId)
				.path(request.getURI().getPath())
				.method(method)
				.clientIp(clientIp)
				.timestamp(Instant.now())
				.build();

		// binding name must match application config
		streamBridge.send("audit-out-0", event);
	}
}

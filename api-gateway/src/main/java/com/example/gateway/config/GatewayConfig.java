package com.example.gateway.config;

import com.example.gateway.publisher.AuditPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;

import java.util.List;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class GatewayConfig {

	@Bean
	public WebFilter auditWebFilter(AuditPublisher publisher) {

		return (exchange, chain) -> {
			String existingTraceId = getExistingTraceId(exchange);
			String traceId = existingTraceId != null ? existingTraceId : UUID.randomUUID().toString();

			// propagate trace id to downstream services
			ServerWebExchange mutatedExchange = exchange.mutate()
					.request(builder -> builder.header("X-Trace-Id", traceId))
					.build();

			return chain.filter(mutatedExchange)
					.doOnSuccess(done -> {
						ServerHttpResponse response = mutatedExchange.getResponse();
						int statusCode = response.getStatusCode() != null
								? response.getStatusCode().value()
								: 200;

                        log.debug("Publishing audit event traceId={}, method={}, path={}, status={}", traceId, mutatedExchange.getRequest().getMethod(), mutatedExchange.getRequest().getURI().getPath(), statusCode);
						publisher.publish(mutatedExchange.getRequest(), statusCode, traceId);
					});
		};
	}

	private String getExistingTraceId(ServerWebExchange exchange) {
		List<String> headerValues = exchange.getRequest().getHeaders().get("X-Trace-Id");
        // Trace id will be used for correlating gateway logs + audit events
		if (headerValues != null && !headerValues.isEmpty()) {
            log.debug("Using existing X-Trace-Id header: {}", headerValues.getFirst());
			return headerValues.getFirst();
		}
        log.debug("No X-Trace-Id header present on request {} {}", exchange.getRequest().getMethod(), exchange.getRequest().getURI());
		return null;
	}
}

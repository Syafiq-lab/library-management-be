package com.example.gateway.config;

import com.example.gateway.publisher.AuditPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

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

						publisher.publish(mutatedExchange.getRequest(), statusCode, traceId);
					});
		};
	}

	private String getExistingTraceId(ServerWebExchange exchange) {
		List<String> headerValues = exchange.getRequest().getHeaders().get("X-Trace-Id");
		if (headerValues != null && !headerValues.isEmpty()) {
			return headerValues.getFirst();
		}
		return null;
	}
}

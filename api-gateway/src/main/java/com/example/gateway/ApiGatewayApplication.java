package com.example.gateway;

import com.example.gateway.publisher.AuditPublisher;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.UUID;

@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public WebFilter auditWebFilter(AuditPublisher publisher) {
        return (exchange, chain) -> {
            var request = exchange.getRequest();

            String traceId = request.getHeaders().getFirst("X-Trace-Id");
            if (traceId == null || traceId.isBlank()) {
                traceId = UUID.randomUUID().toString();
            }

            publisher.publish(request, traceId);

            exchange.getResponse().getHeaders().add("X-Trace-Id", traceId);

            return chain.filter(exchange);
        };
    }

}

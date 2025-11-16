# api-gateway

## 1. Purpose

`api-gateway` is the **single entry point** for all clients (for example your frontend app).

It is built on **Spring Cloud Gateway (WebFlux)** and provides:

- Routing to downstream services via Eureka service discovery.
- Cross cutting concerns:
  - Circuit breakers
  - Fallback handling
  - Basic path rewriting
  - Audit logging of every request to Kafka
- OpenAPI / Swagger UI for documentation passthrough if needed.

---

## 2. Routing and communication

The gateway configuration (in `application.yml`) typically contains routes like:

```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/users/**      # external path
          filters:
            - StripPrefix=1           # remove /api before forwarding
            - name: CircuitBreaker
              args:
                name: userServiceCircuitBreaker
                fallbackUri: forward:/fallback/users

        - id: inventory-service
          uri: lb://inventory-service
          predicates:
            - Path=/api/inventory/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: inventoryServiceCircuitBreaker
                fallbackUri: forward:/fallback/inventory

        - id: qr-service
          uri: lb://qr-service
          predicates:
            - Path=/api/qr/**
          filters:
            - StripPrefix=1
            - name: CircuitBreaker
              args:
                name: qrServiceCircuitBreaker
                fallbackUri: forward:/fallback/qr
```

**What actually happens:**

1. Client calls `POST http://localhost:8080/api/inventory`.
2. Gateway matches the route: `/api/inventory/**`.
3. It strips the `/api` prefix and forwards to `lb://inventory-service/inventory`.
4. If `inventory-service` is healthy:
   - Response is returned to client.
5. If `inventory-service` fails or times out:
   - Circuit breaker calls the fallback endpoint
   - `FallbackController` returns a simple JSON saying the service is temporarily unavailable.

---

## 3. Audit logging via Kafka

Gateway uses a `WebFilter` and an `AuditPublisher` to send **AuditEvent** messages to Kafka:

- `AuditEvent` contains:
  - `traceId`
  - HTTP method
  - Path
  - Timestamp
  - Client host

### AuditPublisher

A typical implementation:

```java
@Service
public class AuditPublisher {

    private final StreamBridge streamBridge;

    public AuditPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }

    public void publish(ServerHttpRequest request, String traceId) {
        AuditEvent event = new AuditEvent(
                traceId,
                request.getURI().getPath(),
                request.getMethod().name(),
                System.currentTimeMillis()
        );
        streamBridge.send("audit-out-0", event);
    }
}
```

The binding `audit-out-0` is configured in `application.yml` to send to a Kafka topic, for example `audit-events`.

### Audit WebFilter

A `WebFilter` intercepts every request:

```java
@Bean
public WebFilter auditWebFilter(AuditPublisher publisher) {
    return (exchange, chain) -> {
        String traceId = UUID.randomUUID().toString();
        ServerHttpRequest mutated = exchange.getRequest()
                .mutate()
                .header("X-Trace-Id", traceId)
                .build();

        publisher.publish(mutated, traceId);

        return chain.filter(exchange.mutate().request(mutated).build());
    };
}
```

This ensures every incoming call is tagged with a trace ID and an audit event is sent out.

---

## 4. FallbackController

`FallbackController` is a simple Spring WebFlux controller that returns JSON responses when circuit breakers trigger:

```java
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/users")
    public Mono<Map<String, String>> usersFallback() {
        return Mono.just(Map.of("message", "User service is currently unavailable. Please try again later."));
    }

    // Similar endpoints for inventory and qr
}
```

---

## 5. Folder structure

```text
api-gateway/
  ├─ src/main/java/com/example/gateway/
  │    ├─ ApiGatewayApplication.java   # @SpringBootApplication
  │    ├─ config/                      # gateway config (WebFilter, etc)
  │    ├─ event/                       # AuditEvent
  │    ├─ publisher/                   # AuditPublisher
  │    └─ web/                         # FallbackController
  ├─ src/main/resources/
  │    └─ application.yml             # gateway and Kafka bindings
  └─ pom.xml
```

---

## 6. How to run

Make sure:

- `service-registry` is running
- Kafka is running
- Downstream services are running (user, inventory, qr)

Then run:

```bash
cd api-gateway
mvn spring-boot:run
```

Access example:

```text
GET  http://localhost:8080/api/users
GET  http://localhost:8080/api/inventory
GET  http://localhost:8080/api/qr
```

If downstream services are not running, you will see fallback responses.

---

## 7. Notes for senior developers

- Gateway is using WebFlux, which is fully reactive.
- Circuit breaker is based on Resilience4J via Spring Cloud Gateway filter.
- Audit events are sent via **Spring Cloud Stream with Kafka binder**; the gateway does not block on Kafka.

---

## 8. Notes for fresh developers

Important ideas:

- The gateway is just another Spring Boot app, but it does **not** have controllers for every business feature.
- Instead, it **forwards** requests to the correct microservice.
- To add a new microservice:
  1. Register it in `service-registry`.
  2. Add a new route in `api-gateway` `application.yml`.
  3. Optionally add circuit breaker + fallback.

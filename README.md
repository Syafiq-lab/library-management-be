# Advanced Spring Boot 3.5 Microservices Backend

This is a fully working backend template designed to sit behind your frontend.

## Stack

- Spring Boot 3.5.7 (Java 21)
- Spring Cloud 2025.0.0 (Northfields) for microservices patterns
- Spring Data JPA (H2 + Postgres)
- Spring Cloud Gateway, Eureka, Config Server, OpenFeign, Resilience4j
- Spring Security (basic auth starter setup)
- springdoc-openapi 2.8.14 (Swagger UI for every service)
- MapStruct 1.6.3 + Lombok for DTO mapping and boilerplate reduction
- Testcontainers for integration testing stubs
- ZXing for QR code generation and decoding

## Microservices

- `user-service`  
  Basic user management with OOP layering, DTOs for requests and responses, MapStruct mapping, validation and exception handling.

- `inventory-service`  
  Inventory items linked to users. Validates user existence via OpenFeign call to user-service. Demonstrates inter-service communication and Resilience4j.

- `qr-service`  
  QR code creator and reader:
  - Generate QR from arbitrary payload
  - Decode QR from base64 image
  - Persist basic QR audit record

- `api-gateway`  
  Single entry point with routing to user, inventory and QR services, circuit breaker and fallback, and an aggregated Swagger UI base.

- `service-registry`  
  Eureka discovery server.

- `config-server`  
  Spring Cloud Config server (file Git repo placeholder, easy to switch to real Git).

## How to run

1. Build all modules

   ```bash
   mvn clean install
   ```

2. Run in this order (each in its own terminal)

   ```bash
   cd service-registry && mvn spring-boot:run
   cd ../config-server && mvn spring-boot:run
   cd ../user-service && mvn spring-boot:run
   cd ../inventory-service && mvn spring-boot:run
   cd ../qr-service && mvn spring-boot:run
   cd ../api-gateway && mvn spring-boot:run
   ```


 Advanced Spring Boot Microservices Backend

This repository is a **multi module Spring Boot microservices template** designed for a real world backend with:

- Service discovery
- Centralized configuration
- API gateway with routing and circuit breakers
- Domain services for user, inventory, and QR
- Kafka based event streaming and audit logging
- MySQL as primary database

It is intentionally written so that **senior developers** can dive straight into modules and architecture, while **fresh developers** can learn from the folder structure, naming and comments.

---

## 1. High level architecture

**Core building blocks**

1. **Service Registry**  
   - Eureka server that keeps a registry of all running services.  
   - Other services register themselves and discover each other using logical names (`user-service`, `inventory-service`, `qr-service`, `api-gateway`, `config-server`).

2. **Config Server**  
   - Spring Cloud Config Server that reads configuration from a `config-repo` folder (or Git repo).  
   - Each service can fetch its configuration from here instead of hardcoding everything in its own jar.

3. **API Gateway**  
   - Spring Cloud Gateway (reactive, WebFlux).  
   - Acts as the single entry point for the frontend.  
   - Routes HTTP requests to downstream services using service discovery (`lb://user-service`, etc).  
   - Adds **cross cutting concerns**:
     - Circuit breakers with fallbacks
     - Basic header manipulation (strip prefix, remove cookies, add trace IDs)
     - Audit logging to Kafka

4. **Domain Services**
   - **user-service**  
     Manages user accounts, basic profile data, and security related operations.
   - **inventory-service**  
     Manages items owned by users (belongs to `ownerUserId`). Calls `user-service` to validate user existence.
   - **qr-service**  
     Generates and decodes QR codes using ZXing, and can persist metadata.

5. **common-lib**
   - Shared DTOs, event classes, and basic utilities that are used by multiple services to avoid duplication.

6. **Messaging and Audit**
   - Kafka is used as the message broker.
   - Spring Cloud Stream is used to **publish structured events**:
     - `AuditEvent` from API gateway to `audit-events` topic.
     - Domain events (user, inventory, qr) can be published to their own topics.
   - Other services may later subscribe to these topics to build analytics, notifications, etc.

7. **Database**
   - MySQL is used as the primary relational store.  
   - For simplicity, both `user-service` and `inventory-service` can point to the same `userdb` schema (or you can split later if needed).  
   - QR service can also use MySQL for persistence.

---

## 2. Module layout

At the top level:

```text
advanced-microservices-backend/
  ├─ pom.xml                      # parent Maven pom
  ├─ README.md                    # this file
  ├─ common-lib/
  ├─ service-registry/
  ├─ config-server/
  ├─ api-gateway/
  ├─ user-service/
  ├─ inventory-service/
  └─ qr-service/
```

### Typical service structure

Inside each service (for example `user-service`) you will see a layered structure:

```text
user-service/src/main/java/com/example/userservice/
  ├─ domain/          # JPA entities, domain models
  ├─ repository/      # Spring Data JPA repositories
  ├─ service/         # business logic (Application services)
  ├─ web/             # controllers and DTOs (request/response)
  ├─ mapping/         # MapStruct mappers
  ├─ client/          # HTTP clients to other microservices
  ├─ messaging/       # event publishers / consumers (Kafka, etc)
  └─ config/          # Spring configuration classes
```

This is a classic **hexagonal / layered** style:

- Controllers only deal with HTTP and DTOs.
- Services contain business rules.
- Repositories hide persistence details.
- Mappers convert between domain and DTOs.

---

## 3. How the services communicate

### 3.1 Frontend → Gateway → Backend services

1. **Frontend** calls `http://localhost:8080` (API gateway).
2. **API Gateway** uses **routing rules** to forward:
   - `/api/users/**` → `lb://user-service`
   - `/api/inventory/**` → `lb://inventory-service`
   - `/api/qr/**` → `lb://qr-service`
3. The `lb://` prefix means:
   - Lookup the service name in Eureka registry
   - Load balance across instances if multiple are running.

If a downstream service is down or slow, the **circuit breaker** on the gateway triggers and:

- Returns a fallback JSON from `FallbackController`
- Optionally logs or sends metrics.

### 3.2 Service → Service call

Example: `inventory-service` creates an item for a given user.

1. `inventory-service` receives `POST /api/inventory` with `ownerUserId`.
2. `InventoryService` calls `UserClient` (a HTTP client) which targets `user-service` via service discovery.
3. If `user-service` returns OK, the inventory item is saved.
4. If `user-service` is down, Resilience4J circuit breaker can fall back (or throw custom exception).

### 3.3 Event / MQ communication (Kafka)

Every HTTP request through gateway is audited:

1. API gateway `WebFilter` builds an `AuditEvent` with:
   - Trace ID
   - Path, method
   - Client IP
   - Timestamp
2. It uses `AuditPublisher` and `StreamBridge` to send `AuditEvent` to binding `audit-out-0`.
3. `audit-out-0` is mapped (via `application.yml`) to Kafka topic `audit-events`.
4. Any downstream consumer service can subscribe to `audit-events` for monitoring or analytics.

You can extend the same pattern for domain events:

- `user-service` → `user-events` topic
- `inventory-service` → `inventory-events` topic
- `qr-service` → `qr-events` topic

---

## 4. Basic things you must run locally

For a local developer setup you need:

1. **Java JDK**  
   - JDK 23 (or any version supported by your Spring Boot version).

2. **MySQL**
   - Example configuration:
     - Host: `localhost`
     - Port: `3306`
     - Database: `userdb`
     - User: `user`
     - Password: `password`
   - Make sure those values match each service `application.yml` (or later the config-server properties).

3. **Kafka + Zookeeper**
   - You can use Docker for convenience, for example:

     ```bash
     docker compose up -d   # if you have a docker-compose file
     ```

     or install Kafka locally and run:

     ```bash
     # example, adjust to your installation
     bin/zookeeper-server-start.sh config/zookeeper.properties
     bin/kafka-server-start.sh config/server.properties
     ```

4. **Config repo**
   - For now the services are still reading from local `application.yml` inside each module.
   - Later we will move those configurations to `config-repo` and point config-server to it.

---

## 5. Run order (local development)

Recommended order:

1. **Service registry**

   ```bash
   cd service-registry
   mvn spring-boot:run
   # Visit http://localhost:8761 to see the registry UI
   ```

2. **Config server**

   ```bash
   cd config-server
   mvn spring-boot:run
   # Exposes config at http://localhost:8888
   ```

3. **Infrastructure: MySQL + Kafka**  
   - Make sure MySQL and Kafka are running.

4. **Domain services**

   ```bash
   cd user-service
   mvn spring-boot:run

   cd inventory-service
   mvn spring-boot:run

   cd qr-service
   mvn spring-boot:run
   ```

5. **API gateway**

   ```bash
   cd api-gateway
   mvn spring-boot:run
   # Gateway now listens on http://localhost:8080
   ```

---

## 6. For senior developers

Things you may care about:

- **Version alignment**  
  - Parent pom manages Spring Boot, Spring Cloud, Spring Cloud Stream, MapStruct, Lombok versions.
- **Cross cutting concerns**  
  - Centralized config via config-server (once enabled).
  - Circuit breakers at gateway level.
  - Kafka based audit events.
- **Extensibility**  
  - Add new microservices by:
    - Creating a new module
    - Registering with Eureka
    - Adding a route in gateway
    - Optionally wiring Kafka producers / consumers for events.

---

## 7. For fresh developers

Key ideas to learn from this template:

1. **Separation of concerns**  
   - Controllers handle HTTP.
   - Services handle business logic.
   - Repositories handle DB.
   - Mappers convert between domain and DTOs.

2. **Why use an API gateway?**  
   - Frontend talks to a single URL.
   - We can centralize security, logging, and routing logic.

3. **Why use a service registry?**  
   - We do not hardcode `http://localhost:8081` for user-service.
   - We use service name (`lb://user-service`) and let Eureka do the lookup.

4. **Why message queue / Kafka?**  
   - HTTP is good for request reply.
   - Kafka is good for event driven patterns (audit, analytics, async workflows).

5. **How config will be centralized** (later step)  
   - Instead of each service having its own `application.yml`, we can define e.g.
     - `user-service.properties`
     - `inventory-service.properties`
   - Config server serves them, and services load them from there. This makes configuration changes easier in production.

---

## 8. Per service documentation

Each module has its own `README.md` with:

- Purpose
- Package layout
- Important configuration
- How it communicates with others
- Examples of typical flows

See:

- `service-registry/README.md`
- `config-server/README.md`
- `api-gateway/README.md`
- `user-service/README.md`
- `inventory-service/README.md`
- `qr-service/README.md`


## Swagger URLs (All Services)

### API Gateway (Aggregated Swagger UI)
- http://localhost:8080/swagger-ui.html
- http://localhost:8080/swagger-ui/index.html

### Gateway-proxied OpenAPI JSON
- http://localhost:8080/auth/v3/api-docs
- http://localhost:8080/users/v3/api-docs
- http://localhost:8080/inventory/v3/api-docs
- http://localhost:8080/qr/v3/api-docs

### Auth Service (Direct)
- http://localhost:8090/swagger-ui.html
- http://localhost:8090/v3/api-docs

### User Service (Direct)
- http://localhost:8091/swagger-ui.html
- http://localhost:8091/v3/api-docs

### Inventory Service (Direct)
- http://localhost:8092/swagger-ui.html
- http://localhost:8092/v3/api-docs

### QR Service (Direct)
- http://localhost:8093/swagger-ui.html
- http://localhost:8093/v3/api-docs

### Service Registry (Eureka) (Optional)
- http://localhost:8761
- http://localhost:8761/swagger-ui.html
- http://localhost:8761/v3/api-docs

### Config Server (Optional)
- http://localhost:8888
- http://localhost:8888/actuator/health

# API URLs (use **only** API Gateway on `:8080`)

Your services are exposed through Spring Cloud Gateway routes. The `StripPrefix` filter removes N path segments before forwarding downstream. :contentReference[oaicite:0]{index=0}

## Base API prefixes (via Gateway)

Use either the `/api/...` style or the short style:

### Auth Service
- `http://localhost:8080/api/auth/<endpoint>`
- `http://localhost:8080/auth/<endpoint>`

### User Service
- `http://localhost:8080/api/users/<endpoint>`
- `http://localhost:8080/users/<endpoint>`

### Inventory Service
- `http://localhost:8080/api/inventory/<endpoint>`
- `http://localhost:8080/inventory/<endpoint>`

### QR Service
- `http://localhost:8080/api/qr/<endpoint>`
- `http://localhost:8080/qr/<endpoint>`

> Replace `<endpoint>` with the actual controller path (see Swagger below).

---

## Swagger / OpenAPI (via Gateway)

### Aggregated Swagger UI (all services)
- `http://localhost:8080/swagger-ui.html`
- `http://localhost:8080/swagger-ui/index.html`

Springdoc commonly serves OpenAPI JSON at `/v3/api-docs` and Swagger UI via `/swagger-ui...` when enabled. :contentReference[oaicite:1]{index=1}

### OpenAPI JSON (per service, proxied through Gateway)
- `http://localhost:8080/auth/v3/api-docs`
- `http://localhost:8080/users/v3/api-docs`
- `http://localhost:8080/inventory/v3/api-docs`
- `http://localhost:8080/qr/v3/api-docs`

### Gateway’s own OpenAPI JSON (if enabled)
- `http://localhost:8080/v3/api-docs`

> Your gateway can aggregate multiple downstream OpenAPI specs using `springdoc.swagger-ui.urls`. :contentReference[oaicite:2]{index=2}

---

## How to get the full list of endpoints
Open:
- `http://localhost:8080/swagger-ui.html`

Then pick each service in the dropdown to see **all endpoints + request/response models**.

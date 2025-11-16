# inventory-service

## 1. Purpose

`inventory-service` manages items that belong to users.

Responsibilities:

- CRUD operations for `InventoryItem` entities.
- Validate that each item is owned by an existing user (via `user-service`).
- Persist inventory data in MySQL (`userdb`).
- Optionally publish inventory related events to Kafka (for audit or integration).

---

## 2. Service class overview

The core application service is `InventoryService`:

```java
@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryItemRepository repository;
    private final InventoryItemMapper mapper;
    private final UserClient userClient;

    @Transactional
    @CircuitBreaker(name = "userService", fallbackMethod = "fallbackCreateWithUnknownUser")
    public InventoryItemResponse createItem(InventoryItemCreateRequest request) {
        // Validate that owner exists in user-service
        userClient.getUserById(request.getOwnerUserId());

        InventoryItem entity = mapper.toEntity(request);
        InventoryItem saved = repository.save(entity);
        return mapper.toResponse(saved);
    }

    public InventoryItemResponse fallbackCreateWithUnknownUser(InventoryItemCreateRequest request, Throwable t) {
        throw new IllegalStateException("Cannot validate owner user. user-service unavailable.", t);
    }

    @Transactional(readOnly = true)
    public InventoryItemResponse getItem(Long id) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));
        return mapper.toResponse(item);
    }

    @Transactional(readOnly = true)
    public List<InventoryItemResponse> getItemsForUser(Long userId) {
        return repository.findByOwnerUserId(userId).stream()
                .map(mapper::toResponse)
                .toList();
    }

    @Transactional
    public InventoryItemResponse updateItem(Long id, InventoryItemUpdateRequest request) {
        InventoryItem item = repository.findById(id)
                .orElseThrow(() -> new InventoryItemNotFoundException(id));
        mapper.updateEntity(request, item);
        item.setUpdatedAt(Instant.now());
        InventoryItem saved = repository.save(item);
        return mapper.toResponse(saved);
    }

    @Transactional
    public void deleteItem(Long id) {
        if (!repository.existsById(id)) {
            throw new InventoryItemNotFoundException(id);
        }
        repository.deleteById(id);
    }
}
```

Key points:

- All write operations are wrapped in `@Transactional`.
- `@CircuitBreaker` around the user lookup ensures the service degrades gracefully when `user-service` is unavailable.
- Mapping between entities and DTOs is handled by MapStruct.

---

## 3. Packages and layering

Typical structure:

```text
com.example.inventoryservice
  ├─ domain/                # InventoryItem JPA entity
  ├─ repository/            # InventoryItemRepository
  ├─ service/               # InventoryService and possibly event publishers
  ├─ web/
  │    ├─ controller/       # InventoryController
  │    └─ dto/              # InventoryItemCreateRequest, InventoryItemUpdateRequest, InventoryItemResponse
  ├─ mapping/               # InventoryItemMapper
  ├─ client/                # UserClient (calls user-service)
  └─ config/                # Resilience4J, Feign/WebClient config, etc.
```

---

## 4. Database

- Uses MySQL `userdb` schema (shared with user-service in this setup).
- Example configuration:

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/userdb
      username: user
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
    jpa:
      hibernate:
        ddl-auto: update
      show-sql: true
      properties:
        hibernate.format_sql: true
  ```

The `InventoryItem` entity usually has fields like:

- `id`
- `ownerUserId`
- `name`
- `description`
- `quantity`
- `createdAt`
- `updatedAt`

---

## 5. Communication with user-service

`inventory-service` uses a client interface such as `UserClient` to call `user-service`.

Example (using OpenFeign pattern):

```java
@FeignClient(name = "user-service", path = "/users")
public interface UserClient {

    @GetMapping("/{id}")
    UserResponse getUserById(@PathVariable("id") Long id);
}
```

At runtime:

1. `InventoryService.createItem` calls `userClient.getUserById(ownerUserId)`.
2. Feign uses service discovery to locate `user-service` instance(s).
3. If the call succeeds, the item is saved.
4. If the call fails repeatedly, the Resilience4J circuit breaker will trigger and the fallback method is used.

---

## 6. Kafka / events (optional pattern)

You can extend `inventory-service` to publish events when items are created or updated.

Example pattern:

```java
@Service
@RequiredArgsConstructor
public class InventoryEventPublisher {

    private final StreamBridge streamBridge;

    public void publishItemCreated(InventoryItem item) {
        InventoryItemCreatedEvent event = new InventoryItemCreatedEvent(
            item.getId(),
            item.getOwnerUserId(),
            item.getName(),
            item.getQuantity(),
            Instant.now()
        );
        streamBridge.send("inventory-events-out-0", event);
    }
}
```

Then call this publisher from `createItem` after saving.

---

## 7. How to run

Preconditions:

- MySQL is running and `userdb` exists.
- `service-registry` and `user-service` are running (for full functionality).

Run:

```bash
cd inventory-service
mvn spring-boot:run
```

The service will:

- Register itself with Eureka under `inventory-service`.
- Expose endpoints behind the gateway as `/api/inventory/**`.

---

## 8. Notes for senior developers

- Circuit breaker is around cross service user validation only.
- Domain model is intentionally simple so it can be extended to support:
  - stock reservations
  - event sourcing
  - multi-warehouse setups.

---

## 9. Notes for fresh developers

Concepts to focus on:

- **Why validate owner user**  
  - Prevents creating items for non existing users.

- **Circuit breaker**  
  - Protects `inventory-service` from being dragged down if `user-service` is slow or down.

- **DTO vs entity**  
  - DTO is what you send via HTTP.
  - Entity is what you store in DB.

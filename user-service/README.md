# user-service

## 1. Purpose

`user-service` manages user accounts and basic profile data. Other services (such as `inventory-service`) depend on it to validate that a user exists.

Responsibilities:

- Persist users to MySQL (`userdb`).
- Expose REST APIs for creating, reading, updating, and deleting users.
- Provide an endpoint for other services (like inventory) to validate a user by ID.
- Optionally publish user related events to Kafka (for example `USER_CREATED`).

---

## 2. Packages and layering

Typical package layout:

```text
com.example.userservice
  ├─ domain/              # JPA entities (User, Role, etc)
  ├─ repository/          # Spring Data JPA repositories
  ├─ web/
  │    ├─ controller/     # REST controllers (UserController)
  │    └─ dto/            # Request/response DTOs
  ├─ service/             # Business services
  ├─ mapping/             # MapStruct mappers
  ├─ messaging/           # (Optional) Kafka event publishers
  ├─ client/              # (If this service calls others)
  └─ config/              # Spring / security / doc configuration
```

### Responsibilities per layer

- **Controller**  
  - Accepts HTTP requests (e.g. `POST /users`).
  - Validates incoming DTO using `jakarta.validation` annotations.
  - Calls `UserService`.

- **Service**  
  - Contains business logic (e.g. check for duplicates, hash password if needed).
  - Calls `UserRepository` and mappers.

- **Repository**  
  - Extends `JpaRepository<User, Long>` (or similar).
  - Encapsulates DB operations.

- **Mapper**  
  - Converts between `User` entity and DTOs (`UserResponse`, `UserCreateRequest`, etc).

---

## 3. Database

- Uses MySQL `userdb` schema (same DB that inventory-service is using in this template).
- Typical configuration (either via local `application.yml` or via config server):

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

---

## 4. Service to service communication

Other services (for example `inventory-service`) call `user-service` to validate an owner:

- A typical endpoint might be:

  ```http
  GET /users/{id}
  ```

- The gateway exposes this as:

  ```http
  GET /api/users/{id}
  ```

- `inventory-service` uses an HTTP client (`UserClient`) to call `user-service` through the gateway or through service discovery (`lb://user-service`).

If `user-service` is down, the caller uses **Resilience4J circuit breaker** to avoid hanging.

---

## 5. Kafka / messaging (optional pattern)

If you choose to publish user events:

- A `UserEventPublisher` can send events via Spring Cloud Stream:

  ```java
  public void publishUserCreated(User user) {
      UserCreatedEvent event = new UserCreatedEvent(
          user.getId(),
          user.getEmail(),
          Instant.now()
      );
      streamBridge.send("user-events-out-0", event);
  }
  ```

- The binding `user-events-out-0` is configured to map to a Kafka topic, such as `user-events`.

This decouples user-service from other services that may need to react to user creation.

---

## 6. How to run

Make sure MySQL and `service-registry` are running.

From this module:

```bash
cd user-service
mvn spring-boot:run
```

The service will:

- Register itself with Eureka under `user-service`.
- Expose its REST API on its configured `server.port` (check `application.yml`).
- Appear in the Eureka dashboard at `http://localhost:8761`.

---

## 7. Notes for senior developers

- JPA mappings are simple and designed to be easy to refactor later (e.g. move to dedicated user DB).
- Consider adding:
  - Proper security (JWT, OAuth2) and not exposing all endpoints publicly.
  - Versioned APIs (`/api/v1/users`).
  - Outbox pattern if you need reliable event publishing.

---

## 8. Notes for fresh developers

Key learning points:

- **Entity vs DTO**:  
  - Entity (`User`) is how we store data in DB.  
  - DTO (`UserResponse`) is how we return data in HTTP responses.

- **Repository**:  
  - You do not write SQL manually for basic CRUD; Spring Data JPA generates queries.

- **Service**:  
  - This is where you put logic like:
    - Check if email already exists.
    - Prevent deletion of certain system users.

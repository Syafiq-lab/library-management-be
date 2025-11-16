# common-lib

## 1. Purpose

`common-lib` is a shared module that holds code used across multiple microservices.

Typical contents:

- Shared **DTOs** (for example common response wrappers).
- Shared **event classes** (for Kafka messages).
- Shared **utility classes** (constants, error codes).
- Shared **validation annotations** if needed.

By placing these in `common-lib` you **avoid code duplication** and keep a single source of truth for cross service contracts.

---

## 2. How it is used

Each service that needs shared types declares a dependency:

```xml
<dependency>
    <groupId>com.example</groupId>
    <artifactId>common-lib</artifactId>
    <version>${project.version}</version>
</dependency>
```

Then you can import classes like:

```java
import com.example.common.event.AuditEvent;
import com.example.common.dto.ErrorResponse;
```

---

## 3. Guidelines

- Keep `common-lib` **lightweight**:
  - Avoid pulling in heavy frameworks or unnecessary dependencies.
- Only put things here that are truly **cross service**:
  - Example: `AuditEvent`, `UserCreatedEvent`, generic `ApiResponse`.
- Avoid putting service specific logic here.

---

## 4. Notes for fresh developers

- This module is not a microservice by itself; it does not run on its own.
- It is just a **library jar** used by other modules.

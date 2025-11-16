# service-registry

## 1. Purpose

`service-registry` hosts a **Netflix Eureka Server** that acts as a central registry for all running microservices.

- Each service registers itself with a logical name, for example:
  - `user-service`
  - `inventory-service`
  - `qr-service`
  - `api-gateway`
  - `config-server`
- Other services look up instances by these names instead of hardcoding host and port.

This enables:

- Load balancing
- Dynamic scaling (you can run multiple instances of the same service)
- Easier configuration for client services

---

## 2. How it works

When you start another microservice (for example `user-service`):

1. It has the Eureka client dependency (`spring-cloud-starter-netflix-eureka-client`).
2. In its configuration it points to the Eureka server URL:

   ```properties
   eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
   ```

3. At startup it registers itself with its service name:

   ```properties
   spring.application.name=user-service
   ```

4. Once registered, it appears on the Eureka dashboard at:

   ```text
   http://localhost:8761
   ```

---

## 3. Folder structure

```text
service-registry/
  ├─ src/main/java/com/example/registry/
  │    └─ ServiceRegistryApplication.java   # main Spring Boot app with @EnableEurekaServer
  ├─ src/main/resources/
  │    └─ application.yml / properties     # server.port, eureka configs
  └─ pom.xml
```

---

## 4. How to run

From the project root or from this module:

```bash
cd service-registry
mvn spring-boot:run
```

Then open:

```text
http://localhost:8761
```

You should see the Eureka dashboard. Initially it may show no applications. Once you start other services, they will appear under **Instances currently registered with Eureka**.

---

## 5. Notes for senior developers

- Eureka is used because it integrates smoothly with Spring Cloud.  
- In production you may:
  - Run multiple Eureka nodes (peer aware setup).
  - Turn off self preservation depending on your environment.
  - Secure the dashboard with Spring Security.

---

## 6. Notes for fresh developers

Things to remember:

- Eureka is **not** a database for your business data; it is only for **service locations**.
- Client services do not need to know exact host and port of others; they ask Eureka.
- This is why in the gateway config you see URIs like `lb://user-service` instead of `http://localhost:8081`.

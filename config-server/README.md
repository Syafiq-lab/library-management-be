# config-server

## 1. Purpose

`config-server` is a **Spring Cloud Config Server**. Its job is to serve external configuration for all microservices.

Instead of placing all configuration inside each jar, you put most of the configuration in a central **config repository** (folder or Git repo). Each service then pulls its configuration at startup.

Benefits:

- One place to manage configuration for all services.
- Easy to switch between environments (dev, test, prod).
- Configuration can be refreshed (if actuator / refresh is enabled).

---

## 2. How it works

1. `config-server` is configured to point to a location (for example a local folder `config-repo` or a Git repo):

   ```yaml
   spring:
     cloud:
       config:
         server:
           native:
             search-locations: file:../config-repo
   ```

2. Inside that config repo you create files like:

   ```text
   user-service.properties
   inventory-service.properties
   api-gateway.properties
   ```

3. When `user-service` starts, it can be configured (via bootstrap or environment) to contact config-server:

   ```properties
   spring.cloud.config.uri=http://localhost:8888
   spring.application.name=user-service
   ```

4. `config-server` returns the configuration for `user-service` from `user-service.properties`.

Later we can move your current `application.yml` content from each service into these files.

---

## 3. Folder structure

```text
config-server/
  ├─ src/main/java/com/example/configserver/
  │    └─ ConfigServerApplication.java          # @EnableConfigServer
  ├─ src/main/resources/
  │    └─ application.yml / properties          # config-server settings
  └─ pom.xml
```

---

## 4. How to run

From the module:

```bash
cd config-server
mvn spring-boot:run
```

Then check:

```text
http://localhost:8888
```

You should be able to access config endpoints such as:

```text
http://localhost:8888/user-service/default
```

(once the config repo is set up).

---

## 5. Notes for senior developers

- You can choose **native** backend (file system) or **Git** backend for configuration.
- In production, Git backend is common, and config server is usually **highly available**.
- Configuration encryption support is available if you need to store secrets.

---

## 6. Notes for fresh developers

Key points:

- `config-server` does not store business data, only **configuration** (like DB URLs, ports, feature flags).
- You generally:
  - Start `config-server` early.
  - Point all services to it via `spring.cloud.config.uri`.
- In this template, we still keep local `application.yml` in services for simplicity. The next step is to move those into the config repo.

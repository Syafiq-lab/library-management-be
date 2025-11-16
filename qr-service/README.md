# qr-service

## 1. Purpose

`qr-service` handles QR code generation and decoding.

Responsibilities:

- Generate QR code images (PNG, etc) for given payloads (for example watch IDs, inventory IDs, URLs).
- Decode QR images and return the embedded data.
- Optionally persist generated QR metadata in MySQL.
- Optionally publish QR related events to Kafka.

This is useful for your luxury watch supply chain project where every component or watch has a QR code attached.

---

## 2. Dependencies

Key dependencies from `pom.xml`:

- Spring Boot starters:
  - `spring-boot-starter-web`
  - `spring-boot-starter-data-jpa`
  - `spring-boot-starter-validation`
  - `spring-boot-starter-security` (if you secure the endpoints)
- Spring Cloud Eureka client
- Springdoc OpenAPI UI:
  - `org.springdoc:springdoc-openapi-starter-webmvc-ui`
- `common-lib`
- Databases:
  - MySQL
- QR library:
  - `com.google.zxing:core`
  - `com.google.zxing:javase`

---

## 3. Typical API design

Usual endpoints (your code may differ slightly, but conceptually):

1. **Generate QR**

   ```http
   POST /qr/generate
   Content-Type: application/json

   {
     "data": "WATCH-12345",
     "width": 300,
     "height": 300
   }
   ```

   Response: PNG bytes or a base64 encoded string, plus metadata.

2. **Decode QR**

   ```http
   POST /qr/decode
   Content-Type: multipart/form-data

   file: <uploaded qr image>
   ```

   Response:

   ```json
   {
     "data": "WATCH-12345"
   }
   ```

3. **List / persist metadata** (optional)

   If you store QR metadata in DB, you can have endpoints to list or query them.

The gateway routes these as:

```http
POST http://localhost:8080/api/qr/generate
POST http://localhost:8080/api/qr/decode
```

---

## 4. Folder structure

```text
com.example.qrservice
  ├─ web/
  │    ├─ controller/            # QrController
  │    └─ dto/                   # QrGenerateRequest, QrDecodeResponse, etc
  ├─ service/                    # QrService with ZXing integration
  ├─ domain/ (optional)          # QrCodeMetadata entity if persisted
  ├─ repository/ (optional)      # QrCodeMetadataRepository
  ├─ mapping/ (optional)         # MapStruct mappers
  ├─ messaging/ (optional)       # event publishers if needed
  └─ config/                     # security, swagger, etc.
```

---

## 5. ZXing usage

Core QR logic usually lives in a service class, calling ZXing:

```java
public BufferedImage generateQrImage(String data, int width, int height) {
    Map<EncodeHintType, Object> hints = new HashMap<>();
    hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

    BitMatrix matrix = new MultiFormatWriter()
            .encode(data, BarcodeFormat.QR_CODE, width, height, hints);

    return MatrixToImageWriter.toBufferedImage(matrix);
}

public String decodeQrImage(BufferedImage image) throws NotFoundException {
    LuminanceSource source = new BufferedImageLuminanceSource(image);
    BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

    Result result = new MultiFormatReader().decode(bitmap);
    return result.getText();
}
```

Controller then converts the `BufferedImage` to PNG bytes in the HTTP response.

---

## 6. Database

If you persist QR metadata:

- Use MySQL (either shared `userdb` or a separate schema like `qrdb`).
- Typical configuration:

  ```yaml
  spring:
    datasource:
      url: jdbc:mysql://localhost:3306/userdb
      username: user
      password: password
      driver-class-name: com.mysql.cj.jdbc.Driver
  ```

`QrCodeMetadata` might contain:

- `id`
- `data`
- `fileName`
- `createdAt`
- `createdBy` (optional)

---

## 7. Kafka / events (optional pattern)

You may publish events like `QrGeneratedEvent`:

```java
@Service
@RequiredArgsConstructor
public class QrEventPublisher {

    private final StreamBridge streamBridge;

    public void publishQrGenerated(String id, String data) {
        QrGeneratedEvent event = new QrGeneratedEvent(id, data, Instant.now());
        streamBridge.send("qr-events-out-0", event);
    }
}
```

This could be used by downstream services for logging, notifications, etc.

---

## 8. How to run

Preconditions:

- MySQL is running.
- `service-registry` is running (and optionally config-server).

Run:

```bash
cd qr-service
mvn spring-boot:run
```

The service will:

- Register itself into Eureka as `qr-service`.
- Expose its endpoints through the gateway at `/api/qr/**`.

---

## 9. Notes for senior developers

- ZXing is used directly; you can swap to another library if you need different formats.
- If volume is high, consider:
  - streaming responses
  - offloading heavy image processing to async workers.

---

## 10. Notes for fresh developers

Things to notice:

- This service is a good example of how to integrate a 3rd party library (ZXing) into a Spring Boot service.
- Try to separate QR logic into a dedicated `QrService` class, and keep controllers small.
- Understand how file upload works in Spring MVC for the decode endpoint.

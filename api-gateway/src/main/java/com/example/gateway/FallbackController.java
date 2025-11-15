package com.example.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> usersFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "User service is temporarily unavailable"
        ));
    }

    @GetMapping(value = "/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> inventoryFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "Inventory service is temporarily unavailable"
        ));
    }

    @GetMapping(value = "/qr", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> qrFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "QR service is temporarily unavailable"
        ));
    }
}

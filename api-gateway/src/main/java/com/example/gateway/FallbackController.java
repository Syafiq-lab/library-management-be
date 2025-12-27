package com.example.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class FallbackController {

    @GetMapping(path = "/fallback/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> usersFallback() {
        log.warn("Fallback triggered: /fallback/users");
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "user service is currently unavailable, please try again later"
        ));
    }

    @GetMapping(path = "/fallback/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> inventoryFallback() {
        log.warn("Fallback triggered: /fallback/inventory");
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "inventory service is currently unavailable, please try again later"
        ));
    }

    @GetMapping(path = "/fallback/qr", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> qrFallback() {
        log.warn("Fallback triggered: /fallback/qr");
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "qr service is currently unavailable, please try again later"
        ));
    }
}

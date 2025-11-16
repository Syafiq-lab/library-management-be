package com.example.gateway.web;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
public class FallbackController {

    @GetMapping(path = "/fallback/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> usersFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "user service is currently unavailable, please try again later"
        ));
    }

    @GetMapping(path = "/fallback/inventory", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> inventoryFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "inventory service is currently unavailable, please try again later"
        ));
    }

    @GetMapping(path = "/fallback/qr", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String, Object>> qrFallback() {
        return Mono.just(Map.of(
                "status", HttpStatus.SERVICE_UNAVAILABLE.value(),
                "message", "qr service is currently unavailable, please try again later"
        ));
    }
}

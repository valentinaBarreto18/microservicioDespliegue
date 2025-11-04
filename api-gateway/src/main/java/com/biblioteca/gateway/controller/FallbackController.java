package com.biblioteca.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Controlador de fallback para circuit breaker
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @GetMapping("/inventory")
    public Mono<ResponseEntity<Map<String, String>>> inventoryFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Inventory Service no está disponible");
        response.put("message", "Por favor intente más tarde");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }

    @GetMapping("/loan")
    public Mono<ResponseEntity<Map<String, String>>> loanFallback() {
        Map<String, String> response = new HashMap<>();
        response.put("error", "Loan Service no está disponible");
        response.put("message", "Por favor intente más tarde");
        return Mono.just(ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response));
    }
}


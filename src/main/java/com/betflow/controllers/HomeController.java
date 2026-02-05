package com.betflow.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
public class HomeController {

    @GetMapping("/")
    public ResponseEntity<Map<String, Object>> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Welcome to BetFlow Manager API");
        response.put("status", "running");
        response.put("time", LocalDateTime.now());
        response.put("documentation", "/graphiql");
        return ResponseEntity.ok(response);
    }
}

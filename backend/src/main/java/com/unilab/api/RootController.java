package com.unilab.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "Root")
public class RootController {

    @GetMapping
    @Operation(summary = "API Root - Redirects to Swagger UI")
    public ResponseEntity<Map<String, Object>> root() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Lab Booking API is running!");
        response.put("swagger_ui", "http://localhost:8080/swagger-ui.html");
        response.put("api_docs", "http://localhost:8080/v3/api-docs");
        response.put("health_check", "http://localhost:8080/api/health");
        response.put("events_api", "http://localhost:8080/api/events");
        return ResponseEntity.ok(response);
    }
}

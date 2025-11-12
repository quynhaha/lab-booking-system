package com.unilab.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/health")
@Tag(name = "Health")
public class HealthController {

    @GetMapping
    @Operation(summary = "Health check")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", "UP");
        payload.put("timestamp", OffsetDateTime.now().toString());
        return ResponseEntity.ok(payload);
    }
}



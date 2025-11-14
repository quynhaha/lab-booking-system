package com.unilab.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/")
@Tag(name = "Root")
public class RootController {

    @GetMapping
    @Operation(summary = "API Root - Shows API information dynamically")
    public ResponseEntity<Map<String, Object>> root(HttpServletRequest request) {

        String domain = request.getRequestURL().toString();
        if (domain.endsWith("/")) {
            domain = domain.substring(0, domain.length() - 1);
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Lab Booking API is running!");
        response.put("swagger_ui", domain + "/swagger-ui.html");
        response.put("api_docs", domain + "/v3/api-docs");
        response.put("health_check", domain + "/api/health");
        response.put("events_api", domain + "/api/events");

        return ResponseEntity.ok(response);
    }
}

package com.unilab.api;

import com.unilab.dto.LabSearchResponse;
import com.unilab.service.LabSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/labs/search")
@Tag(name = "Lab Search")
public class LabSearchController {
    
    @Autowired
    private LabSearchService labSearchService;
    
    @GetMapping("/equipment")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or hasRole('USER')")
    @Operation(summary = "Search labs by equipment name", description = "Search labs that have specific equipment")
    public ResponseEntity<?> searchLabsByEquipment(@RequestParam String equipmentName) {
        try {
            List<LabSearchResponse> labs = labSearchService.searchLabsByEquipment(equipmentName);
            
            if (labs.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No labs found with the specified equipment", "labs", labs));
            }
            
            return ResponseEntity.ok(Map.of("labs", labs, "count", labs.size()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
    
    @GetMapping("/equipment-type")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LECTURER') or hasRole('USER')")
    @Operation(summary = "Search labs by equipment type", description = "Search labs that have specific type of equipment")
    public ResponseEntity<?> searchLabsByEquipmentType(@RequestParam String equipmentType) {
        try {
            List<LabSearchResponse> labs = labSearchService.searchLabsByEquipmentType(equipmentType);
            
            if (labs.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No labs found with the specified equipment type", "labs", labs));
            }
            
            return ResponseEntity.ok(Map.of("labs", labs, "count", labs.size()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }
}

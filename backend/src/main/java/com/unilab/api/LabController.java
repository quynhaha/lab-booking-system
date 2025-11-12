package com.unilab.api;

import com.unilab.dto.LabDto;
import com.unilab.service.LabService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/labs")
@Tag(name = "Lab Management", description = "CRUD operations for labs")
public class LabController {

    @Autowired
    private LabService labService;

    // Get all labs
    @GetMapping
    @Operation(summary = "Get all labs", description = "Returns a list of all labs in the system")
    @ApiResponse(responseCode = "200", description = "List of labs retrieved successfully")
    public ResponseEntity<List<LabDto>> getAllLabs() {
        return ResponseEntity.ok(labService.getAllLabs());
    }

    // Get lab by ID
    @GetMapping("/{id}")
    @Operation(summary = "Get lab by ID", description = "Retrieve a single lab by its ID")
    public ResponseEntity<LabDto> getLabById(
            @Parameter(description = "ID of the lab to retrieve", example = "1")
            @PathVariable("id") Long id) {
        return ResponseEntity.ok(labService.getLabById(id));
    }

    // Search labs
    @GetMapping("/search")
    @Operation(summary = "Search labs by keyword", description = "Search labs by name or code")
    public ResponseEntity<List<LabDto>> searchLabs(
            @Parameter(description = "Keyword to search for (matches name or lab code)", example = "Computer")
            @RequestParam(name = "keyword") String keyword) {
        return ResponseEntity.ok(labService.searchLabs(keyword));
    }

    // Create new lab
    @PostMapping
    @Operation(summary = "Create new lab", description = "Add a new lab to the system")
    public ResponseEntity<LabDto> createLab(@Valid @RequestBody LabDto dto) {
        return ResponseEntity.ok(labService.createLab(dto));
    }

    // Update lab
    @PutMapping("/{id}")
    @Operation(summary = "Update lab information", description = "Update details of an existing lab")
    public ResponseEntity<LabDto> updateLab(
            @Parameter(description = "ID of the lab to update", example = "1")
            @PathVariable("id") Long id,
            @Valid @RequestBody LabDto dto) {
        return ResponseEntity.ok(labService.updateLab(id, dto));
    }

    // Delete lab
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete lab by ID", description = "Delete a lab from the system by its ID")
    public ResponseEntity<Void> deleteLab(
            @Parameter(description = "ID of the lab to delete", example = "1")
            @PathVariable("id") Long id) {
        labService.deleteLab(id);
        return ResponseEntity.noContent().build();
    }

    // Get available labs
    @GetMapping("/available")
    @Operation(summary = "Get all available labs", description = "Retrieve all labs that are currently available")
    public ResponseEntity<List<LabDto>> getAvailableLabs() {
        return ResponseEntity.ok(labService.getAvailableLabs());
    }
}

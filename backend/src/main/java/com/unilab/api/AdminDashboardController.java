package com.unilab.api;

import com.unilab.dto.DashboardDto;
import com.unilab.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
@Tag(name = "Admin Dashboard", description = "Admin dashboard and statistics endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AdminDashboardController {
    
    @Autowired
    private DashboardService dashboardService;
    
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get admin dashboard", description = "Get comprehensive admin dashboard with statistics and recent activity")
    public ResponseEntity<DashboardDto> getAdminDashboard() {
        DashboardDto dashboard = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(dashboard);
    }
    
    @GetMapping("/filter")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get dashboard filtered by date range", description = "Get admin dashboard filtered by specific date range")
    public ResponseEntity<DashboardDto> getDashboardByDateRange(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate
    ) {
        DashboardDto dashboard = dashboardService.getDashboardByDateRange(startDate, endDate);
        return ResponseEntity.ok(dashboard);
    }
}

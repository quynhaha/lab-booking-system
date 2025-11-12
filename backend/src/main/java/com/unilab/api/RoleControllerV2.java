package com.unilab.api;

import com.unilab.dto.RoleDto;
import com.unilab.model.Role;
import com.unilab.repository.RoleRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/roles")
@Tag(name = "Role Management V2")
public class RoleControllerV2 {

    private final RoleRepository roleRepository;

    public RoleControllerV2(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve a list of all available roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<RoleDto>> getAllRoles() {
        List<RoleDto> roles = roleRepository.findAll().stream()
                .map(role -> new RoleDto(role.getId(), role.getName()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get role by ID", 
        description = "Retrieve a specific role by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> getRoleById(
            @Parameter(
                name = "id", 
                description = "Role ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return ResponseEntity.ok(new RoleDto(role.get().getId(), role.get().getName()));
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    @Operation(summary = "Create a new role", description = "Create a new role with the specified name")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
        // Check if role name already exists
        if (roleRepository.findByName(roleDto.getName()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Role role = new Role();
        role.setName(roleDto.getName());
        Role savedRole = roleRepository.save(role);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new RoleDto(savedRole.getId(), savedRole.getName()));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update role by ID", 
        description = "Update an existing role with new information"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<RoleDto> updateRole(
            @Parameter(
                name = "id", 
                description = "Role ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id, 
            @Valid @RequestBody RoleDto roleDto) {
        Optional<Role> existingRole = roleRepository.findById(id);
        if (existingRole.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        // Check if another role with the same name exists
        Optional<Role> roleWithSameName = roleRepository.findByName(roleDto.getName());
        if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().equals(id)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        
        Role role = existingRole.get();
        role.setName(roleDto.getName());
        Role savedRole = roleRepository.save(role);
        
        return ResponseEntity.ok(new RoleDto(savedRole.getId(), savedRole.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete role by ID", 
        description = "Delete a role by its ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteRole(
            @Parameter(
                name = "id", 
                description = "Role ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id) {
        Optional<Role> role = roleRepository.findById(id);
        if (role.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        roleRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}

//package com.unilab.api;
//
//import com.unilab.dto.RoleDto;
//import com.unilab.model.Role;
//import com.unilab.repository.RoleRepository;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/roles")
//@Tag(name = "Role Management")
//public class RoleController {
//
//    private final RoleRepository roleRepository;
//
//    public RoleController(RoleRepository roleRepository) {
//        this.roleRepository = roleRepository;
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all roles")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<RoleDto>> getAllRoles() {
//        List<RoleDto> roles = roleRepository.findAll().stream()
//                .map(role -> new RoleDto(role.getId(), role.getName()))
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(roles);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get role by ID")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<RoleDto> getRoleById(
//            @Parameter(description = "Role ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id) {
//        Optional<Role> role = roleRepository.findById(id);
//        if (role.isPresent()) {
//            return ResponseEntity.ok(new RoleDto(role.get().getId(), role.get().getName()));
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PostMapping
//    @Operation(summary = "Create a new role")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<RoleDto> createRole(@Valid @RequestBody RoleDto roleDto) {
//        // Check if role name already exists
//        if (roleRepository.findByName(roleDto.getName()).isPresent()) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
//
//        Role role = new Role();
//        role.setName(roleDto.getName());
//        Role savedRole = roleRepository.save(role);
//
//        return ResponseEntity.status(HttpStatus.CREATED)
//                .body(new RoleDto(savedRole.getId(), savedRole.getName()));
//    }
//
//    @PutMapping("/{id}")
//    @Operation(summary = "Update role by ID")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<RoleDto> updateRole(
//            @Parameter(description = "Role ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id,
//            @Valid @RequestBody RoleDto roleDto) {
//        Optional<Role> existingRole = roleRepository.findById(id);
//        if (existingRole.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Check if another role with the same name exists
//        Optional<Role> roleWithSameName = roleRepository.findByName(roleDto.getName());
//        if (roleWithSameName.isPresent() && !roleWithSameName.get().getId().equals(id)) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).build();
//        }
//
//        Role role = existingRole.get();
//        role.setName(roleDto.getName());
//        Role savedRole = roleRepository.save(role);
//
//        return ResponseEntity.ok(new RoleDto(savedRole.getId(), savedRole.getName()));
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete role by ID")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteRole(
//            @Parameter(description = "Role ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id) {
//        Optional<Role> role = roleRepository.findById(id);
//        if (role.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        // Check if role is being used by any users
//        // This would require a custom query or service method
//        // For now, we'll allow deletion and let the database handle foreign key constraints
//
//        roleRepository.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//}

package com.unilab.api;

import com.unilab.dto.UserDto;
import com.unilab.dto.UserRoleDto;
import com.unilab.model.Role;
import com.unilab.model.User;
import com.unilab.repository.RoleRepository;
import com.unilab.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Management V2")
public class UserManagementControllerV2 {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserManagementControllerV2(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get user by ID", 
        description = "Retrieve a specific user by their ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(
            @Parameter(
                name = "id", 
                description = "User ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return ResponseEntity.ok(convertToDto(user.get()));
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/role")
    @Operation(
        summary = "Assign role to user", 
        description = "Assign a specific role to a user"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> assignRoleToUser(
            @Parameter(
                name = "id", 
                description = "User ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id, 
            @Valid @RequestBody UserRoleDto userRoleDto) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        Optional<Role> roleOpt = roleRepository.findById(userRoleDto.getRoleId());
        if (roleOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        
        User user = userOpt.get();
        user.setRole(roleOpt.get());
        User savedUser = userRepository.save(user);
        
        return ResponseEntity.ok(convertToDto(savedUser));
    }

    @PutMapping("/{id}/status")
    @Operation(
        summary = "Update user status", 
        description = "Activate or deactivate a user account"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> updateUserStatus(
            @Parameter(
                name = "id", 
                description = "User ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id, 
            @Parameter(
                name = "status", 
                description = "User status (true = active, false = inactive)", 
                required = true, 
                example = "true",
                in = ParameterIn.QUERY
            )
            @RequestParam("status") boolean status) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        User user = userOpt.get();
        user.setStatus(status);
        User savedUser = userRepository.save(user);
        
        return ResponseEntity.ok(convertToDto(savedUser));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete user by ID", 
        description = "Delete a user account by their ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @Parameter(
                name = "id", 
                description = "User ID", 
                required = true, 
                example = "1",
                in = ParameterIn.PATH
            )
            @PathVariable("id") Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-role/{roleName}")
    @Operation(
        summary = "Get users by role name", 
        description = "Retrieve all users that have a specific role"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getUsersByRole(
            @Parameter(
                name = "roleName", 
                description = "Role name (e.g., ADMIN, USER)", 
                required = true, 
                example = "ADMIN",
                in = ParameterIn.PATH
            )
            @PathVariable("roleName") String roleName) {
        List<UserDto> users = userRepository.findAll().stream()
                .filter(user -> user.getRole() != null && roleName.equals(user.getRole().getName()))
                .map(this::convertToDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    private UserDto convertToDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole() != null ? user.getRole().getName() : null,
                user.getStudentId(),
                user.getFaculty(),
                user.isStatus(),
                user.getCreatedAt()
        );
    }
}

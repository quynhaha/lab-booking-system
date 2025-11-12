//package com.unilab.api;
//
//import com.unilab.dto.UserDto;
//import com.unilab.dto.UserRoleDto;
//import com.unilab.model.Role;
//import com.unilab.model.User;
//import com.unilab.repository.RoleRepository;
//import com.unilab.repository.UserRepository;
//import io.swagger.v3.oas.annotations.Operation;
//import io.swagger.v3.oas.annotations.Parameter;
//import io.swagger.v3.oas.annotations.media.Schema;
//import io.swagger.v3.oas.annotations.tags.Tag;
//import jakarta.validation.Valid;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.Optional;
//import java.util.stream.Collectors;
//
//@RestController
//@RequestMapping("/api/users")
//@Tag(name = "User Management")
//public class UserManagementController {
//
//    private final UserRepository userRepository;
//    private final RoleRepository roleRepository;
//
//    public UserManagementController(UserRepository userRepository, RoleRepository roleRepository) {
//        this.userRepository = userRepository;
//        this.roleRepository = roleRepository;
//    }
//
//    @GetMapping
//    @Operation(summary = "Get all users")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserDto>> getAllUsers() {
//        List<UserDto> users = userRepository.findAll().stream()
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/{id}")
//    @Operation(summary = "Get user by ID")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> getUserById(
//            @Parameter(description = "User ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id) {
//        Optional<User> user = userRepository.findById(id);
//        if (user.isPresent()) {
//            return ResponseEntity.ok(convertToDto(user.get()));
//        }
//        return ResponseEntity.notFound().build();
//    }
//
//    @PutMapping("/{id}/role")
//    @Operation(summary = "Assign role to user")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> assignRoleToUser(
//            @Parameter(description = "User ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id,
//            @Valid @RequestBody UserRoleDto userRoleDto) {
//        Optional<User> userOpt = userRepository.findById(id);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        Optional<Role> roleOpt = roleRepository.findById(userRoleDto.getRoleId());
//        if (roleOpt.isEmpty()) {
//            return ResponseEntity.badRequest().build();
//        }
//
//        User user = userOpt.get();
//        user.setRole(roleOpt.get());
//        User savedUser = userRepository.save(user);
//
//        return ResponseEntity.ok(convertToDto(savedUser));
//    }
//
//    @PutMapping("/{id}/status")
//    @Operation(summary = "Update user status (activate/deactivate)")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<UserDto> updateUserStatus(
//            @Parameter(description = "User ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id,
//            @Parameter(description = "User status", required = true, example = "true", schema = @Schema(type = "boolean"))
//            @RequestParam boolean status) {
//        Optional<User> userOpt = userRepository.findById(id);
//        if (userOpt.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        User user = userOpt.get();
//        user.setStatus(status);
//        User savedUser = userRepository.save(user);
//
//        return ResponseEntity.ok(convertToDto(savedUser));
//    }
//
//    @DeleteMapping("/{id}")
//    @Operation(summary = "Delete user by ID")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Void> deleteUser(
//            @Parameter(description = "User ID", required = true, example = "1", schema = @Schema(type = "integer", format = "int64"))
//            @PathVariable Long id) {
//        Optional<User> user = userRepository.findById(id);
//        if (user.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//
//        userRepository.deleteById(id);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/by-role/{roleName}")
//    @Operation(summary = "Get users by role name")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<List<UserDto>> getUsersByRole(
//            @Parameter(description = "Role name", required = true, example = "ADMIN", schema = @Schema(type = "string"))
//            @PathVariable String roleName) {
//        List<UserDto> users = userRepository.findAll().stream()
//                .filter(user -> user.getRole() != null && roleName.equals(user.getRole().getName()))
//                .map(this::convertToDto)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(users);
//    }
//
//    private UserDto convertToDto(User user) {
//        return new UserDto(
//                user.getId(),
//                user.getFullName(),
//                user.getEmail(),
//                user.getRole() != null ? user.getRole().getName() : null,
//                user.getStudentId(),
//                user.getFaculty(),
//                user.isStatus(),
//                user.getCreatedAt()
//        );
//    }
//}

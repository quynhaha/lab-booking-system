package com.unilab.api;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.unilab.model.Role;
import com.unilab.model.User;
import com.unilab.repository.RoleRepository;
import com.unilab.repository.UserRepository;
import com.unilab.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

record RegisterRequest(@Email String email, @NotBlank String password, @NotBlank String fullName, String studentId, String faculty) {}
record TokenResponse(String token) {}
record GoogleLoginRequest(@NotBlank String idToken) {}
record FptSsoLoginRequest(@Email String email, @NotBlank String password) {}
record LoginVerificationResponse(String token, String loginMethod, String userType, String message) {}
record UserResponse(Long id, String fullName, String email, String role, String studentId, String faculty, boolean status) {}

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final List<String> googleClientIds;

    public AuthController(UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          JwtService jwtService,
                          @Value("${app.google.clientId:}") String googleClientId,
                          @Value("${app.google.androidClientId:}") String androidClientId) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        
        // Support multiple Client IDs (Web and Android)
        this.googleClientIds = new ArrayList<>();
        if (googleClientId != null && !googleClientId.trim().isEmpty()) {
            this.googleClientIds.add(googleClientId.trim());
        }
        if (androidClientId != null && !androidClientId.trim().isEmpty()) {
            this.googleClientIds.add(androidClientId.trim());
        }
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user account (FPT University only)")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest request) {
        // Validate FPT email domain
        if (!request.email().endsWith("@fpt.edu.vn")) {
            return ResponseEntity.status(400).body("Only @fpt.edu.vn emails are allowed for registration");
        }
        
        // Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(409).body("Email already exists");
        }
        
        // Create new user with STUDENT role by default for FPT emails
        User user = new User();
        user.setEmail(request.email());
        user.setFullName(request.fullName());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setStudentId(request.studentId());
        user.setFaculty(request.faculty());
        user.setStatus(true);
        
        // Assign STUDENT role for FPT University emails
        Role studentRole = roleRepository.findByName("STUDENT").orElseGet(() -> {
            Role role = new Role();
            role.setName("STUDENT");
            return roleRepository.save(role);
        });
        user.setRole(studentRole);
        
        User savedUser = userRepository.save(user);
        
        // Generate token for immediate login
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", savedUser.getRole().getName());
        claims.put("loginMethod", "FPT_REGISTRATION");
        String token = jwtService.generateToken(savedUser.getEmail(), claims);
        
        return ResponseEntity.status(201).body(new LoginVerificationResponse(
            token, "FPT_REGISTRATION", "FPT_STUDENT", "Registration successful"
        ));
    }


    @PostMapping("/login/fpt-sso")
    @Operation(summary = "Login with FPT University SSO (@fpt.edu.vn)")
    public ResponseEntity<?> loginWithFptSso(@RequestBody FptSsoLoginRequest request) {
        // Validate FPT email domain
        if (!(request.email().endsWith("@fpt.edu.vn") || request.email().endsWith("@unilab.local"))) {
            return ResponseEntity.status(400).body("Only @fpt.edu.vn and @unilab.local emails are allowed for FPT SSO");
        }
        
        return performLogin(request.email(), request.password(), "FPT_SSO");
    }

    private ResponseEntity<?> performLogin(String email, String password, String loginMethod) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(401).body(new LoginVerificationResponse(
                null, loginMethod, "UNKNOWN", "Invalid credentials"
            ));
        }
        
        User user = userOpt.get();
        if (!passwordEncoder.matches(password, user.getPasswordHash())) {
            return ResponseEntity.status(401).body(new LoginVerificationResponse(
                null, loginMethod, "INVALID_PASSWORD", "Invalid credentials"
            ));
        }
        
        if (!user.isStatus()) {
            return ResponseEntity.status(403).body(new LoginVerificationResponse(
                null, loginMethod, "ACCOUNT_DISABLED", "Account is disabled"
            ));
        }
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().getName());
        claims.put("loginMethod", loginMethod);
        claims.put("nameIdentifier", user.getId());
        String token = jwtService.generateToken(user.getEmail(), claims);
        
        String userType = determineUserType(user);
        String message = String.format("Login successful via %s", loginMethod);
        
        return ResponseEntity.ok(new LoginVerificationResponse(
            token, loginMethod, userType, message
        ));
    }

    private String determineUserType(User user) {
        if (user.getEmail().endsWith("@fpt.edu.vn")) {
            return "FPT_STUDENT";
        } else if (user.getEmail().endsWith("@gmail.com")) {
            return "GOOGLE_USER";
        } else {
            return "REGULAR_USER";
        }
    }

    @PostMapping("/google")
    @Operation(summary = "Login with Google ID token")
    public ResponseEntity<?> googleLogin(@RequestBody GoogleLoginRequest request) {
        try {
            logger.info("Google login attempt - Client IDs configured: {}", googleClientIds.size());
            
            if (googleClientIds.isEmpty()) {
                logger.error("Google Client IDs not configured");
                return ResponseEntity.status(500).body(new LoginVerificationResponse(
                    null, "GOOGLE_SSO", "CONFIG_ERROR", "Google Client ID not configured"
                ));
            }

            // Try to verify with any of the configured Client IDs
            GoogleIdToken idToken = null;
            Exception lastException = null;
            
            for (String clientId : googleClientIds) {
                try {
                    logger.debug("Attempting to verify token with Client ID: {}", clientId.substring(0, 20) + "...");
                    
                    GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                            GoogleNetHttpTransport.newTrustedTransport(),
                            GsonFactory.getDefaultInstance())
                            .setAudience(java.util.Collections.singletonList(clientId))
                            .build();
                    
                    idToken = verifier.verify(request.idToken());
                    if (idToken != null) {
                        logger.info("Token verified successfully with Client ID: {}", clientId.substring(0, 20) + "...");
                        break; // Successfully verified
                    }
                } catch (Exception e) {
                    logger.warn("Failed to verify with Client ID {}: {}", clientId.substring(0, 20) + "...", e.getMessage());
                    lastException = e;
                    // Try next Client ID
                    continue;
                }
            }

            if (idToken == null) {
                logger.error("Token verification failed for all Client IDs. Last error: {}", 
                    lastException != null ? lastException.getMessage() : "Unknown");
                return ResponseEntity.status(401).body(new LoginVerificationResponse(
                    null, "GOOGLE_SSO", "INVALID_TOKEN", 
                    "Invalid Google token: " + (lastException != null ? lastException.getMessage() : "Verification failed")
                ));
            }
            
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String fullName = (String) payload.get("name");
            
            logger.info("Google login successful for email: {}", email);

            // Check if user exists
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                logger.info("Creating new user for Google login: {}", email);
                User u = new User();
                u.setEmail(email);
                u.setFullName(fullName != null ? fullName : email);
                u.setPasswordHash(passwordEncoder.encode("google_login_placeholder"));
                u.setStatus(true);
                
                // Assign role based on email domain
                Role role;
                if (email.endsWith("@fpt.edu.vn")) {
                    role = roleRepository.findByName("STUDENT").orElseGet(() -> {
                        Role r = new Role();
                        r.setName("STUDENT");
                        return roleRepository.save(r);
                    });
                } else {
                    role = roleRepository.findByName("USER").orElseGet(() -> {
                        Role r = new Role();
                        r.setName("USER");
                        return roleRepository.save(r);
                    });
                }
                u.setRole(role);
                return userRepository.save(u);
            });

            if (!user.isStatus()) {
                logger.warn("Account disabled for email: {}", email);
                return ResponseEntity.status(403).body(new LoginVerificationResponse(
                    null, "GOOGLE_SSO", "ACCOUNT_DISABLED", "Account is disabled"
                ));
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("role", user.getRole().getName());
            claims.put("loginMethod", "GOOGLE_SSO");
            claims.put("nameIdentifier", user.getId());
            String token = jwtService.generateToken(user.getEmail(), claims);
            
            String userType = determineUserType(user);
            String message = "Google login successful";
            
            logger.info("Google login completed successfully for user: {}", email);
            
            return ResponseEntity.ok(new LoginVerificationResponse(
                token, "GOOGLE_SSO", userType, message
            ));
        } catch (Exception e) {
            logger.error("Unexpected error during Google login", e);
            return ResponseEntity.status(500).body(new LoginVerificationResponse(
                null, "GOOGLE_SSO", "SERVER_ERROR", 
                "Internal server error: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout (client should discard JWT)")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @Operation(summary = "Get current authenticated user info")
    public ResponseEntity<?> me(@RequestHeader(name = "Authorization", required = false) String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }
        String token = authHeader.substring(7);
        String email;
        try {
            email = jwtService.validateAndParse(token).getSubject();
        } catch (Exception e) {
            return ResponseEntity.status(401).build();
        }
        return userRepository.findByEmail(email)
                .map(u -> new UserResponse(
                        u.getId(),
                        u.getFullName(),
                        u.getEmail(),
                        u.getRole() != null ? u.getRole().getName() : null,
                        u.getStudentId(),
                        u.getFaculty(),
                        u.isStatus()
                ))
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(404).build());
    }
}



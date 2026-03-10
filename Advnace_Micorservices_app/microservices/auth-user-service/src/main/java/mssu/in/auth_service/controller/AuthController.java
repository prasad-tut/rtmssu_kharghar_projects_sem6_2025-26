package mssu.in.auth_service.controller;

import mssu.in.auth_service.dto.*;
import mssu.in.auth_service.entity.User;
import mssu.in.auth_service.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthenticationService authenticationService;

    public AuthController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        LoginResponse response = authenticationService.login(loginRequest);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signup")
    public ResponseEntity<UserResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        User user = authenticationService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody SignupRequest signupRequest) {
        User user = authenticationService.signup(signupRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(new UserResponse(user));
    }

    @PostMapping("/validate-token")
    public ResponseEntity<Map<String, Object>> validateToken(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        if (token == null || token.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "valid", false,
                    "message", "Token is required"));
        }

        boolean isValid = authenticationService.validateToken(token);
        if (isValid) {
            User user = authenticationService.getUserFromToken(token);
            return ResponseEntity.ok(Map.of(
                    "valid", true,
                    "userId", user.getId(),
                    "email", user.getEmail(),
                    "name", user.getName(),
                    "role", user.getRole().name()));
        } else {
            return ResponseEntity.ok(Map.of(
                    "valid", false,
                    "message", "Invalid or expired token"));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam String email) {
        authenticationService.logout(email);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "UP",
                "service", "auth-user-service"));
    }
}

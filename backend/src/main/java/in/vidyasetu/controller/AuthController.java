package in.vidyasetu.controller;

import in.vidyasetu.dto.request.LoginRequest;
import in.vidyasetu.dto.request.RegisterRequest;
import in.vidyasetu.dto.response.ApiResponse;
import in.vidyasetu.dto.response.AuthResponse;
import in.vidyasetu.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // POST /v1/auth/register
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @Valid @RequestBody RegisterRequest req,
            HttpServletResponse response
    ) {
        AuthResponse data = authService.register(req, response);
        return ok(data, "School registered successfully");
    }

    // POST /v1/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @Valid @RequestBody LoginRequest req,
            HttpServletResponse response
    ) {
        AuthResponse data = authService.login(req, response);
        return ok(data, "Login successful");
    }

    // POST /v1/auth/refresh
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AuthResponse data = authService.refresh(request, response);
        return ok(data, "Token refreshed");
    }

    // POST /v1/auth/logout
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletResponse response) {
        authService.logout(response);
        return ok(null, "Logged out successfully");
    }

    // GET /v1/auth/me
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        AuthResponse data = authService.me(userId);
        return ok(data, "User fetched successfully");
    }

    // ── Standard response wrapper ─────────────────────────────────────────────
    private ResponseEntity<?> ok(Object data, String message) {
        return ResponseEntity.ok(ApiResponse.ok(data, message));
    }
}

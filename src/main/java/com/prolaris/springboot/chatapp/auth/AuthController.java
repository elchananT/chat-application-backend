package com.prolaris.springboot.chatapp.auth;

import com.prolaris.springboot.chatapp.auth.dto.AuthResponse;
import com.prolaris.springboot.chatapp.auth.dto.LoginRequest;
import com.prolaris.springboot.chatapp.auth.dto.RegisterRequest;
import com.prolaris.springboot.chatapp.auth.dto.UserResponse;
import com.prolaris.springboot.chatapp.auth.users.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal UserDetails principal) {
        if (principal instanceof User user) {
            return ResponseEntity.ok(new UserResponse(user.getId(), user.getUsername(), user.getEmail()));
        }
        return ResponseEntity.ok(new UserResponse(null, principal.getUsername(), null));
    }
}

package com.prolaris.springboot.chatapp.auth;

import com.prolaris.springboot.chatapp.auth.dto.AuthResponse;
import com.prolaris.springboot.chatapp.auth.dto.LoginRequest;
import com.prolaris.springboot.chatapp.auth.dto.RegisterRequest;
import com.prolaris.springboot.chatapp.auth.exceptions.InvalidCredentialsException;
import com.prolaris.springboot.chatapp.auth.exceptions.UserAlreadyExistException;
import com.prolaris.springboot.chatapp.auth.security.jwt.JwtService;
import com.prolaris.springboot.chatapp.auth.users.User;
import com.prolaris.springboot.chatapp.auth.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistException("Email is already in use");
        }
        if (userRepository.existsByUsername(request.username())) {
            throw new UserAlreadyExistException("Username is already in use");
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        User saved = userRepository.save(user);
        String token = jwtService.generateToken(saved);

        return new AuthResponse(saved.getUsername(), saved.getEmail(), token);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid Credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid Credentials");
        }

        String token = jwtService.generateToken(user);
        return new AuthResponse(user.getUsername(), user.getEmail(), token);
    }
}

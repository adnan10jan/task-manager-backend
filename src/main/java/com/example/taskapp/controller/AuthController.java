package com.example.taskapp.controller;

import com.example.taskapp.dto.*;
import com.example.taskapp.exception.ApiException;
import com.example.taskapp.model.RefreshToken;
import com.example.taskapp.model.User;
import com.example.taskapp.repository.RefreshTokenRepository;
import com.example.taskapp.repository.UserRepository;
import com.example.taskapp.security.JwtUtil;
import com.example.taskapp.service.RefreshTokenService;
import com.example.taskapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenRepository refreshTokenRepository;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserService userService,
            UserRepository userRepository,
            RefreshTokenService refreshTokenService,
            RefreshTokenRepository refreshTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    // ✅ SIGNUP
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Void>> signup(@Valid @RequestBody SignupRequest req) {
        userService.register(req.username(), req.email(), req.password());
        return ResponseEntity.ok(
                new ApiResponse<>(true, "User registered successfully", null)
        );
    }

    // ✅ LOGIN
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest req) {

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            req.username(),
                            req.password()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Invalid username or password");
        }

        User user = userRepository.findByUsername(req.username())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        AuthResponse response =
                new AuthResponse(accessToken, refreshToken, user.getUsername());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Login successful", response)
        );
    }

    // ✅ REFRESH TOKEN
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<Map<String, String>>> refreshToken(
            @RequestBody RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new IllegalArgumentException("Invalid refresh token"));

        String newAccessToken =
                jwtUtil.generateToken(refreshToken.getUser().getUsername());

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Token refreshed",
                        Map.of("accessToken", newAccessToken))
        );
    }

    // ✅ LOGOUT
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        refreshTokenService.deleteByUser(user);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Logged out successfully", null)
        );
    }
}

package com.example.taskapp.controller;

import com.example.taskapp.dto.AuthRequest;
import com.example.taskapp.dto.AuthResponse;
import com.example.taskapp.dto.SignupRequest;
import com.example.taskapp.dto.RefreshRequest;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        userService.register(req.username(), req.email(), req.password());
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(),
                        req.password()
                )
        );

        User user = userRepository.findByUsername(req.username()).orElseThrow();

        String accessToken = jwtUtil.generateToken(user.getUsername());
        String refreshToken = refreshTokenService.createRefreshToken(user).getToken();

        return ResponseEntity.ok(
                new AuthResponse(accessToken, refreshToken, user.getUsername())
        );
    }



    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshRequest request) {

        RefreshToken refreshToken = refreshTokenRepository.findByToken(request.refreshToken())
                .map(refreshTokenService::verifyExpiration)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        String newAccessToken =
                jwtUtil.generateToken(refreshToken.getUser().getUsername());

        return ResponseEntity.ok(Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal UserDetails userDetails) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow();

        refreshTokenService.deleteByUser(user);

        return ResponseEntity.ok("Logged out successfully");
    }
}

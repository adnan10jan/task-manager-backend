package com.example.taskapp.controller;

import com.example.taskapp.dto.AuthRequest;
import com.example.taskapp.dto.AuthResponse;
import com.example.taskapp.dto.SignupRequest;
import com.example.taskapp.model.User;
import com.example.taskapp.security.JwtUtil;
import com.example.taskapp.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtUtil jwtUtil,
            UserService userService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequest req) {
        User user = userService.register(
                req.username(),
                req.email(),
                req.password()
        );
        return ResponseEntity.ok("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest req) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.username(),
                        req.password()
                )
        );

        String token = jwtUtil.generateToken(req.username());
        return ResponseEntity.ok(new AuthResponse(token, req.username()));
    }
}

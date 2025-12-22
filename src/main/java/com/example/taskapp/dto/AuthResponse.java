package com.example.taskapp.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String username
) {}

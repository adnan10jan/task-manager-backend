package com.example.taskapp.dto;

import jakarta.validation.constraints.*;

public record SignupRequest(

        @NotBlank(message = "Username is required")
        @Size(min = 3, max = 20)
        String username,

        @Email(message = "Invalid email")
        @NotBlank
        String email,

        @NotBlank
        @Size(min = 6, message = "Password must be at least 6 chars")
        String password
) {}

package com.example.taskapp.dto;

public record ApiResponse<T>(
        boolean success,
        String message,
        T data
) {}

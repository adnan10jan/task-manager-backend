package com.example.taskapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskDto(
        Long id,

        @NotBlank(message = "Title is required")
        String title,

        String description,

        @NotNull
        String status,

        @NotNull
        String priority,

        LocalDateTime dueDate
) {}

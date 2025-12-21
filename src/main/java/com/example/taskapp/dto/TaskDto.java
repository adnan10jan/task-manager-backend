package com.example.taskapp.dto;

import java.time.LocalDateTime;

public record TaskDto(
        Long id,
        String title,
        String description,
        String status,
        String priority,
        LocalDateTime dueDate,
        String ownerUsername
) {}

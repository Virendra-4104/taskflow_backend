package com.taskflow.taskflow_backend.dto.response;

import lombok.Builder;

import java.time.Instant;

@Builder
public record ProjectResponse(
        Long id,
        String title,
        String description,
        String createdBy,
        Instant createdAt,
        Instant updatedAt
) {
}

package com.taskflow.taskflow_backend.dto.response;

import java.time.Instant;

import lombok.Builder;

@Builder 
public record CommentResponse(
    Long id,
    String content,
    Long taskId,
    String createdByUsername,
    String createdByEmail,
    Instant createdAt
) {
}

package com.taskflow.taskflow_backend.dto.response;

import java.time.Instant;

import lombok.Builder;

@Builder 
public record ErrorResponse(
    String status,
    String message,
    Instant timeStamp
) {
}

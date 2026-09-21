package com.taskflow.taskflow_backend.dto.response;

import java.time.Instant;
import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import lombok.Builder;

@Builder 
public record PersonalTaskResponse(
    Long id,
    String title,
    String description,
    String createdBy,
    Status status,
    Priority priority,
    LocalDate dueDate,
    Instant createdAt,
    Instant updatedAt
) {
}

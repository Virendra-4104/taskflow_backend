package com.taskflow.taskflow_backend.dto.response;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record ProjectTaskResponse(
        Long id,
        String title,
        String description,
        Status status,
        Priority priority,
        String createdByUsername,
        String createdByEmail,
        Long projectId,
        String assignedToEmail,
        LocalDate dueDate
) {
}

package com.taskflow.taskflow_backend.dto.request;

import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

public record UpdateProjectTaskRequest(
    String title,
    String description,
    Status status,
    Priority priority,
    Long projectId,
    String assignedUserEmail,
    LocalDate dueDate
) {
}

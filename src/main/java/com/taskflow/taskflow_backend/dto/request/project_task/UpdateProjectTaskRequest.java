package com.taskflow.taskflow_backend.dto.request.project_task;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import java.time.LocalDate;

public record UpdateProjectTaskRequest(
        String title,
        String description,
        Status status,
        Priority priority,
        LocalDate dueDate
) {
}

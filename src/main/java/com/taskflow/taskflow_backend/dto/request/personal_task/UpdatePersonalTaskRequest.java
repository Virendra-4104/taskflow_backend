package com.taskflow.taskflow_backend.dto.request.personal_task;

import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

public record UpdatePersonalTaskRequest(
    String title,
    String description,
    Status status,
    Priority priority,
    LocalDate dueDate
) {
}

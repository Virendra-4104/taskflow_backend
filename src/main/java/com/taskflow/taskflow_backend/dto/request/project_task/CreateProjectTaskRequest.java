package com.taskflow.taskflow_backend.dto.request.project_task;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record CreateProjectTaskRequest(
        @Size(min = 3, max = 150, message = "Title must be in between 3 to 150")
        @NotBlank(message = "Title is required.")
        String title,

        String description,

        @NotNull(message = "Status is require.")
        Status status,

        @NotNull(message = "Priority is required")
        Priority priority,

        @Email(message = "Email must be valid.")
        @NotBlank(message = "Member email is required.")
        String assignedToEmail,

        @NotNull(message = "Due date is required.")
        LocalDate dueDate
) {
}

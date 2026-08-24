package com.taskflow.taskflow_backend.dto.request.project_task;

import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateProjectTaskRequest(
    
    @NotBlank(message = "Title is required.")
    @Size(min = 2, message = "Title must be in between 2 to 150 characters.")
    String title,

    String description,
    
    @NotNull(message = "Status is required.")
    Status status,

    @NotNull(message = "Priority is required.")
    Priority priority,
    
    @NotNull(message = "Project id is required.")
    Long projectId,
    
    @NotBlank(message = "Project member email  is required.")
    @Email(message = "Email must be valid.")
    String assignedUserEmail,

    @NotNull(message = "Due Date is required.")
    LocalDate dueDate


) {
}

package com.taskflow.taskflow_backend.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
    
    @NotBlank(message = "Title is required.")
    @Size(min = 3, max = 50, message = "Title must be between 3 to 50 characters")
    String title,

    String description
) {
}

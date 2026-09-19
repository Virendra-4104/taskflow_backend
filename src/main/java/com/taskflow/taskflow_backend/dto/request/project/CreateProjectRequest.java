package com.taskflow.taskflow_backend.dto.request.project;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateProjectRequest(
        @Size(min = 3, max = 150, message = "Title must be between 3 to 150 characters")
        @NotBlank(message = "title is required")
        String title,

        String description
) {
}

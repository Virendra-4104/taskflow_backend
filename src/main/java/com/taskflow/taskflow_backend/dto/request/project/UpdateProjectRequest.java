package com.taskflow.taskflow_backend.dto.request.project;

public record UpdateProjectRequest(
    String title,
    String description
) {
}

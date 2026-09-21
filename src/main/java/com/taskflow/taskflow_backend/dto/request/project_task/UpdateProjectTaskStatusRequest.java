package com.taskflow.taskflow_backend.dto.request.project_task;

import com.taskflow.taskflow_backend.enums.Status;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder 
public record UpdateProjectTaskStatusRequest(

    @NotNull(message = "Status is required")
    Status status
) {
}

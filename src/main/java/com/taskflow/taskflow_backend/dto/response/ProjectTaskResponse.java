package com.taskflow.taskflow_backend.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ProjectTaskResponse {
    private Long id;
    private String title;
    private String description;
    private String createdByEmail;
    private Status status;
    private Priority priority;
    private Long projectId;
    private String assignedUserEmail;
    private LocalDate dueDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

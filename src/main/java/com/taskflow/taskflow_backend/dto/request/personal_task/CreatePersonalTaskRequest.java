package com.taskflow.taskflow_backend.dto.request.personal_task;


import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder 
public record CreatePersonalTaskRequest(

    @Size(min = 3, max = 150,message = "Title must be in between 3 to 150 characters.")
    @NotBlank(message = "Title is required.")
    String title,
    
    String description,

    @NotNull(message = "Status is required.")
    Status status,

    @NotNull(message = "Priority is required.")
    Priority priority,

    @NotNull(message = "Due date is required.")
    LocalDate dueDate
) {
}

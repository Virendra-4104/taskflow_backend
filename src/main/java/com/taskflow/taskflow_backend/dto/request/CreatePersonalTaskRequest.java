package com.taskflow.taskflow_backend.dto.request;

import java.time.LocalDate;

import com.taskflow.taskflow_backend.enums.Priority;
import com.taskflow.taskflow_backend.enums.Status;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreatePersonalTaskRequest(

    @NotBlank(message = "Title is required.")
    @Size(min = 2, message = "Title must be in between 2 to 150 characters.")
    String title,

    String description,
    
    @NotNull(message = "Status is required.")
    Status status,

    @NotNull(message = "Priority is required.")
    Priority priority,

    @NotNull(message = "Due Date is required.")
    LocalDate dueDate
){
}

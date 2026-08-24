package com.taskflow.taskflow_backend.dto.request;

import com.taskflow.taskflow_backend.enums.Status;

public record UpdateProjectTaskStatusByAssignedUserRequest(
    Status status
){
}

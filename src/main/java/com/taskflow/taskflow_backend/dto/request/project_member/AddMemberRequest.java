package com.taskflow.taskflow_backend.dto.request.project_member;

import com.taskflow.taskflow_backend.enums.ProjectRole;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder 
public record AddMemberRequest(
        @NotBlank(message = "Member email is required.")
        @Email(message = "Email must be valid.")
        String memberEmail,

        @NotNull(message = "Project is required.")
        Long projectId,

        @NotNull(message = "Member role is required.")
        ProjectRole role
) {
}

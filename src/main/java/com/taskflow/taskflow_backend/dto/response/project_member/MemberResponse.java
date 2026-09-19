package com.taskflow.taskflow_backend.dto.response.project_member;

import lombok.Builder;

@Builder 
public record MemberResponse(
    String username,
    String email
) {
}

package com.taskflow.taskflow_backend.dto.response;

import lombok.Builder;

@Builder
public record AuthResponse(
        String jwtToken,
        UserResponse userResponse
) {
}

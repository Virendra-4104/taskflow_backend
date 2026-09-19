package com.taskflow.taskflow_backend.dto.response;

import com.taskflow.taskflow_backend.enums.Gender;
import lombok.Builder;

import java.time.Instant;

@Builder
public record UserResponse(
        String username,
        String email,
        String profileImgUrl,
        Gender gender,
        Instant createdAt
) {
}

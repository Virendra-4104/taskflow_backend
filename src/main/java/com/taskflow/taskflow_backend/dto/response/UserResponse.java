package com.taskflow.taskflow_backend.dto.response;

import java.time.LocalDateTime;

import com.taskflow.taskflow_backend.enums.Gender;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserResponse {
    private String username;
    private String email;
    private Gender gender;
    private String profileImgUrl;
    private LocalDateTime createdAt;
}

package com.taskflow.taskflow_backend.dto.request.user_auth;

import com.taskflow.taskflow_backend.enums.Gender;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be valid.")
    String email,

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "username must be between 3 to 50 characters.")
    String username,

    @NotBlank(message = "Password is required.")
    @Size(min = 6, message = "Password is must be at least 6 characters.")
    String password,

    @NotNull(message = "Gender id required.")
    Gender gender
) {
}

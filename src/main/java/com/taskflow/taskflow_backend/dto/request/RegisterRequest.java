package com.taskflow.taskflow_backend.dto.request;

import com.taskflow.taskflow_backend.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @Size(min = 3, max = 50)
        @NotBlank(message = "Username is required.")
        String username,

        @Size(max = 150)
        @NotBlank(message = "Email is required.")
        @Email(message = "Email must be valid")
        String email,

        @Size(min = 6,max = 255)
        @NotBlank(message = "Password is required")
        String password,

        @NotNull(message = "Gender is required.")
        Gender gender
) {
}

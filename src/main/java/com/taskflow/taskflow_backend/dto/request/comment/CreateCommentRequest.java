package com.taskflow.taskflow_backend.dto.request.comment;

import jakarta.validation.constraints.NotBlank;

public record CreateCommentRequest(
    
    @NotBlank(message = "Content is required.")
    String content
) {
}

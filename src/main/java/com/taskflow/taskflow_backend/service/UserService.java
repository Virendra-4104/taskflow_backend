package com.taskflow.taskflow_backend.service;

import com.taskflow.taskflow_backend.dto.request.user_auth.LoginRequest;
import com.taskflow.taskflow_backend.dto.request.user_auth.RegisterRequest;
import com.taskflow.taskflow_backend.dto.response.AuthResponse;

public interface UserService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
}

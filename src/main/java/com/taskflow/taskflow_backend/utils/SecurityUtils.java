package com.taskflow.taskflow_backend.utils;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.security.CustomUserDetails;

@Component
public class SecurityUtils {
    public User getCurrentUser(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated())
            throw new RuntimeException("User not authenticated");

        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}

package com.taskflow.taskflow_backend.utils;

import com.taskflow.taskflow_backend.entity.User;
import com.taskflow.taskflow_backend.exception.UserNotAuthenticatedException;
import com.taskflow.taskflow_backend.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public User getCurrent(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if(auth == null || !auth.isAuthenticated()){
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getUser();
    }
}

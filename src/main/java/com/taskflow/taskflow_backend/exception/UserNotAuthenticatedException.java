package com.taskflow.taskflow_backend.exception;

public class UserNotAuthenticatedException extends RuntimeException{
    public UserNotAuthenticatedException(String message) {
        super(message);
    }
}

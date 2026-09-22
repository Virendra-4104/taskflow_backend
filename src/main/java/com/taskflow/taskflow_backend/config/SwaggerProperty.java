package com.taskflow.taskflow_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@ConfigurationProperties ("app.swagger")
@Getter 
@Setter 
public class SwaggerProperty {
    private String name;
    private String email;
}

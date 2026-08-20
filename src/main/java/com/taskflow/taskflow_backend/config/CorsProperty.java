package com.taskflow.taskflow_backend.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.cors")
public class CorsProperty {
    private List<String> allowedOrigins;
}

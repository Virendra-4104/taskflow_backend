package com.taskflow.taskflow_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Getter
@Setter
@ConfigurationProperties("app.security.cors")
public class CorsProperty {
    private List<String> allowedOrigins;
}

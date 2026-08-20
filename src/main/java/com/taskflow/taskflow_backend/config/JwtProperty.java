package com.taskflow.taskflow_backend.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperty {
    private String secretKey;
    private Long expiration;
}

package com.taskflow.taskflow_backend.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "app.security.jwt")
public class JwtProperty {
    private String secretKey;
    private Long expiration;
}

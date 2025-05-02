package com.truskappka.truskappka_backend.config.google;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "google")
@Getter
@Setter
public class GoogleProperties {
    private String clientId;
}
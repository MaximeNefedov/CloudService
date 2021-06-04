package ru.netology.cloud_service_app.sercurity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Configuration
@ConfigurationProperties(prefix = "application.security")
public class SecurityConfigProperties {
    private String allowedOrigins;
}

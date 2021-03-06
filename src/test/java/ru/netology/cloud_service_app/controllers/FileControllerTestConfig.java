package ru.netology.cloud_service_app.controllers;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import ru.netology.cloud_service_app.repositories.UserRepository;
import ru.netology.cloud_service_app.security.CustomUserDetailsService;

import static org.mockito.Mockito.mock;

@TestConfiguration
@ComponentScan({"ru.netology.cloud_service_app.configs",
        "ru.netology.cloud_service_app.security",
        "ru.netology.cloud_service_app.configs"})
public class FileControllerTestConfig {

    @Bean
    public CustomUserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository());
    }

    @Bean
    public UserRepository userRepository() {
        return mock(UserRepository.class);
    }
}

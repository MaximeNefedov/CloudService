package ru.netology.cloud_service_app.security.jwt;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UsernamePasswordAuthenticationRequest {
    private String login;
    private String password;
}

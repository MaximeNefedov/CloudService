package ru.netology.cloud_service_app.exceptions;

import org.springframework.security.core.AuthenticationException;

public class InvalidUserDetailsException extends AuthenticationException {
    public InvalidUserDetailsException(String message) {
        super(message);
    }
}

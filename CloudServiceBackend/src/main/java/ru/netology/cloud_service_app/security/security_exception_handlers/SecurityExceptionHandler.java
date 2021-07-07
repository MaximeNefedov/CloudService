package ru.netology.cloud_service_app.security.security_exception_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.netology.cloud_service_app.models.ApiResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class SecurityExceptionHandler implements AuthenticationEntryPoint {

    private final String MESSAGE = "Authentication error";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException e) throws IOException, ServletException {
        val forbiddenStatus = HttpStatus.FORBIDDEN.value();
        response.setStatus(forbiddenStatus);
        val exceptionResponse = new ApiResponse(MESSAGE, forbiddenStatus);
        new ObjectMapper().writeValue(response.getOutputStream(), exceptionResponse);
    }
}

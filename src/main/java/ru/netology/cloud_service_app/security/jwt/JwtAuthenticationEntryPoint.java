package ru.netology.cloud_service_app.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import ru.netology.cloud_service_app.models.ApiResponse;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException e) throws IOException, ServletException {
        log.error(e.getMessage());
        val forbiddenStatus = HttpStatus.FORBIDDEN.value();
        response.setStatus(forbiddenStatus);
        val exceptionResponse = new ApiResponse(e.getMessage(), forbiddenStatus);
        new ObjectMapper().writeValue(response.getOutputStream(), exceptionResponse);
    }
}

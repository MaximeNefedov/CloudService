package ru.netology.cloud_service_app.security.success_response_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import ru.netology.cloud_service_app.filters.CustomFilterWrapper;
import ru.netology.cloud_service_app.models.ApiResponse;
import ru.netology.cloud_service_app.security.jwt.JwtConfig;
import ru.netology.cloud_service_app.security.jwt.JwtTokenHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomLogoutSuccessHandler extends
        SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    private final JwtConfig jwtConfig;
    public static final String SUCCESS_LOGOUT_MESSAGE = "Success logout";



    public CustomLogoutSuccessHandler(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {

        val authorizationHeaderName = jwtConfig.getAuthorizationHeader();
        val authorizationHeader = request.getHeader(authorizationHeaderName);
        val tokenPrefix = jwtConfig.getTokenPrefix();
        JwtTokenHandler.handleJwtTokenAfterLogout(authorizationHeader, tokenPrefix);

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                new ApiResponse(SUCCESS_LOGOUT_MESSAGE, HttpStatus.OK.value())
        );
    }
}

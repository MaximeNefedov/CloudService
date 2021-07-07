package ru.netology.cloud_service_app.security.success_response_handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import ru.netology.cloud_service_app.models.ApiResponse;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomLogoutSuccessHandler extends
        SimpleUrlLogoutSuccessHandler implements LogoutSuccessHandler {
    public static final String SUCCESS_LOGOUT_MESSAGE = "Success logout";
    @Override
    public void onLogoutSuccess(HttpServletRequest request,
                                HttpServletResponse response,
                                Authentication authentication) throws IOException {

        new ObjectMapper().writeValue(
                response.getOutputStream(),
                new ApiResponse(SUCCESS_LOGOUT_MESSAGE, HttpStatus.OK.value())
        );
    }
}

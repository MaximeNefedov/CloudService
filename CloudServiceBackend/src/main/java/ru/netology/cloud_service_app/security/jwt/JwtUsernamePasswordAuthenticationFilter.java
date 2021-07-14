package ru.netology.cloud_service_app.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.netology.cloud_service_app.security.security_exception_handlers.SecurityExceptionHandler;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtConfig jwtConfig;
    private final SecurityExceptionHandler securityExceptionHandler;

    public JwtUsernamePasswordAuthenticationFilter(AuthenticationManager authenticationManager,
                                                   JwtConfig jwtConfig,
                                                   SecurityExceptionHandler securityExceptionHandler) {
        this.authenticationManager = authenticationManager;
        this.jwtConfig = jwtConfig;
        this.securityExceptionHandler = securityExceptionHandler;
    }

    @SneakyThrows
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request,
                                                HttpServletResponse response) throws AuthenticationException {
        Authentication authenticationManagerResult = null;
        try {
            val authenticationRequest = new ObjectMapper()
                    .readValue(request.getInputStream(), UsernamePasswordAuthenticationRequest.class);

            val authentication = new UsernamePasswordAuthenticationToken(
                    authenticationRequest.getLogin(),
                    authenticationRequest.getPassword()
            );
            authenticationManagerResult = authenticationManager.authenticate(authentication);
        } catch (AuthenticationException e) {
            securityExceptionHandler.commence(request, response, e);
        }
        return authenticationManagerResult;
    }

    @Override
    public void successfulAuthentication(HttpServletRequest request,
                                         HttpServletResponse response,
                                         FilterChain chain,
                                         Authentication authResult) throws IOException {
        val token = JwtTokenHandler.singJwtToken(authResult.getName(),
                jwtConfig.getSecretKey(),
                authResult.getAuthorities(),
                jwtConfig.getTokenExpirationDays());
        val jwtToken = new JwtToken(token);
        new ObjectMapper().writeValue(response.getOutputStream(), jwtToken);
    }
}

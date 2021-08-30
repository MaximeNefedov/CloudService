package ru.netology.cloud_service_app.security.jwt;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloud_service_app.filters.CustomFilterWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class JwtTokenVerifier extends OncePerRequestFilter {
    private final JwtConfig jwtConfig;

    public JwtTokenVerifier(JwtConfig jwtConfig) {
        this.jwtConfig = jwtConfig;
    }


    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        val authorizationHeaderName = jwtConfig.getAuthorizationHeader();

        val filterWrapper = new CustomFilterWrapper(request);
        val authorizationHeader = filterWrapper.getHeader(authorizationHeaderName);
        val tokenPrefix = jwtConfig.getTokenPrefix();
        val secretKey = jwtConfig.getSecretKey();

        if (!JwtTokenHandler.checkRequestHeader(authorizationHeader, tokenPrefix)) {
            log.error("Authorization header is invalid");
            return;
        }
        JwtTokenHandler.verifyJwtToken(authorizationHeader, tokenPrefix, secretKey);
        chain.doFilter(filterWrapper, response);
    }
}

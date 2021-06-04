package ru.netology.cloud_service_app.jwt;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import lombok.SneakyThrows;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.cloud_service_app.filters.CustomFilterWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

        if (authorizationHeader == null
                || authorizationHeader.isEmpty()
                || !authorizationHeader.startsWith(tokenPrefix)) {
            chain.doFilter(request, response);
            return;
        }

        try {
            val token = authorizationHeader.replaceAll(tokenPrefix, "");
            val claims = Jwts.parserBuilder()
                    .setSigningKey(jwtConfig.getSecretKey())
                    .build()
                    .parseClaimsJws(token);
            val body = claims.getBody();
            val username = body.getSubject();
            val authoritiesAsList = (List<Map<String, String>>) body.get("authorities");

            val authorities = authoritiesAsList.stream()
                    .map(value -> new SimpleGrantedAuthority(value.get("authority")))
                    .collect(Collectors.toSet());

            val authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException e) {
            throw new IllegalStateException(e);
        }

        chain.doFilter(filterWrapper, response);
    }
}

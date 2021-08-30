package ru.netology.cloud_service_app.security.jwt;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

@Slf4j
public class JwtTokenHandler {
    private static final Set<String> JWT_TOKENS_BLACK_LIST_CASH = new ConcurrentSkipListSet<>();
    private static final String CLAIMS_NAME = "authorities";
    private static final String CLAIM_NAME = "authority";

    public static void handleJwtTokenAfterLogout(String authorizationHeader, String tokenPrefix) {
        val token = authorizationHeader.replaceAll(tokenPrefix, "");
        JWT_TOKENS_BLACK_LIST_CASH.add(token);
    }

    public static boolean checkRequestHeader(String authorizationHeader, String tokenPrefix) {
        return authorizationHeader != null
                && !authorizationHeader.isEmpty()
                && authorizationHeader.startsWith(tokenPrefix);
    }

    public static void verifyJwtToken(String authorizationHeader, String tokenPrefix, String secretKey) {
        try {

            val token = authorizationHeader.replaceAll(tokenPrefix, "");

            if (JWT_TOKENS_BLACK_LIST_CASH.contains(token)) {
                throw new IllegalArgumentException("Попытка входа в систему с JWT-токеном, который был помещен в черный список");
            }

            val claims = Jwts.parserBuilder()
                    .setSigningKey(getSecretKeyHash(secretKey))
                    .build()
                    .parseClaimsJws(token);

            val body = claims.getBody();
            val username = body.getSubject();
            val authoritiesAsList = (List<Map<String, String>>) body.get(CLAIMS_NAME);

            val authorities = authoritiesAsList.stream()
                    .map(value -> new SimpleGrantedAuthority(value.get(CLAIM_NAME)))
                    .collect(Collectors.toSet());

            val authentication = new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }

    public static String singJwtToken(String username,
                                      String secretKey,
                                      Collection<? extends GrantedAuthority> grantedAuthorities,
                                      int expiration) {
        return Jwts.builder()
                .setSubject(username)
                .claim(CLAIMS_NAME, grantedAuthorities)
                .setIssuedAt(new Date())
                .setExpiration(java.sql.Date.valueOf(LocalDate.now().plusDays(expiration)))
                .signWith(getSecretKeyHash(secretKey))
                .compact();
    }

    private static SecretKey getSecretKeyHash(String secretKey) {
        return Keys.hmacShaKeyFor(secretKey.getBytes());
    }
}

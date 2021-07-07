package ru.netology.cloud_service_app.security.security_configs;

import lombok.val;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.netology.cloud_service_app.security.success_response_handlers.CustomLogoutSuccessHandler;
import ru.netology.cloud_service_app.security.jwt.jwt_exceptions.JwtAuthenticationEntryPoint;
import ru.netology.cloud_service_app.security.security_exception_handlers.SecurityExceptionHandler;
import ru.netology.cloud_service_app.security.jwt.JwtConfig;
import ru.netology.cloud_service_app.security.jwt.JwtTokenVerifier;
import ru.netology.cloud_service_app.security.jwt.JwtUsernamePasswordAuthenticationFilter;
import ru.netology.cloud_service_app.security.security_user_details_services.CustomUserDetailsService;

import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtConfig jwtConfig;
    private final SecurityConfigProperties securityConfigProperties;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtConfig jwtConfig,
                          SecurityConfigProperties securityConfigProperties,
                          CustomUserDetailsService userDetailsService) {
        this.jwtConfig = jwtConfig;
        this.securityConfigProperties = securityConfigProperties;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }

    @Bean
    public SecurityExceptionHandler securityExceptionHandler() {
        return new SecurityExceptionHandler();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()

                .csrf().disable()

                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint())

                .and()

                .logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())

                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .addFilterBefore(new CorsFilter(corsConfigurationSource()), ChannelProcessingFilter.class)
                .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(), jwtConfig, securityExceptionHandler()))
                .addFilterAfter(new JwtTokenVerifier(jwtConfig), JwtUsernamePasswordAuthenticationFilter.class)

                .authorizeRequests()
                .anyRequest().authenticated();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        val configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(securityConfigProperties.getAllowedOrigins()));
        configuration.setAllowedMethods(List.of("HEAD",
                "GET", "POST", "PUT", "DELETE", "PATCH"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("Content-Type", "auth-token", "Cache-Control"));
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        daoAuthenticationProvider.setUserDetailsService(userDetailsService);
        return daoAuthenticationProvider;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

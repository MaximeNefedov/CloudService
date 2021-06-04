package ru.netology.cloud_service_app.sercurity;

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
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import ru.netology.cloud_service_app.jwt.JwtConfig;
import ru.netology.cloud_service_app.jwt.JwtTokenVerifier;
import ru.netology.cloud_service_app.jwt.JwtUsernamePasswordAuthenticationFilter;
import ru.netology.cloud_service_app.services.CustomUserDetailsService;

import java.util.List;

@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final JwtConfig jwtConfig;
    private final SecurityConfigProperties securityConfigProperties;
    private final CustomUserDetailsService userDetailsService;

    public SecurityConfig(JwtConfig jwtConfig, SecurityConfigProperties securityConfigProperties, CustomUserDetailsService userDetailsService) {
        this.jwtConfig = jwtConfig;
        this.securityConfigProperties = securityConfigProperties;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()

                .and()

                .csrf().disable()

                .logout().logoutSuccessHandler(new CustomLogoutSuccessHandler())

                .and()

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

                .and()

                .addFilterBefore(new CorsFilter(corsConfigurationSource()), ChannelProcessingFilter.class)
                .addFilter(new JwtUsernamePasswordAuthenticationFilter(authenticationManager(), jwtConfig))
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
        // setAllowCredentials(true) is important, otherwise:
        // The value of the 'Access-Control-Allow-Origin' header in the response must not be the wildcard '*' when the request's credentials mode is 'include'.
        configuration.setAllowCredentials(true);
        // setAllowedHeaders is important! Without it, OPTIONS preflight request
        // will fail with 403 Invalid CORS request
        configuration.setAllowedHeaders(List.of("Content-Type", "auth-token", "Cache-Control"));
//        configuration.setAllowedHeaders(List.of("Authorization", "Cache-Control", "Content-Type"));
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

//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService() {
//        UserDetails userMax = User.builder()
//                .username("max@mail.ru")
//                .password(passwordEncoder().encode("123"))
//                .roles("ADMIN")
//                .build();
//
//        UserDetails userKate = User.builder()
//                .username("kate@mail.ru")
//                .password(passwordEncoder().encode("123"))
//                .roles("USER")
//                .build();
//
//        return new InMemoryUserDetailsManager(userMax, userKate);
//    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

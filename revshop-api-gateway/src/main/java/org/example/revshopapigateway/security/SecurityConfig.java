package org.example.revshopapigateway.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {

        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(auth -> auth
                        .pathMatchers("/auth/**").permitAll()
                        .pathMatchers("/user/security-question").permitAll()
                        .pathMatchers("/user/forgot-password").permitAll()
                        .pathMatchers("/products/**").permitAll()
                        .pathMatchers("/categories/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(jwtFilter,
                        org.springframework.security.config.web.server.SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
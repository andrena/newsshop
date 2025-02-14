package com.andrena.newsec.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // Intentionally weak security configuration
        http
            .csrf().disable()
            .authorizeHttpRequests()
                .requestMatchers("/api/newsletter/subscribe").permitAll()
                .requestMatchers("/api/newsletter/subscribers").authenticated()
                .anyRequest().authenticated()
            .and()
            .httpBasic();
        
        return http.build();
    }
}

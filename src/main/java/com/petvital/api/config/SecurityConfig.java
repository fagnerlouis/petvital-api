package com.petvital.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable) // Desabilitado para APIs REST (Stateless)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/clinicas/**").permitAll() // Rota pública temporária para testar cadastro
                .anyRequest().authenticated()
            );
        return http.build();
    }
}

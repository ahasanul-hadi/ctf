package com.cirt.ctf.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    @Order(2)
    SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception{

        return http
                .formLogin(form->form.loginPage("/web/users/login").defaultSuccessUrl("/web/home", true).permitAll())
                .sessionManagement(se->se.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED).maximumSessions(1).expiredUrl("/web/users/login?expired"))
                .requestCache(c->c.disable())
                .exceptionHandling(ex->ex.accessDeniedPage("/error"))
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/**").permitAll()
                        .anyRequest().authenticated()
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

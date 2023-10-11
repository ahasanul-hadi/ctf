package com.cirt.ctf.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {
    @Bean
    SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception{

        return http
                .csrf(AbstractHttpConfigurer::disable)
                //.anonymous(AbstractHttpConfigurer::disable)
                .formLogin(form->form.loginPage("/login").defaultSuccessUrl("/", true).permitAll())
                .requestCache(RequestCacheConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/teams/registration").permitAll()
                        .requestMatchers("/users/**").permitAll()
                        .requestMatchers("/","/index").permitAll()
                        .anyRequest().authenticated()
                )
                .exceptionHandling(c->c.accessDeniedPage("/403"))
                .logout(out->out.logoutSuccessUrl("/login?logout").deleteCookies().clearAuthentication(true).invalidateHttpSession(true))
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

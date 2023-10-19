package com.cirt.ctf.config.security;

import com.cirt.ctf.exception.AppAccessDeniedHandler;
import com.cirt.ctf.exception.AppAuthEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.RequestCacheConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private  final AppAuthEntryPoint appAuthEntryPoint;
    private final AppAccessDeniedHandler accessDeniedHandler;


    @Bean
    SecurityFilterChain webFilterChain(HttpSecurity http) throws Exception{

        return http
                .csrf(AbstractHttpConfigurer::disable)
                //.sessionManagement(se->se.maximumSessions(1).maxSessionsPreventsLogin(true).sessionRegistry(sessionRegistry()))
                //.exceptionHandling(exp->exp.authenticationEntryPoint(appAuthEntryPoint).accessDeniedHandler(accessDeniedHandler))
                .formLogin(form->form.loginPage("/login").defaultSuccessUrl("/",true).permitAll())
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests( authorize -> authorize
                        .requestMatchers("/actuator/**").permitAll()
                        .requestMatchers("/assets/**").permitAll()
                        .requestMatchers("/teams/registration").permitAll()
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

    // Work around https://jira.spring.io/browse/SEC-2855
    @Bean
    public SessionRegistry sessionRegistry() {
        SessionRegistry sessionRegistry = new SessionRegistryImpl();
        return sessionRegistry;
    }
}

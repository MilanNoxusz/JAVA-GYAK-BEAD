package com.example.gyakbea;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // ÚJ IMPORT
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // A tanár által kért új annotáció a régi @EnableGlobalMethodSecurity helyett
public class WebSecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // JAVÍTVA: A konstruktornak átadjuk a userDetailsService-t (Spring Security 6+ elvárás)
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // Statikus fájlok engedélyezése (CSS, Képek, JS, Fonts)
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/fonts/**").permitAll()
                        // Nyilvános oldalak
                        .requestMatchers("/", "/index", "/register", "/regisztral_feldolgoz").permitAll()
                        // Admin oldal
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        // Üzenetek oldal
                        .requestMatchers("/messages/**").hasAnyRole("USER", "ADMIN")
                        // Minden más csak bejelentkezés után
                        .anyRequest().authenticated()
                )
                .formLogin((form) -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout((logout) -> logout
                        .logoutSuccessUrl("/")
                        .permitAll()
                );

        return http.build();
    }
}
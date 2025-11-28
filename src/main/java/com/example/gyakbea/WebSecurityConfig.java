package com.example.gyakbea;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
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
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((requests) -> requests
                        // Statikus fájlok
                        .requestMatchers("/css/**", "/js/**", "/images/**", "/img/**", "/fonts/**", "/sass/**").permitAll()

                        // PUBLIKUS oldalak (Vendég is látja a leírásod alapján: Diagram, CRUD, REST is!)
                        .requestMatchers("/", "/index", "/adatbazis", "/contact", "/login", "/register", "/regisztral_feldolgoz").permitAll()
                        .requestMatchers("/diagram", "/crud", "/rest").permitAll() // Ezeket kérted, hogy a vendég is lássa

                        // Védett oldalak (Csak bejelentkezett felhasználóknak)
                        .requestMatchers("/messages/**").hasAnyRole("USER", "ADMIN")

                        // Admin oldalak
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // Minden más hitelesítést igényel
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
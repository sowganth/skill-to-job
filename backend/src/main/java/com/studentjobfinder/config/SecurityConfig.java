package com.studentjobfinder.config;

import com.studentjobfinder.security.TokenAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final CorsConfigurationSource corsConfigurationSource;

    public SecurityConfig(TokenAuthenticationFilter tokenAuthenticationFilter,
                          CorsConfigurationSource corsConfigurationSource) {
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.corsConfigurationSource = corsConfigurationSource;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            )
            .authorizeHttpRequests(auth -> auth
                // Public authentication & health
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/students/register", "/api/students/login").permitAll()
                .requestMatchers("/api/health").permitAll()

                // Public catalog reads (jobs, skills catalogue, learning resources)
                .requestMatchers(HttpMethod.GET, "/api/jobs", "/api/jobs/{id}").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/skills").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/learning-resources").permitAll()

                // Admin endpoints strictly restricted
                .requestMatchers("/api/admin/**").hasRole("ADMIN")

                // Protected student endpoints
                .requestMatchers("/api/students/me/**").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/api/students/profile").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/api/dashboard").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/api/jobs/recommendations").hasAnyRole("STUDENT", "ADMIN")
                .requestMatchers("/api/jobs/{id}/skill-gap").hasAnyRole("STUDENT", "ADMIN")

                // Any other API request requires authentication
                .anyRequest().authenticated()
            );

        http.addFilterBefore(tokenAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

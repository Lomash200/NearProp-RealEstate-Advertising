package com.nearprop.config;

import com.nearprop.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/v1/auth/**",
                    "/auth/**",
                    "/public/**",
                        "/api/public/**",
                    "/swagger-ui/**",
                    "/v3/api-docs/**",
                    "/actuator/**",
                    "/api/performance/health", // Allow health check endpoint without authentication
                    "/api/performance/auth-debug", // Allow auth debug endpoint without authentication
                    "/v1/admin/setup",  // Allow the initial admin setup without authentication
                    "/ws/**",           // Allow WebSocket connections without authentication
                    "/ws",              // Base WebSocket endpoint
                    "/socket/**",       // Alternative WebSocket paths to try
                    "/websocket/**",    // Alternative WebSocket paths to try
                    "/api/ws/**",       // Working WebSocket endpoint
                    "/api/ws",          // Base API WebSocket endpoint
                    "/v1/geolocation/**", // Allow access to all geolocation endpoints for testing
                    "/v1/advertisements/**", // Allow access to all advertisement endpoints for testing
                    "/v1/properties/**",  // Allow access to all property advertisement endpoints for testing
                    "/api/property-districts/**", // Allow access to property districts
                    "/api/franchisee/requests/is-district-assigned/**", // Allow checking if district is assigned
                    "/api/franchisee/requests/test", // Allow test endpoint
                    "/api/franchisee/test/**", // Allow all test endpoints
                    "/api/subscriptions/plans/**", // Allow access to subscription plans
                    "/api/subscriptions/health", // Allow access to subscription health endpoint
                    "/api/subscriptions/test-email", // Allow access to email test form
                    "/api/subscriptions/send-test-email", // Allow access to email test endpoint
                    "/api/email-test/**", // Allow access to email test endpoints
                    "/api/simple-test/**", // Allow access to simple test endpoints
                    "/v1/debug/**",       // Allow access to debug endpoints
                    "/api/safety-standards", // Allow access to safety standards with context path
                    "/safety-standards", // Allow access to safety standards without context path
		    "/property-districts/**"
                ).permitAll()
                .requestMatchers(HttpMethod.GET, "/api/admin/coupons/**").permitAll() // Allow GET access to admin coupon endpoints
                .requestMatchers(HttpMethod.GET, "/api/coupons/**").permitAll() // Allow GET access to coupon endpoints
                .requestMatchers(HttpMethod.POST, "/api/coupons/validate").permitAll() // Allow POST access to coupon validation
                .requestMatchers(HttpMethod.POST, "/api/inquiries").permitAll()
                .requestMatchers(HttpMethod.POST, "/inquiries").permitAll()
                .requestMatchers("/subscriptions/test-email", "/subscriptions/send-test-email", "/subscriptions/plans/**").permitAll() // For backward compatibility
                // Allow property search and property by ID endpoints without authentication
                .requestMatchers(HttpMethod.GET, "/api/properties").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/properties/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/properties").permitAll()
                .requestMatchers(HttpMethod.GET, "/properties/**").permitAll()
                    .requestMatchers(HttpMethod.DELETE, "/api/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }



    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true); // Important for WebSocket connection with credentials
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
} 

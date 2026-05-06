package com.nearprop.security;

import com.nearprop.entity.User;
import com.nearprop.entity.UserSession;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.UserSessionRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final UserSessionRepository userSessionRepository;
    
    // Testing mode flag - set to true for development/testing only
    private static final boolean TESTING_MODE = true;
    
    // Debug mode flag to log detailed role information
    private static final boolean DEBUG_ROLE_INFO = true;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getServletPath();
        // Skip JWT authentication for public endpoints
        
            if (path.equals("/safety-standards") || path.equals("/v1/auth/login") || path.equals("/v1/auth/register") || path.startsWith("/property-districts") || path.startsWith("/api/property-districts") ) {
            filterChain.doFilter(request, response);
            return;
        }

        

        try {
	    String jwt = getJwtFromRequest(request);
            // Log the request path and method
            log.info("Request: {} {}", request.getMethod(), request.getRequestURI());

            if (StringUtils.hasText(jwt)) {
                log.info("JWT token found: {}", jwt.substring(0, Math.min(10, jwt.length())) + "...");
                
                boolean isValid = jwtUtil.validateToken(jwt);
                log.info("JWT validation result: {}", isValid);
                
                if (isValid) {
                    log.info("JWT token validated successfully");
                    
                    String userId = jwtUtil.getUserIdFromToken(jwt);
                    String sessionId = jwtUtil.getSessionIdFromToken(jwt);
                    
                    log.info("User ID from token: {}", userId);
                    log.info("Session ID from token: {}", sessionId);
                
                    log.debug("Checking session ID: {} for user ID: {}", sessionId, userId);
                    Optional<UserSession> sessionOpt = userSessionRepository.findBySessionIdAndActive(sessionId, true);
                    
                    if (sessionOpt.isPresent()) {
                        UserSession session = sessionOpt.get();
                        User user = session.getUser();
                        
                        // Check if session is expired
                        if (session.getExpiresAt() != null && session.getExpiresAt().isBefore(LocalDateTime.now())) {
                            log.debug("Session expired for user ID: {}", userId);
                            session.setActive(false);
                            userSessionRepository.save(session);
                        } else if (user != null && user.getId().toString().equals(userId)) {
                            // Update last accessed timestamp
                            session.setLastAccessedAt(LocalDateTime.now());
                            userSessionRepository.save(session);
                            
                            authenticateUser(request, user);
                        } else {
                            log.warn("User ID mismatch in token and session. Token user ID: {}, Session user ID: {}", 
                                    userId, user != null ? user.getId() : "null");
                        }
                    } else {
                        log.debug("No active session found for session ID: {}", sessionId);
                        
                        // For development/testing only - allow token authentication without session check
//                        if (TESTING_MODE) {
//                            log.warn("TESTING MODE: Bypassing session check for token authentication");
//                            userRepository.findById(Long.parseLong(userId)).ifPresent(user -> {
//                                log.debug("Authenticating user in testing mode: {}", user.getId());
//                                authenticateUser(request, user);
//                            });
//                        }

                        if (TESTING_MODE) {
                            log.warn("TESTING MODE: Bypassing session check for token authentication");

                            if (userId != null && !userId.isBlank()) {
                                try {
                                    Long uid = Long.parseLong(userId);
                                    userRepository.findById(uid).ifPresent(user -> {
                                        log.debug("Authenticating user in testing mode: {}", user.getId());
                                        authenticateUser(request, user);
                                    });
                                } catch (NumberFormatException ex) {
                                    log.error("Invalid userId in JWT: {}", userId);
                                }
                            } else {
                                log.error("JWT valid hai but userId claim missing/blank hai");
                            }
                        }

                    }
                }
            } else {
                log.debug("No JWT token found in request");
            }
        } catch (Exception e) {
            log.error("Could not authenticate user: {}", e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }

    private void authenticateUser(HttpServletRequest request, User user) {
        // Ensure roles are properly loaded from database
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            log.warn("User {} has no roles assigned. Reloading from database.", user.getId());
            User refreshedUser = userRepository.findById(user.getId()).orElse(user);
            if (refreshedUser.getRoles() != null && !refreshedUser.getRoles().isEmpty()) {
                user = refreshedUser;
                log.info("Successfully reloaded user with roles: {}", user.getRoles());
            } else {
                log.warn("User still has no roles after reload from database");
            }
        }
        
        List<SimpleGrantedAuthority> authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .collect(Collectors.toList());
        
        // Add debug logging for roles
        if (DEBUG_ROLE_INFO) {
            log.info("User roles from database: {}", user.getRoles());
            log.info("Generated authorities: {}", authorities);
            
            // Check if ADMIN role exists
            boolean hasAdminRole = user.getRoles().stream()
                    .anyMatch(role -> role.name().equals("ADMIN"));
            log.info("User has ADMIN role in database: {}", hasAdminRole);
            
            boolean hasAdminAuthority = authorities.stream()
                    .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
            log.info("User has ROLE_ADMIN authority: {}", hasAdminAuthority);
            
            // Log all request headers for debugging
            log.debug("Request headers:");
            java.util.Enumeration<String> headerNames = request.getHeaderNames();
            while (headerNames.hasMoreElements()) {
                String headerName = headerNames.nextElement();
                log.debug("{}: {}", headerName, request.getHeader(headerName));
            }
        }
        
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                user, null, authorities);
        
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        log.info("Authenticated user ID: {} with roles: {}", user.getId(), user.getRoles());
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
} 

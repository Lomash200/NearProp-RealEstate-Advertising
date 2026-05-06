package com.nearprop.config;

import com.nearprop.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.Collections;
import java.util.List;

@Configuration
@EnableWebSocketMessageBroker
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@RequiredArgsConstructor
@Slf4j
public class WebSocketAuthenticationConfig implements WebSocketMessageBrokerConfigurer {

    private final JwtUtil jwtUtil;

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    // Extract token from query parameters
                    String token = extractTokenFromQueryParams(accessor);
                    log.debug("WebSocket connection attempt with token: {}", token != null ? "present" : "absent");

                    if (token != null) {
                        try {
                            // Validate token and set authentication if valid
                            if (jwtUtil.validateToken(token)) {
                                String userId = jwtUtil.getUserIdFromToken(token);
                                
                                // Create a simple user authentication
                                // We'll use USER role since we just need basic authentication
                                List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                                        new SimpleGrantedAuthority("ROLE_USER"));
                                
                                UsernamePasswordAuthenticationToken authentication = 
                                        new UsernamePasswordAuthenticationToken(userId, null, authorities);
                                
                                accessor.setUser(authentication);
                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug("WebSocket authenticated for user ID: {}", userId);
                            } else {
                                log.warn("Invalid WebSocket token provided");
                            }
                        } catch (Exception e) {
                            log.error("Error processing WebSocket authentication", e);
                        }
                    }
                }
                return message;
            }
        });
    }

    private String extractTokenFromQueryParams(StompHeaderAccessor accessor) {
        String query = accessor.getFirstNativeHeader("query");
        if (query != null && query.contains("token=")) {
            return query.substring(query.indexOf("token=") + 6);
        }
        
        // Try to get token from session attributes if set by HTTP handshake
        if (accessor.getSessionAttributes() != null) {
            String token = (String) accessor.getSessionAttributes().get("token");
            if (token != null) {
                return token;
            }
        }
        
        return null;
    }
} 
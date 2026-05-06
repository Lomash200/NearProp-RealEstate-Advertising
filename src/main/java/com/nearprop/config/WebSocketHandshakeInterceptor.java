package com.nearprop.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.net.URI;
import java.util.Map;

@Component
@Slf4j
public class WebSocketHandshakeInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                  WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        URI uri = request.getURI();
        String query = uri.getQuery();
        log.debug("WebSocket handshake request received with query: {}", query);

        // Extract token from query string if present
        if (query != null && query.contains("token=")) {
            String token = extractTokenFromQuery(query);
            if (token != null && !token.isEmpty()) {
                attributes.put("token", token);
                log.debug("Token extracted and stored in session attributes");
                return true;
            } else {
                log.warn("Token parameter is present but empty in WebSocket handshake");
            }
        } else {
            log.warn("No token found in WebSocket handshake query parameters");
        }
        
        // Allow the handshake to proceed even without a token
        // The WebSocketAuthenticationConfig will handle authorization later
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                              WebSocketHandler wsHandler, Exception exception) {
        // No action needed after handshake
    }
    
    private String extractTokenFromQuery(String query) {
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            if (pair.startsWith("token=")) {
                return pair.substring("token=".length());
            }
        }
        return null;
    }
} 
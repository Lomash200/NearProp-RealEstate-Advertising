package com.nearprop.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

/**
 * Configuration for enhanced HTTP request logging
 */
@Configuration
public class RequestLoggingFilterConfig {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilterConfig.class);
    private static final int MAX_PAYLOAD_LENGTH = 10000;

    @Bean
    public OncePerRequestFilter requestLoggingFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
                    throws ServletException, IOException {
                
                // Generate unique request ID and add to MDC
                String requestId = UUID.randomUUID().toString();
                MDC.put("requestId", requestId);
                
                // Wrap request and response to allow reading the body multiple times
                ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
                ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
                
                // Add request ID to response headers
                wrappedResponse.setHeader("X-Request-ID", requestId);
                
                long startTime = System.currentTimeMillis();
                
                try {
                    // Log the request
                    logRequest(wrappedRequest);
                    
                    // Process the request
                    filterChain.doFilter(wrappedRequest, wrappedResponse);
                    
                    // Log the response
                    logResponse(wrappedRequest, wrappedResponse, System.currentTimeMillis() - startTime);
                } finally {
                    // Copy content to the original response
                    wrappedResponse.copyBodyToResponse();
                    
                    // Clear MDC
                    MDC.remove("requestId");
                }
            }
            
            private void logRequest(ContentCachingRequestWrapper request) {
                String queryString = request.getQueryString();
                String url = request.getRequestURL() + (queryString != null ? "?" + queryString : "");
                
                log.info("REQUEST: {} {} (Client: {})", 
                        request.getMethod(), 
                        url,
                        request.getRemoteAddr());
                
                // Only log request body for specific content types
                String contentType = request.getContentType();
                if (contentType != null && 
                    (contentType.startsWith("application/json") || 
                     contentType.startsWith("application/xml") ||
                     contentType.startsWith("multipart/form-data"))) {
                    
                    logPayload("Request body", request.getContentAsByteArray(), request.getCharacterEncoding());
                }
            }
            
            private void logResponse(ContentCachingRequestWrapper request, 
                                    ContentCachingResponseWrapper response,
                                    long timeElapsed) {
                
                log.info("RESPONSE: {} {} - {} ({} ms)",
                        request.getMethod(),
                        request.getRequestURI(),
                        response.getStatus(),
                        timeElapsed);
                
                // Only log response body for specific content types
                String contentType = response.getContentType();
                if (contentType != null && 
                    (contentType.startsWith("application/json") || 
                     contentType.startsWith("application/xml"))) {
                    
                    logPayload("Response body", response.getContentAsByteArray(), response.getCharacterEncoding());
                }
            }
            
            private void logPayload(String prefix, byte[] content, String encoding) {
                if (content.length == 0) return;
                
                int length = Math.min(content.length, MAX_PAYLOAD_LENGTH);
                String contentString;
                try {
                    contentString = new String(content, 0, length, encoding != null ? encoding : "UTF-8");
                    log.debug("{}: {}", prefix, contentString);
                } catch (UnsupportedEncodingException e) {
                    log.warn("Failed to log payload: {}", e.getMessage());
                }
            }
        };
    }
} 
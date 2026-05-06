package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.service.LocalizationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.LocaleResolver;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.Locale;
import java.util.Map;

/**
 * Controller for language/locale switching
 */
@RestController
@RequestMapping("/locale")
@RequiredArgsConstructor
@Slf4j
public class LocalizationController {
    
    private final LocaleResolver localeResolver;
    private final LocalizationService localizationService;
    
    /**
     * Change the locale for the current user session
     * 
     * @param lang Language code (e.g., "en", "hi")
     * @param request HTTP request
     * @param response HTTP response
     * @return Response with success or error message
     */
    @PostMapping("/change")
    public ResponseEntity<ApiResponse<Void>> changeLocale(
            @RequestParam String lang,
            HttpServletRequest request,
            HttpServletResponse response) {
        
        try {
            Locale locale = new Locale(lang);
            log.info("Changing locale to: {}", locale);
            
            localeResolver.setLocale(request, response, locale);
            
            String message = localizationService.getMessage("locale.changed");
            return ResponseEntity.ok(ApiResponse.success(message));
            
        } catch (Exception e) {
            log.error("Failed to change locale to: {}", lang, e);
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error("Failed to change language: " + e.getMessage()));
        }
    }
    
    /**
     * Get the current locale information
     * 
     * @return Current locale information
     */
    @GetMapping("/current")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getCurrentLocale() {
        
        Locale currentLocale = localizationService.getCurrentLocale();
        
        Map<String, Object> localeInfo = Map.of(
                "language", currentLocale.getLanguage(),
                "displayLanguage", currentLocale.getDisplayLanguage(),
                "country", currentLocale.getCountry(),
                "displayName", currentLocale.getDisplayName()
        );
        
        return ResponseEntity.ok(ApiResponse.success("Current locale retrieved", localeInfo));
    }
    
    /**
     * Get a specific message in the current locale
     * 
     * @param code Message code
     * @return Localized message
     */
    @GetMapping("/message")
    public ResponseEntity<ApiResponse<String>> getMessage(@RequestParam String code) {
        
        String message = localizationService.getMessage(code);
        
        return ResponseEntity.ok(ApiResponse.success("Message retrieved", message));
    }
    
    /**
     * Get a list of supported languages
     * 
     * @return List of supported languages
     */
    @GetMapping("/supported")
    public ResponseEntity<ApiResponse<Map<String, String>>> getSupportedLanguages() {
        
        Map<String, String> supportedLanguages = Map.of(
                "en", "English",
                "hi", "हिन्दी (Hindi)"
        );
        
        return ResponseEntity.ok(
                ApiResponse.success("Supported languages retrieved", supportedLanguages));
    }
} 
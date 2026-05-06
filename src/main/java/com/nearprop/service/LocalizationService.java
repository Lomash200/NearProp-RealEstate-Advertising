package com.nearprop.service;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

/**
 * Service for handling localization and internationalization
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class LocalizationService {
    
    private final MessageSource messageSource;
    
    /**
     * Get a message for the current locale
     * 
     * @param code Message code
     * @return Localized message
     */
    public String getMessage(String code) {
        return getMessage(code, null);
    }
    
    /**
     * Get a message for the current locale with arguments
     * 
     * @param code Message code
     * @param args Arguments for message placeholders
     * @return Localized message
     */
    public String getMessage(String code, Object[] args) {
        Locale locale = LocaleContextHolder.getLocale();
        return getMessage(code, args, locale);
    }
    
    /**
     * Get a message for a specific locale
     * 
     * @param code Message code
     * @param args Arguments for message placeholders
     * @param locale Specific locale
     * @return Localized message
     */
    public String getMessage(String code, Object[] args, Locale locale) {
        try {
            return messageSource.getMessage(code, args, locale);
        } catch (Exception e) {
            log.warn("Failed to get message for code: {}, locale: {}", code, locale, e);
            return code; // Return the code itself as fallback
        }
    }
    
    /**
     * Get the current user's locale
     * 
     * @return Current locale
     */
    public Locale getCurrentLocale() {
        return LocaleContextHolder.getLocale();
    }
    
    /**
     * Check if current locale is English
     * 
     * @return True if locale is English
     */
    public boolean isEnglishLocale() {
        return "en".equals(getCurrentLocale().getLanguage());
    }
    
    /**
     * Check if current locale is Hindi
     * 
     * @return True if locale is Hindi
     */
    public boolean isHindiLocale() {
        return "hi".equals(getCurrentLocale().getLanguage());
    }
} 
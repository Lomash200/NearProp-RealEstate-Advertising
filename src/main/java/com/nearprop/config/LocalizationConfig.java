package com.nearprop.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

import java.util.Locale;

/**
 * Configuration for internationalization and localization support
 */
@Configuration
public class LocalizationConfig implements WebMvcConfigurer {
    
    /**
     * Configures the message source for i18n
     * 
     * @return MessageSource bean
     */
    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames(
                "messages/general", 
                "messages/validation", 
                "messages/error", 
                "messages/property",
                "messages/subscription",
                "messages/analytics"
        );
        messageSource.setDefaultEncoding("UTF-8");
        // Use code as default message if no message found
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }
    
    /**
     * Define locale resolver to determine which locale to use
     * Uses cookie-based locale resolution
     * 
     * @return LocaleResolver bean
     */
    @Bean
    public LocaleResolver localeResolver() {
        CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver("NEARPROP_LOCALE");
        cookieLocaleResolver.setDefaultLocale(new Locale("en")); // Default language is English
        return cookieLocaleResolver;
    }
    
    /**
     * Configure the interceptor that switches locales when the 'lang' parameter is included in a request
     * 
     * @return LocaleChangeInterceptor bean
     */
    @Bean
    public LocaleChangeInterceptor localeChangeInterceptor() {
        LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
        localeChangeInterceptor.setParamName("lang"); // Parameter name to switch language
        return localeChangeInterceptor;
    }
    
    /**
     * Register the locale change interceptor
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(localeChangeInterceptor());
    }
} 
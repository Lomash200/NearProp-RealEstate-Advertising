package com.nearprop.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for formatting JSON objects in logs
 */
public class JsonFormatter {
    private static final Logger log = LoggerFactory.getLogger(JsonFormatter.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);

    /**
     * Format an object as pretty-printed JSON
     * @param object The object to format
     * @return Pretty-printed JSON string or original toString() if formatting fails
     */
    public static String formatJson(Object object) {
        if (object == null) {
            return "null";
        }
        
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            log.warn("Failed to format object as JSON: {}", e.getMessage());
            return object.toString();
        }
    }
    
    /**
     * Format an object as pretty-printed JSON with a custom label
     * @param label Label to prepend to the JSON
     * @param object The object to format
     * @return Labeled, pretty-printed JSON string
     */
    public static String formatJsonWithLabel(String label, Object object) {
        return label + ":\n" + formatJson(object);
    }
} 
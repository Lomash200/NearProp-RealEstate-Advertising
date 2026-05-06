package com.nearprop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final AmazonS3Service amazonS3Service;
    
    /**
     * Retrieve document URLs from comma-separated document IDs
     */
    public List<String> getDocumentUrls(String documentIds) {
        if (documentIds == null || documentIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        try {
            return Arrays.stream(documentIds.split(","))
                    .map(String::trim)
                    .filter(id -> !id.isEmpty())
                    .map(amazonS3Service::getDocumentUrl)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Error retrieving document URLs", e);
            return Collections.emptyList();
        }
    }
}

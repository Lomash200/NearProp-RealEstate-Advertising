package com.nearprop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AmazonS3Service {

    @Value("${aws.s3.document-bucket-url:https://nearprop-documents.s3.amazonaws.com}")
    private String documentBucketUrl;
    
    /**
     * Get the URL for a document in S3 by its ID
     */
    public String getDocumentUrl(String documentId) {
        // In a real implementation, this would use the AWS SDK to generate a pre-signed URL
        // or retrieve the object from S3. For now, we'll just construct a simple URL.
        return documentBucketUrl + "/" + documentId;
    }
}

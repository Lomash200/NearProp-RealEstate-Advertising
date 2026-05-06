package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for child safety standards API required by Google Play
 * This API is public and does not require authentication
 */
@RestController
@RequestMapping("/safety-standards")
@RequiredArgsConstructor
@Slf4j
public class SafetyStandardsController {

    /**
     * Get safety standards as plain text
     * @return Plain text safety standards document
     */
    @GetMapping(produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> getSafetyStandards() {
        log.info("Retrieving safety standards document");
        String safetyStandardsText = generateSafetyStandardsDocument();
        return ResponseEntity.ok(safetyStandardsText);
    }

    /**
     * Get safety standards as JSON
     * @return JSON response with safety standards document
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ApiResponse<String>> getSafetyStandardsJson() {
        log.info("Retrieving safety standards document as JSON");
        String safetyStandardsText = generateSafetyStandardsDocument();
        return ResponseEntity.ok(ApiResponse.success("Safety standards retrieved successfully", safetyStandardsText));
    }

    /**
     * Generate the safety standards document
     * @return Safety standards text
     */
    private String generateSafetyStandardsDocument() {
        return "NearProp Child Safety and CSAE Standards\n\n" +
                "At NearProp, we are committed to providing a safe environment for all users, " +
                "with special attention to child safety and protection against Child Sexual Abuse and Exploitation (CSAE).\n\n" +
                "Our Commitments:\n\n" +
                "1. Age Restrictions: NearProp services are designed for adults aged 18 and above. " +
                "We do not knowingly collect or solicit personal information from anyone under 18.\n\n" +
                "2. Content Monitoring: We employ both automated systems and human review to detect " +
                "and remove any inappropriate content that may violate our policies.\n\n" +
                "3. Reporting Mechanisms: We provide clear and accessible ways for users to report " +
                "concerning content or behavior.\n\n" +
                "4. Swift Action: We promptly investigate all reports and take appropriate action, " +
                "including content removal and account termination when necessary.\n\n" +
                "5. Cooperation with Authorities: We cooperate fully with law enforcement agencies " +
                "in investigations related to child safety and CSAE.\n\n" +
                "6. Regular Policy Updates: We continuously review and update our safety policies " +
                "to address emerging threats and improve protection measures.\n\n" +
                "7. User Education: We provide resources to educate users about online safety.\n\n" +
                "For more information or to report concerns, please contact safety@nearprop.com\n\n" +
                "Last Updated: " + java.time.LocalDate.now().toString();
    }
} 
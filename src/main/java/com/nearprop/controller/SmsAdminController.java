package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.service.DigitalSmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/v1/admin/sms")
@RequiredArgsConstructor
@Slf4j
public class SmsAdminController {
    private final DigitalSmsService digitalSmsService;

    @GetMapping("/balance")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> checkSmsBalance() {
        log.info("Admin requested SMS balance check");
        
        // Force a fresh balance check
        digitalSmsService.checkSmsBalance();
        
        // Get the balance status
        Map<String, Object> status = digitalSmsService.getSmsBalanceStatus();
        
        return ResponseEntity.ok(ApiResponse.success("SMS balance status retrieved", status));
    }
}

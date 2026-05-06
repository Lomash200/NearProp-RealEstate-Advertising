package com.nearprop.controller.franchisee;

import com.nearprop.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/franchisee/test")
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final SubscriptionService subscriptionService;

    @GetMapping("/expire-now")
    public String expireNow() {
        log.info("MANUAL EXPIRY TRIGGER");
        subscriptionService.processExpiredSubscriptions();
        return "Expiry job executed – check DB & logs";
    }

    @GetMapping("/ping")
    public ResponseEntity<Map<String, Object>> ping() {
        log.info("Test endpoint called - ping");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Franchisee test endpoint working!");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }



    @GetMapping("/admin-ping")
    public ResponseEntity<Map<String, Object>> adminPing() {
        log.info("Admin test endpoint called - admin-ping");
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Franchisee admin test endpoint working!");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
} 
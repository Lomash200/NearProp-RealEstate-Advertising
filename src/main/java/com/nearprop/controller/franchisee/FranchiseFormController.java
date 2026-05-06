package com.nearprop.controller.franchisee;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/franchisee/form")
public class FranchiseFormController {

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> submitForm(
            @RequestPart("districtId") String districtId,
            @RequestPart("businessName") String businessName,
            @RequestPart("businessAddress") String businessAddress,
            @RequestPart(value = "documents", required = false) List<MultipartFile> documents) {
        
        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Form received successfully");
        response.put("districtId", districtId);
        response.put("businessName", businessName);
        response.put("businessAddress", businessAddress);
        
        if (documents != null && !documents.isEmpty()) {
            response.put("documentCount", documents.size());
        }
        
        return ResponseEntity.ok(response);
    }
} 
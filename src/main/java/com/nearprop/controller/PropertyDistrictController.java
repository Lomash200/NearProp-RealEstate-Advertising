package com.nearprop.controller;

import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.service.franchisee.DistrictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for managing district data
 * Used by frontend to fetch states and districts for property creation
 */
@RestController
@RequestMapping("/property-districts")
@RequiredArgsConstructor
@Slf4j
public class PropertyDistrictController {

    private final DistrictService districtService;
    
    /**
     * Get all states from the database
     */
    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        log.debug("REST request to get all states");
        return ResponseEntity.ok(districtService.getAllStates());
    }
    
    /**
     * Get all active districts
     */
    @GetMapping
    public ResponseEntity<List<DistrictDto>> getAllDistricts() {
        log.debug("REST request to get all districts");
        return ResponseEntity.ok(districtService.getActiveDistricts());
    }
    
    /**
     * Get districts by state
     */
    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<DistrictDto>> getDistrictsByState(@PathVariable String state) {
        log.debug("REST request to get districts by state: {}", state);
        return ResponseEntity.ok(districtService.getDistrictsByState(state));
    }
    
    /**
     * Get a single district by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<DistrictDto> getDistrict(@PathVariable Long id) {
        log.debug("REST request to get district: {}", id);
        return ResponseEntity.ok(districtService.getDistrict(id));
    }
} 
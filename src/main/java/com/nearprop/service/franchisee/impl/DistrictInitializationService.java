package com.nearprop.service.franchisee.impl;

import com.nearprop.entity.District;
import com.nearprop.repository.DistrictRepository;
import com.nearprop.service.franchisee.DistrictJsonService;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistrictInitializationService {

    private final DistrictJsonService districtJsonService;
    private final DistrictRepository districtRepository;
    
    @PersistenceContext
    private EntityManager entityManager;
    
    /**
     * Initialize districts from district.json file at application startup
     * only if no districts exist in the database
     */
    @PostConstruct
    @Transactional
    public void initializeDistricts() {
        // Check if districts already exist
        long districtCount = districtRepository.count();
        
        if (districtCount > 0) {
            log.info("Districts already exist in database (count: {}). Skipping initialization.", districtCount);
            return;
        }
        
        log.info("No districts found in database. Initializing districts from district.json");
        
        // Reset the sequence to start from 1
        try {
            entityManager.createNativeQuery("ALTER SEQUENCE districts_id_seq RESTART WITH 1").executeUpdate();
            log.info("District ID sequence reset to start from 1");
        } catch (Exception e) {
            log.warn("Failed to reset district ID sequence: {}", e.getMessage());
        }
        
        List<DistrictJsonService.StateDistrict> stateDistricts = districtJsonService.getAllStateDistricts();
        int totalCreated = 0;
        
        for (DistrictJsonService.StateDistrict stateDistrict : stateDistricts) {
            String state = stateDistrict.getState();
            
            for (DistrictJsonService.DistrictData districtData : stateDistrict.getDistricts()) {
                String districtName = districtData.getName();
                
                // Create new district
                District district = District.builder()
                        .name(districtName)
                        .state(state)
                        // Use district name as city for now
                        .city(districtName)
                        .pincode(districtData.getPinCode())
                        // Default revenue share percentage of 50%
                        .revenueSharePercentage(new BigDecimal("50.00"))
                        // Default active status
                        .active(true)
                        .build();
                totalCreated++;
                
                districtRepository.save(district);
            }
        }
        
        log.info("District initialization complete: {} created", totalCreated);
    }
} 
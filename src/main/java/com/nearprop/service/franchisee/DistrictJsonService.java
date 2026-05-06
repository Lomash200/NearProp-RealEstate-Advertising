package com.nearprop.service.franchisee;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.dto.franchisee.DistrictDto;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DistrictJsonService {

    private final ResourceLoader resourceLoader;
    private final ObjectMapper objectMapper;
    
    private List<StateDistrict> stateDistricts;
    private Map<String, List<DistrictData>> districtsByState;
    private Map<Long, DistrictData> districtsById;
    private Map<String, DistrictData> districtsByName;
    
    @PostConstruct
    public void loadDistricts() {
        try {
            Resource resource = resourceLoader.getResource("classpath:district.json");
            InputStream inputStream = resource.getInputStream();
            
            stateDistricts = objectMapper.readValue(inputStream, objectMapper.getTypeFactory().constructCollectionType(List.class, StateDistrict.class));
            
            // Create lookup maps
            districtsByState = new HashMap<>();
            districtsById = new HashMap<>();
            districtsByName = new HashMap<>();
            
            for (StateDistrict stateDistrict : stateDistricts) {
                districtsByState.put(stateDistrict.getState(), stateDistrict.getDistricts());
                
                for (DistrictData district : stateDistrict.getDistricts()) {
                    district.setState(stateDistrict.getState());
                    district.setStateCode(stateDistrict.getStateCode());
                    districtsById.put(district.getSerialNumber(), district);
                    districtsByName.put(district.getName().toLowerCase(), district);
                }
            }
            
            log.info("Loaded {} states and {} districts from district.json", stateDistricts.size(), districtsById.size());
        } catch (IOException e) {
            log.error("Failed to load district data from JSON file", e);
            // Initialize empty collections to prevent NullPointerExceptions
            stateDistricts = new ArrayList<>();
            districtsByState = new HashMap<>();
            districtsById = new HashMap<>();
            districtsByName = new HashMap<>();
        }
    }
    
    public List<String> getAllStates() {
        return stateDistricts.stream().map(StateDistrict::getState).collect(Collectors.toList());
    }
    
    public List<DistrictData> getDistrictsByState(String state) {
        return districtsByState.getOrDefault(state, Collections.emptyList());
    }
    
    public Optional<DistrictData> getDistrictById(Long id) {
        return Optional.ofNullable(districtsById.get(id));
    }
    
    public Optional<DistrictData> getDistrictByName(String name) {
        return Optional.ofNullable(districtsByName.get(name.toLowerCase()));
    }
    
    public List<DistrictData> getAllDistricts() {
        return new ArrayList<>(districtsById.values());
    }
    
    public List<StateDistrict> getAllStateDistricts() {
        return new ArrayList<>(stateDistricts);
    }
    
    public DistrictDto mapToDistrictDto(DistrictData districtData) {
        return DistrictDto.builder()
                .id(districtData.getSerialNumber())
                .name(districtData.getName())
                .state(districtData.getState())
                .city(districtData.getName())
                .pincode(districtData.getPinCode())
                .active(true)
                .build();
    }
    
    public static class StateDistrict {
        private String state;
        private String stateCode;
        private List<DistrictData> districts;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getStateCode() {
            return stateCode;
        }

        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }

        public List<DistrictData> getDistricts() {
            return districts;
        }

        public void setDistricts(List<DistrictData> districts) {
            this.districts = districts;
        }
    }
    
    public static class DistrictData {
        private Long serialNumber;
        private String name;
        private String pinCode;
        private String state;
        private String stateCode;
        
        public Long getSerialNumber() {
            return serialNumber;
        }

        public void setSerialNumber(Long serialNumber) {
            this.serialNumber = serialNumber;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPinCode() {
            return pinCode;
        }

        public void setPinCode(String pinCode) {
            this.pinCode = pinCode;
        }
        
        public String getState() {
            return state;
        }
        
        public void setState(String state) {
            this.state = state;
        }
        
        public String getStateCode() {
            return stateCode;
        }
        
        public void setStateCode(String stateCode) {
            this.stateCode = stateCode;
        }
    }
} 
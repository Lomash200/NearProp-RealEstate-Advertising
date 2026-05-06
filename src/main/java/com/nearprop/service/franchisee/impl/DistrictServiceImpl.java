package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.franchisee.CreateDistrictDto;
import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.entity.District;
import com.nearprop.entity.FranchiseeDistrict;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.DistrictRepository;
import com.nearprop.repository.franchisee.FranchiseeDistrictRepository;
import com.nearprop.service.franchisee.DistrictService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DistrictServiceImpl implements DistrictService {

    private final DistrictRepository districtRepository;
    private final FranchiseeDistrictRepository franchiseeDistrictRepository;

    @Override
    @Transactional
    public DistrictDto createDistrict(CreateDistrictDto createDistrictDto) {
        District district = District.builder()
                .name(createDistrictDto.getName())
                .state(createDistrictDto.getState())
                .city(createDistrictDto.getCity())
                .pincode(createDistrictDto.getPincode())
                .revenueSharePercentage(createDistrictDto.getRevenueSharePercentage())
                .latitude(createDistrictDto.getLatitude())
                .longitude(createDistrictDto.getLongitude())
                .radiusKm(createDistrictDto.getRadiusKm())
                .active(true)
                .build();
        
        District savedDistrict = districtRepository.save(district);
        return mapToDto(savedDistrict);
    }

    @Override
    @Transactional(readOnly = true)
    public DistrictDto getDistrict(Long districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + districtId));
        return mapToDto(district);
    }

    @Override
    @Transactional
    public DistrictDto updateDistrict(Long districtId, CreateDistrictDto updateDistrictDto) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + districtId));
        
        district.setName(updateDistrictDto.getName());
        district.setState(updateDistrictDto.getState());
        district.setCity(updateDistrictDto.getCity());
        district.setPincode(updateDistrictDto.getPincode());
        district.setRevenueSharePercentage(updateDistrictDto.getRevenueSharePercentage());
        district.setLatitude(updateDistrictDto.getLatitude());
        district.setLongitude(updateDistrictDto.getLongitude());
        district.setRadiusKm(updateDistrictDto.getRadiusKm());
        
        District updatedDistrict = districtRepository.save(district);
        return mapToDto(updatedDistrict);
    }

    @Override
    @Transactional
    public void deleteDistrict(Long districtId) {
        District district = districtRepository.findById(districtId)
                .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + districtId));
        
        // Check if district has any franchisee associations before deletion
        if (!franchiseeDistrictRepository.findByDistrictId(districtId).isEmpty()) {
            throw new IllegalStateException("Cannot delete district that is assigned to franchisees");
        }
        
        districtRepository.delete(district);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DistrictDto> getAllDistricts(Pageable pageable) {
        return districtRepository.findAll(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> getActiveDistricts() {
        return districtRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> getDistrictsByCity(String city) {
        return districtRepository.findByCity(city).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> getDistrictsByState(String state) {
        return districtRepository.findByState(state).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllStates() {
        return districtRepository.findAll().stream()
                .map(District::getState)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> findNearbyDistricts(Double latitude, Double longitude, Double radiusKm) {
        // For simplicity, just return all districts for now
        // In a real implementation, this would use geospatial queries
        return districtRepository.findByActiveTrue().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DistrictDto> getDistrictsByFranchiseeId(Long franchiseeId) {
        if (franchiseeId == null) {
            return new ArrayList<>();
        }
        
        try {
            List<FranchiseeDistrict> franchiseeDistricts = franchiseeDistrictRepository.findByUserId(franchiseeId);
            if (franchiseeDistricts == null || franchiseeDistricts.isEmpty()) {
                return new ArrayList<>();
            }
            
            List<Long> districtIds = franchiseeDistricts.stream()
                    .map(FranchiseeDistrict::getDistrictId)
                    .collect(Collectors.toList());
                    
            if (districtIds.isEmpty()) {
                return new ArrayList<>();
            }
            
            return districtRepository.findAllById(districtIds).stream()
                    .map(this::mapToDto)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            // Log the error but return an empty list instead of throwing an exception
            System.err.println("Error retrieving districts for franchisee ID " + franchiseeId + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private DistrictDto mapToDto(District district) {
        return DistrictDto.builder()
                .id(district.getId())
                .name(district.getName())
                .state(district.getState())
                .city(district.getCity())
                .pincode(district.getPincode())
                .revenueSharePercentage(district.getRevenueSharePercentage())
                .latitude(district.getLatitude())
                .longitude(district.getLongitude())
                .radiusKm(district.getRadiusKm())
                .active(district.isActive())
                .build();
    }
} 
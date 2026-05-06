package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.CreateDistrictDto;
import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.service.franchisee.DistrictService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/franchisee/districts")
@RequiredArgsConstructor
public class DistrictController {
    
    private final DistrictService districtService;
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictDto> createDistrict(@Valid @RequestBody CreateDistrictDto createDistrictDto) {
        DistrictDto district = districtService.createDistrict(createDistrictDto);
        return new ResponseEntity<>(district, HttpStatus.CREATED);
    }
    
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<DistrictDto> getDistrict(@PathVariable("id") Long districtId) {
        DistrictDto district = districtService.getDistrict(districtId);
        return ResponseEntity.ok(district);
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DistrictDto> updateDistrict(
            @PathVariable("id") Long districtId,
            @Valid @RequestBody CreateDistrictDto updateDistrictDto) {
        DistrictDto district = districtService.updateDistrict(districtId, updateDistrictDto);
        return ResponseEntity.ok(district);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDistrict(@PathVariable("id") Long districtId) {
        districtService.deleteDistrict(districtId);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<Page<DistrictDto>> getAllDistricts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<DistrictDto> districts = districtService.getAllDistricts(pageable);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/active")
    public ResponseEntity<List<DistrictDto>> getActiveDistricts() {
        List<DistrictDto> districts = districtService.getActiveDistricts();
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/by-city/{city}")
    public ResponseEntity<List<DistrictDto>> getDistrictsByCity(@PathVariable String city) {
        List<DistrictDto> districts = districtService.getDistrictsByCity(city);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<DistrictDto>> getDistrictsByState(@PathVariable String state) {
        List<DistrictDto> districts = districtService.getDistrictsByState(state);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/nearby")
    public ResponseEntity<List<DistrictDto>> findNearbyDistricts(
            @RequestParam Double latitude,
            @RequestParam Double longitude,
            @RequestParam(defaultValue = "10.0") Double radiusKm) {
        List<DistrictDto> districts = districtService.findNearbyDistricts(latitude, longitude, radiusKm);
        return ResponseEntity.ok(districts);
    }
    
    @GetMapping("/by-franchisee/{franchiseeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRANCHISEE')")
    public ResponseEntity<List<DistrictDto>> getDistrictsByFranchiseeId(@PathVariable Long franchiseeId) {
        List<DistrictDto> districts = districtService.getDistrictsByFranchiseeId(franchiseeId);
        return ResponseEntity.ok(districts);
    }
} 
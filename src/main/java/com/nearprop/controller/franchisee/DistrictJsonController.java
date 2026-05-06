package com.nearprop.controller.franchisee;

import com.nearprop.dto.franchisee.DistrictDto;
import com.nearprop.service.franchisee.DistrictJsonService;
import com.nearprop.service.franchisee.FranchiseRequestService;
import com.nearprop.entity.District;
import com.nearprop.repository.DistrictRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.Optional;

@RestController
@RequestMapping("/districts")
@RequiredArgsConstructor
public class DistrictJsonController {
    
    private final DistrictJsonService districtJsonService;
    private final FranchiseRequestService franchiseRequestService;
    private final DistrictRepository districtRepository;
    
    @GetMapping
    public ResponseEntity<List<DistrictDto>> getAllDistricts() {
        return ResponseEntity.ok(districtJsonService.getAllDistricts().stream()
                .map(this::enrichDistrictDto)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/states")
    public ResponseEntity<List<String>> getAllStates() {
        return ResponseEntity.ok(districtJsonService.getAllStates());
    }
    
    @GetMapping("/by-state/{state}")
    public ResponseEntity<List<DistrictDto>> getDistrictsByState(@PathVariable String state) {
        return ResponseEntity.ok(districtJsonService.getDistrictsByState(state).stream()
                .map(this::enrichDistrictDto)
                .collect(Collectors.toList()));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<DistrictDto> getDistrictById(@PathVariable Long id) {
        return districtJsonService.getDistrictById(id)
                .map(this::enrichDistrictDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/by-name/{name}")
    public ResponseEntity<DistrictDto> getDistrictByName(@PathVariable String name) {
        return districtJsonService.getDistrictByName(name)
                .map(this::enrichDistrictDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/assigned")
    public ResponseEntity<Map<Long, Boolean>> getAssignedDistrictStatus(
            @RequestParam(required = false) List<Long> districtIds) {
        
        List<Long> ids = districtIds;
        if (ids == null || ids.isEmpty()) {
            ids = districtJsonService.getAllDistricts().stream()
                    .map(DistrictJsonService.DistrictData::getSerialNumber)
                    .collect(Collectors.toList());
        }
        
        return ResponseEntity.ok(ids.stream()
                .collect(Collectors.toMap(
                        id -> id,
                        franchiseRequestService::isDistrictAssigned
                )));
    }
    
    @GetMapping("/test/{id}")
    public ResponseEntity<String> testDistrictExists(@PathVariable Long id) {
        boolean exists = districtRepository.existsById(id);
        return ResponseEntity.ok("District with ID " + id + " exists: " + exists);
    }
    
    private DistrictDto enrichDistrictDto(DistrictJsonService.DistrictData districtData) {
        DistrictDto dto = districtJsonService.mapToDistrictDto(districtData);
        boolean isAssigned = franchiseRequestService.isDistrictAssigned(districtData.getSerialNumber());
        dto.setHasFranchisee(isAssigned);
        return dto;
    }
} 
package com.nearprop.service.impl;

import com.nearprop.dto.PropertyInquiryDto;
import com.nearprop.entity.PropertyInquiry;
import com.nearprop.entity.PropertyInquiryStatusHistory;
import com.nearprop.repository.PropertyInquiryRepository;
import com.nearprop.repository.PropertyInquiryStatusHistoryRepository;
import com.nearprop.service.PropertyInquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PropertyInquiryServiceImpl implements PropertyInquiryService {
    private final PropertyInquiryRepository inquiryRepository;
    private final PropertyInquiryStatusHistoryRepository statusHistoryRepository;

    @Override
    @Transactional
    public PropertyInquiryDto createInquiry(PropertyInquiryDto dto) {
        PropertyInquiry inquiry = mapToEntity(dto);
        inquiry.setStatus(PropertyInquiry.InquiryStatus.IN_REVIEW);
        PropertyInquiry saved = inquiryRepository.save(inquiry);
        // Add initial status history
        PropertyInquiryStatusHistory history = PropertyInquiryStatusHistory.builder()
                .inquiry(saved)
                .status(saved.getStatus())
                .comment("Inquiry created")
                .updatedBy(null)
                .build();
        statusHistoryRepository.save(history);
        return mapToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyInquiryDto> getAllInquiriesForAdmin() {
        return inquiryRepository.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyInquiryDto> getAllInquiriesForFranchise(Long districtId) {
        return inquiryRepository.findByDistrictId(districtId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PropertyInquiryDto updateInquiryStatus(Long inquiryId, PropertyInquiryDto.StatusHistoryDto statusDto, Long updatedBy) {
        PropertyInquiry inquiry = inquiryRepository.findById(inquiryId).orElseThrow();
        inquiry.setStatus(statusDto.getStatus());
        inquiryRepository.save(inquiry);
        PropertyInquiryStatusHistory history = PropertyInquiryStatusHistory.builder()
                .inquiry(inquiry)
                .status(statusDto.getStatus())
                .comment(statusDto.getComment())
                .updatedBy(updatedBy)
                .build();
        statusHistoryRepository.save(history);
        return mapToDto(inquiry);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyInquiryDto getInquiry(Long id) {
        return inquiryRepository.findById(id).map(this::mapToDto).orElse(null);
    }

    private PropertyInquiryDto mapToDto(PropertyInquiry entity) {
        PropertyInquiryDto dto = new PropertyInquiryDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setEmail(entity.getEmail());
        dto.setMobileNumber(entity.getMobileNumber());
        dto.setInfoType(entity.getInfoType());
        dto.setStatus(entity.getStatus());
        dto.setPropertyType(entity.getPropertyType() != null ? entity.getPropertyType().name() : null);
        dto.setMaxPrice(entity.getMaxPrice());
        dto.setBedrooms(entity.getBedrooms());
        dto.setBathrooms(entity.getBathrooms());
        dto.setMinSize(entity.getMinSize());
        dto.setState(entity.getState());
        dto.setCity(entity.getCity());
        dto.setArea(entity.getArea());
        dto.setZipCode(entity.getZipCode());
        dto.setDistrictId(entity.getDistrictId());
        dto.setLatitude(entity.getLatitude());
        dto.setLongitude(entity.getLongitude());
        dto.setMessage(entity.getMessage());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setLastUpdatedAt(entity.getLastUpdatedAt());
        if (entity.getStatusHistory() != null) {
            dto.setStatusHistory(entity.getStatusHistory().stream().map(h -> {
                PropertyInquiryDto.StatusHistoryDto sh = new PropertyInquiryDto.StatusHistoryDto();
                sh.setStatus(h.getStatus());
                sh.setComment(h.getComment());
                sh.setUpdatedBy(h.getUpdatedBy());
                sh.setUpdatedAt(h.getUpdatedAt());
                return sh;
            }).collect(Collectors.toList()));
        }
        return dto;
    }

    private PropertyInquiry mapToEntity(PropertyInquiryDto dto) {
        PropertyInquiry entity = new PropertyInquiry();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setEmail(dto.getEmail());
        entity.setMobileNumber(dto.getMobileNumber());
        entity.setInfoType(dto.getInfoType());
        if (dto.getPropertyType() != null) {
            try {
                entity.setPropertyType(com.nearprop.entity.PropertyType.valueOf(dto.getPropertyType()));
            } catch (Exception e) {
                entity.setPropertyType(null);
            }
        }
        entity.setMaxPrice(dto.getMaxPrice());
        entity.setBedrooms(dto.getBedrooms());
        entity.setBathrooms(dto.getBathrooms());
        entity.setMinSize(dto.getMinSize());
        entity.setState(dto.getState());
        entity.setCity(dto.getCity());
        entity.setArea(dto.getArea());
        entity.setZipCode(dto.getZipCode());
        entity.setDistrictId(dto.getDistrictId());
        entity.setLatitude(dto.getLatitude());
        entity.setLongitude(dto.getLongitude());
        entity.setMessage(dto.getMessage());
        entity.setStatus(dto.getStatus());
        return entity;
    }
} 
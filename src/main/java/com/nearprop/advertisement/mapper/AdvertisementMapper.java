package com.nearprop.advertisement.mapper;

import com.nearprop.advertisement.dto.AdvertisementDto;
import com.nearprop.advertisement.dto.CreateAdvertisementDto;
import com.nearprop.advertisement.entity.Advertisement;
import com.nearprop.dto.DistrictDto;
import com.nearprop.entity.District;
import com.nearprop.entity.User;
import com.nearprop.mapper.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class AdvertisementMapper {
    
    private final UserMapper userMapper;
    
    public AdvertisementMapper(UserMapper userMapper) {
        this.userMapper = userMapper;
    }
    
    public Advertisement toEntity(CreateAdvertisementDto dto, District district, Set<District> targetDistricts, User createdBy) {
        log.debug("Converting CreateAdvertisementDto to Advertisement entity");
        Advertisement advertisement = new Advertisement();
        BeanUtils.copyProperties(dto, advertisement);
        
        // Set primary district
        advertisement.setDistrict(district);
        advertisement.setDistrictName(district != null ? district.getName() : dto.getDistrictName());
        
        // Set target districts
        if (targetDistricts != null && !targetDistricts.isEmpty()) {
            advertisement.setTargetDistricts(targetDistricts);
        }
        
        // Set creator and active status
        advertisement.setCreatedBy(createdBy);
        advertisement.setActive(true);
        
        // Initialize analytics counters
        advertisement.setViewCount(0L);
        advertisement.setClickCount(0L);
        advertisement.setWhatsappClicks(0L);
        advertisement.setPhoneClicks(0L);
        advertisement.setWebsiteClicks(0L);
        advertisement.setSocialMediaClicks(0L);
        
        // Initialize notification flags
        advertisement.setDayBeforeNotificationSent(false);
        advertisement.setHoursBeforeNotificationSent(false);
        advertisement.setExpiryNotificationSent(false);
        
        log.debug("Advertisement entity created: {}", advertisement.getTitle());
        return advertisement;
    }
    
    public AdvertisementDto toDto(Advertisement advertisement) {
        if (advertisement == null) {
            log.debug("Advertisement is null, returning null DTO");
            return null;
        }
        
        log.debug("Converting Advertisement entity to AdvertisementDto: {}", advertisement.getId());
        AdvertisementDto dto = new AdvertisementDto();
        BeanUtils.copyProperties(advertisement, dto);
        
        // Set district ID
        if (advertisement.getDistrict() != null) {
            dto.setDistrictId(advertisement.getDistrict().getId());
        }
        
        // Map target districts
        if (advertisement.getTargetDistricts() != null && !advertisement.getTargetDistricts().isEmpty()) {
            Set<DistrictDto> targetDistrictDtos = advertisement.getTargetDistricts().stream()
                    .map(this::mapDistrictToDto)
                    .collect(Collectors.toSet());
            dto.setTargetDistricts(targetDistrictDtos);
        } else {
            dto.setTargetDistricts(new HashSet<>());
        }
        
        // Map creator
        if (advertisement.getCreatedBy() != null) {
            dto.setCreatedBy(userMapper.toDto(advertisement.getCreatedBy()));
        }
        
        return dto;
    }
    
    public void updateEntityFromDto(CreateAdvertisementDto dto, Advertisement advertisement, District district, Set<District> targetDistricts) {
        log.debug("Updating Advertisement entity from DTO: {}", advertisement.getId());
        
        // Update basic fields
        advertisement.setTitle(dto.getTitle());
        advertisement.setDescription(dto.getDescription());
        advertisement.setBannerImageUrl(dto.getBannerImageUrl());
        advertisement.setVideoUrl(dto.getVideoUrl());
        advertisement.setWebsiteUrl(dto.getWebsiteUrl());
        advertisement.setWhatsappNumber(dto.getWhatsappNumber());
        advertisement.setPhoneNumber(dto.getPhoneNumber());
        advertisement.setEmailAddress(dto.getEmailAddress());
        advertisement.setInstagramUrl(dto.getInstagramUrl());
        advertisement.setFacebookUrl(dto.getFacebookUrl());
        advertisement.setYoutubeUrl(dto.getYoutubeUrl());
        advertisement.setTwitterUrl(dto.getTwitterUrl());
        advertisement.setLinkedinUrl(dto.getLinkedinUrl());
        advertisement.setAdditionalInfo(dto.getAdditionalInfo());
        advertisement.setTargetLocation(dto.getTargetLocation());
        advertisement.setLatitude(dto.getLatitude());
        advertisement.setLongitude(dto.getLongitude());
        advertisement.setRadiusKm(dto.getRadiusKm());
        
        // Update primary district
        if (district != null) {
            advertisement.setDistrict(district);
            advertisement.setDistrictName(district.getName());
        } else {
            advertisement.setDistrictName(dto.getDistrictName());
        }
        
        // Update target districts
        if (targetDistricts != null && !targetDistricts.isEmpty()) {
            advertisement.setTargetDistricts(targetDistricts);
        }
        
        // Update validity dates
        advertisement.setValidFrom(dto.getValidFrom());
        advertisement.setValidUntil(dto.getValidUntil());
        
        // Reset notification flags when dates are updated
        advertisement.setDayBeforeNotificationSent(false);
        advertisement.setHoursBeforeNotificationSent(false);
        advertisement.setExpiryNotificationSent(false);
        
        log.debug("Advertisement entity updated successfully");
    }
    
    private DistrictDto mapDistrictToDto(District district) {
        if (district == null) {
            return null;
        }
        
        DistrictDto dto = new DistrictDto();
        dto.setId(district.getId());
        dto.setName(district.getName());
        dto.setState(district.getState());
        dto.setCity(district.getCity());
        dto.setPincode(district.getPincode());
        dto.setLatitude(district.getLatitude());
        dto.setLongitude(district.getLongitude());
        dto.setRadiusKm(district.getRadiusKm());
        
        return dto;
    }
} 
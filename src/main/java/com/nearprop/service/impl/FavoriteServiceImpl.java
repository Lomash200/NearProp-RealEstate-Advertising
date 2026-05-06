package com.nearprop.service.impl;

import com.nearprop.dto.FavoriteResponseDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.User;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;

    @Override
    @Transactional
    public FavoriteResponseDto addToFavorites(Long propertyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        user.getFavorites().add(property);
        userRepository.save(user);
        
        return FavoriteResponseDto.builder()
                .propertyId(propertyId)
                .userId(userId)
                .isFavorite(true)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional
    public FavoriteResponseDto removeFromFavorites(Long propertyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
                
        user.getFavorites().remove(property);
        userRepository.save(user);
        
        return FavoriteResponseDto.builder()
                .propertyId(propertyId)
                .userId(userId)
                .isFavorite(false)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isFavorite(Long propertyId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        return user.getFavorites().stream()
                .anyMatch(property -> property.getId().equals(propertyId));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getUserFavorites(Long userId, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        Page<Property> favoritesPage = propertyRepository.findByIdIn(
                user.getFavorites().stream().map(Property::getId).collect(Collectors.toSet()),
                pageable
        );
        
        return favoritesPage.map(this::mapToDto);
    }
    
    private PropertyDto mapToDto(Property property) {
        return PropertyDto.builder()
                .id(property.getId())
                .title(property.getTitle())
                .description(property.getDescription())
                .type(property.getType())
                .price(property.getPrice())
                .area(property.getArea())
                .address(property.getAddress())
                .districtId(property.getDistrict() != null ? property.getDistrict().getId() : null)
                .districtName(property.getDistrictName())
                .city(property.getCity())
                .state(property.getState())
                .pincode(property.getPincode())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .status(property.getStatus())
                .amenities(property.getAmenities())
                .imageUrls(property.getImages())
                .owner(mapToUserSummaryDto(property.getOwner()))
                .featured(property.isFeatured())
                .approved(property.isApproved())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .build();
    }
    
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
} 
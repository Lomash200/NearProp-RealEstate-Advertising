package com.nearprop.service.franchisee.impl;

import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.entity.Subscription;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.exception.EntityNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.service.PropertyService;
import com.nearprop.service.franchisee.FranchiseePropertyService;
import com.nearprop.service.impl.PropertyServiceImpl;
import com.nearprop.mapper.PropertyMapper;
import com.nearprop.service.SubscriptionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of FranchiseePropertyService
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class FranchiseePropertyServiceImpl implements FranchiseePropertyService {

    private final UserRepository userRepository;
    private final PropertyRepository propertyRepository;
    private final PropertyService propertyService;
    private final PropertyMapper propertyMapper;
    private final SubscriptionService subscriptionService;

    @Override
    @Transactional
    public PropertyDto createPropertyOnBehalf(String ownerPermanentId, CreatePropertyDto propertyDto, Long franchiseeId) {
        User owner = getUserByPermanentId(ownerPermanentId);
        User franchisee = validateFranchisee(franchiseeId);
        
        // Create property using the existing service
        PropertyDto createdProperty = propertyService.createProperty(propertyDto, owner.getId());
        
        // Update the property to mark it as added by franchisee
        Property property = propertyRepository.findById(createdProperty.getId())
            .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + createdProperty.getId()));
        
        property.setAddedByUser(franchisee);
        property.setAddedByFranchisee(true);
        property.setOwnerPermanentId(ownerPermanentId);
        
        // Check if owner has an active subscription with available property slots
        // If yes, automatically activate the property
        Optional<Subscription> availableSubscription = subscriptionService.getSubscriptionWithAvailablePropertySlots(owner.getId(), PlanType.PROPERTY);
        if (availableSubscription.isPresent()) {
            Subscription subscription = availableSubscription.get();
            property.setActive(true);
            property.setApproved(true); // Auto-approve when subscription is available
            property.setSubscriptionExpiry(subscription.getEndDate());
            property.setSubscriptionId(subscription.getId());
            log.info("Property automatically activated with owner's subscription ID: {} with expiry date: {}", 
                    subscription.getId(), subscription.getEndDate());
        }
        
        // Permanent ID is already set by the property service
        log.info("Property permanent ID: {}", property.getPermanentId());
        
        propertyRepository.save(property);
        
        log.info("Franchisee {} created property {} (permanent ID: {}) on behalf of user with permanent ID: {}", 
            franchisee.getId(), property.getId(), property.getPermanentId(), ownerPermanentId);
        
        return propertyMapper.toDto(property);
    }

    @Override
    @Transactional
    public PropertyDto createPropertyFormOnBehalf(
            String ownerPermanentId,
            PropertyFormDto propertyFormDto,
            List<MultipartFile> images,
            MultipartFile videoFile,
            Long franchiseeId) {
        
        User owner = getUserByPermanentId(ownerPermanentId);
        User franchisee = validateFranchisee(franchiseeId);
        
        // Create property using the existing service
        PropertyDto createdProperty = propertyService.createPropertyFromForm(
            propertyFormDto, images, videoFile, owner.getId());
        
        // Update the property to mark it as added by franchisee
        Property property = propertyRepository.findById(createdProperty.getId())
            .orElseThrow(() -> new EntityNotFoundException("Property not found with id: " + createdProperty.getId()));
        
        property.setAddedByUser(franchisee);
        property.setAddedByFranchisee(true);
        property.setOwnerPermanentId(ownerPermanentId);
        
        // Check if owner has an active subscription with available property slots
        // If yes, automatically activate the property
        Optional<Subscription> availableSubscription = subscriptionService.getSubscriptionWithAvailablePropertySlots(owner.getId(), PlanType.PROPERTY);
        if (availableSubscription.isPresent()) {
            Subscription subscription = availableSubscription.get();
            property.setActive(true);
            property.setApproved(true); // Auto-approve when subscription is available
            property.setSubscriptionExpiry(subscription.getEndDate());
            property.setSubscriptionId(subscription.getId());
            log.info("Property automatically activated with owner's subscription ID: {} with expiry date: {}", 
                    subscription.getId(), subscription.getEndDate());
        }
        
        // Permanent ID is already set by the property service
        log.info("Property permanent ID: {}", property.getPermanentId());
        
        propertyRepository.save(property);
        
        log.info("Franchisee {} created property {} (permanent ID: {}) via form on behalf of user with permanent ID: {}", 
            franchisee.getId(), property.getId(), property.getPermanentId(), ownerPermanentId);
        
        return propertyMapper.toDto(property);
    }

    @Override
    public boolean validateUserId(String permanentId) {
        return userRepository.existsByPermanentId(permanentId);
    }
    
    /**
     * Get a user by their permanent ID
     * @param permanentId The permanent ID
     * @return The user
     */
    private User getUserByPermanentId(String permanentId) {
        return userRepository.findByPermanentId(permanentId)
            .orElseThrow(() -> new EntityNotFoundException("User not found with permanent ID: " + permanentId));
    }
    
    /**
     * Validate that a user is a franchisee
     * @param franchiseeId The franchisee ID
     * @return The franchisee user
     */
    private User validateFranchisee(Long franchiseeId) {
        User franchisee = userRepository.findById(franchiseeId)
            .orElseThrow(() -> new EntityNotFoundException("Franchisee not found with ID: " + franchiseeId));
        
        if (!franchisee.getRoles().contains(Role.FRANCHISEE)) {
            throw new UnauthorizedException("User is not a franchisee");
        }
        
        return franchisee;
    }
} 
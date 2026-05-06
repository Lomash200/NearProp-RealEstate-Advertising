package com.nearprop.service.impl;

import com.nearprop.dto.CreatePropertyDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.PropertyFormDto;
import com.nearprop.dto.DeveloperPropertyFormDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.*;
import com.nearprop.entity.Subscription.SubscriptionStatus;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.exception.BadRequestException;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.DistrictRepository;
import com.nearprop.repository.ReelRepository;
import com.nearprop.repository.SubscriptionRepository;
import com.nearprop.service.PropertyEmailService;
import com.nearprop.service.PropertyService;
import com.nearprop.service.SubscriptionService;
import com.nearprop.util.IdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;

import java.util.HashSet;
import java.util.HashMap;
import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;

import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.client.RestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import com.nearprop.entity.SubscriptionPlan.PlanType;
import com.nearprop.service.NotificationService;


@Service
@RequiredArgsConstructor
public class PropertyServiceImpl implements PropertyService {

    private final JdbcTemplate jdbc;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;
    private final DistrictRepository districtRepository;
    private final ReelRepository reelRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionService subscriptionService;
    private final com.nearprop.service.S3Service s3Service;
    private final PropertyEmailService propertyEmailService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Random random = new Random();
    private static final Logger log = LoggerFactory.getLogger(PropertyServiceImpl.class);
    private final NotificationService notificationService;


    @Value("${google.maps.api.key}")
    private String googleMapsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // Removed images_data.json loading as it's not needed
        // The application will work fine without this file
    }

    private List<String> getRandomImagesForProperty(PropertyType type, int count) {
        // Return empty list since we're not using images_data.json
        return new ArrayList<>();
    }


    @Override
    @Transactional
    public void deletePropertybySubAdmin(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        System.out.println(property);
        propertyRepository.delete(property);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"properties", "user-properties", "property-search"}, allEntries = true)
    public PropertyDto createProperty(CreatePropertyDto propertyDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate that the district exists
        District district = null;
        if (propertyDto.getDistrictId() != null) {
            log.info("Looking for district with ID: {}", propertyDto.getDistrictId());
            district = districtRepository.findById(propertyDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyDto.getDistrictId()));
            log.info("Found district: {}", district.getName());
        } else {
            throw new IllegalArgumentException("District ID is required");
        }

        log.info("Creating property with district: {}", district.getName());
        
        // Check if user has an active subscription with available property slots
        Optional<Subscription> availableSubscription = Optional.empty();
        
        // Try all plan types that can have properties
        for (PlanType planType : new PlanType[] {
                PlanType.PROPERTY, 
                PlanType.ADVISOR, 
                PlanType.SELLER, 
                PlanType.DEVELOPER, 
                PlanType.FRANCHISEE
            }) {
            availableSubscription = subscriptionService.getSubscriptionWithAvailablePropertySlots(userId, planType);
            if (availableSubscription.isPresent()) {
                log.info("Found active subscription of type {} for user {} with available slots", 
                    planType, userId);
                break;
            }
        }
        
        boolean hasActiveSubscription = false;
        if (availableSubscription.isEmpty()) {
            // Check if user has any active subscription
            for (PlanType planType : new PlanType[] {
                    PlanType.PROPERTY, 
                    PlanType.ADVISOR, 
                    PlanType.SELLER, 
                    PlanType.DEVELOPER, 
                    PlanType.FRANCHISEE
                }) {
                hasActiveSubscription = subscriptionService.hasActiveSubscription(userId, planType);
                if (hasActiveSubscription) break;
            }
            
            if (hasActiveSubscription) {
                // User has a subscription but has reached the property limit
                throw new BadRequestException("You have reached the maximum number of properties allowed by your subscription plan. Please upgrade your plan or purchase a new subscription.");
            } else {
                // User has no active subscription
                log.info("User has no active subscription. Property will be created as inactive.");
            }
        } else {
            hasActiveSubscription = true;
        }
        
        // Generate permanent ID for the property
        String permanentId = IdGenerator.generatePropertyId();
        
        Property property = Property.builder()
                .title(propertyDto.getTitle())
                .description(propertyDto.getDescription())
                .type(propertyDto.getType())
                .status(propertyDto.getStatus())
                .label(propertyDto.getLabel())
                .price(propertyDto.getPrice())
                .area(propertyDto.getArea())
                .sizePostfix(propertyDto.getSizePostfix())
                .landArea(propertyDto.getLandArea())
                .landAreaPostfix(propertyDto.getLandAreaPostfix())
                .address(propertyDto.getAddress())
                .city(propertyDto.getCity() != null ? propertyDto.getCity() : district.getCity())
                .state(propertyDto.getState() != null ? propertyDto.getState() : district.getState())
                .pincode(propertyDto.getPincode() != null ? propertyDto.getPincode() : district.getPincode())
                .streetNumber(propertyDto.getStreetNumber())
                .placeName(propertyDto.getPlaceName())
                .bedrooms(propertyDto.getBedrooms())
                .bathrooms(propertyDto.getBathrooms())
                .garages(propertyDto.getGarages())
                .garageSize(propertyDto.getGarageSize())
                .yearBuilt(propertyDto.getYearBuilt())
                .availability(propertyDto.getAvailability())
                .renovated(propertyDto.getRenovated())
                .videoUrl(propertyDto.getVideoUrl())
                .youtubeUrl(propertyDto.getYoutubeUrl())
                .latitude(propertyDto.getLatitude())
                .longitude(propertyDto.getLongitude())
                .note(propertyDto.getNote())
                .privateNote(propertyDto.getPrivateNote())
                .agreementAccepted(propertyDto.getAgreementAccepted())
                .amenities(propertyDto.getAmenities() != null ? propertyDto.getAmenities() : new HashSet<>())
                .securityFeatures(propertyDto.getSecurityFeatures() != null ? propertyDto.getSecurityFeatures() : new HashSet<>())
                .luxuriousFeatures(propertyDto.getLuxuriousFeatures() != null ? propertyDto.getLuxuriousFeatures() : new HashSet<>())
                .features(propertyDto.getFeatures() != null ? propertyDto.getFeatures() : new HashSet<>())
                .images(propertyDto.getImages() != null ? propertyDto.getImages() : new ArrayList<>())
                .additionalDetails(propertyDto.getAdditionalDetails() != null ? propertyDto.getAdditionalDetails() : new HashMap<>())
                .owner(user)
                .ownerPermanentId(user.getPermanentId())
                .featured(false)
                .approved(false)
                .district(district)
                .districtValue(district.getName())
                .districtName(district.getName())
                .permanentId(permanentId)
                .build();

        log.info("Property built with district: {}", property.getDistrict() != null ? property.getDistrict().getName() : "null");
        log.info("Property built with districtValue: {}", property.getDistrictValue());
        
        // If user has an available subscription, assign it to the property
        if (availableSubscription.isPresent()) {
            Subscription subscription = availableSubscription.get();
            property.setActive(true);
            property.setApproved(true); // Auto-approve when subscription is available
            property.setSubscriptionExpiry(subscription.getEndDate());
            property.setSubscriptionId(subscription.getId());
            log.info("Property assigned to subscription ID: {} with expiry date: {}", 
                    subscription.getId(), subscription.getEndDate());
        } else {
            // Property will be inactive until a subscription is purchased
            property.setActive(false);
            property.setApproved(true); // Auto-approve even without subscription
            log.info("Property created as inactive but approved. Needs subscription to be activated.");
        }
        
        Property savedProperty = propertyRepository.save(property);

        // 🔔 APP notification (DISTRICT WISE)
        notificationService.notifyPropertyAddedFiltered(
                savedProperty.getDistrict().getId(), // ✅ districtId
                savedProperty.getId()
        );



        // Send email notification
        try {
            propertyEmailService.sendPropertyCreatedNotification(savedProperty);
            log.info("Property creation notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property creation notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }

    @Override
    @Transactional
    @CacheEvict(value = {"properties", "user-properties", "property-search"}, allEntries = true)
    public PropertyDto updatePropertyFromFormBySubAdmin(Long propertyId, PropertyFormDto propertyFormDto,
                                                        List<MultipartFile> images, MultipartFile videoFile, Long subAdminId) {

        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        User subAdmin = userRepository.findById(subAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("SubAdmin not found with ID: " + subAdminId));

        // Update district if provided
        District district = null;
        if (propertyFormDto.getDistrictId() != null) {
            district = districtRepository.findById(propertyFormDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyFormDto.getDistrictId()));
            property.setDistrict(district);
            property.setDistrictValue(district.getName());
            property.setDistrictName(district.getName());

            if (propertyFormDto.getCity() == null) property.setCity(district.getCity());
            if (propertyFormDto.getState() == null) property.setState(district.getState());
            if (propertyFormDto.getPincode() == null) property.setPincode(district.getPincode());
        }

        // Update basic fields
        property.setTitle(propertyFormDto.getTitle());
        property.setDescription(propertyFormDto.getDescription());
        property.setType(propertyFormDto.getType());
        property.setStatus(propertyFormDto.getStatus());
        property.setLabel(propertyFormDto.getLabel());
        property.setPrice(propertyFormDto.getPrice());
        property.setArea(propertyFormDto.getArea());
        property.setSizePostfix(propertyFormDto.getSizePostfix());
        property.setLandArea(propertyFormDto.getLandArea());
        property.setLandAreaPostfix(propertyFormDto.getLandAreaPostfix());
        property.setAddress(propertyFormDto.getAddress());
        property.setCity(propertyFormDto.getCity());
        property.setState(propertyFormDto.getState());
        property.setPincode(propertyFormDto.getPincode());
        property.setStreetNumber(propertyFormDto.getStreetNumber());
        property.setPlaceName(propertyFormDto.getPlaceName());
        property.setBedrooms(propertyFormDto.getBedrooms());
        property.setBathrooms(propertyFormDto.getBathrooms());
        property.setGarages(propertyFormDto.getGarages());
        property.setGarageSize(propertyFormDto.getGarageSize());
        property.setYearBuilt(propertyFormDto.getYearBuilt());
        property.setAvailability(propertyFormDto.getAvailability());
        property.setRenovated(propertyFormDto.getRenovated());
        property.setLatitude(propertyFormDto.getLatitude());
        property.setLongitude(propertyFormDto.getLongitude());
        property.setNote(propertyFormDto.getNote());
        property.setPrivateNote(propertyFormDto.getPrivateNote());
        property.setAgreementAccepted(propertyFormDto.getAgreementAccepted());

        // Parse JSON collections
        try {
            if (propertyFormDto.getAmenities() != null && !propertyFormDto.getAmenities().isEmpty()) {
                Set<String> amenities = objectMapper.readValue(propertyFormDto.getAmenities(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setAmenities(amenities);
            }
            if (propertyFormDto.getSecurityFeatures() != null && !propertyFormDto.getSecurityFeatures().isEmpty()) {
                Set<String> securityFeatures = objectMapper.readValue(propertyFormDto.getSecurityFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setSecurityFeatures(securityFeatures);
            }
            if (propertyFormDto.getLuxuriousFeatures() != null && !propertyFormDto.getLuxuriousFeatures().isEmpty()) {
                Set<String> luxuriousFeatures = objectMapper.readValue(propertyFormDto.getLuxuriousFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setLuxuriousFeatures(luxuriousFeatures);
            }
            if (propertyFormDto.getFeatures() != null && !propertyFormDto.getFeatures().isEmpty()) {
                Set<String> features = objectMapper.readValue(propertyFormDto.getFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setFeatures(features);
            }
            if (propertyFormDto.getAdditionalDetails() != null && !propertyFormDto.getAdditionalDetails().isEmpty()) {
                Map<String, String> additionalDetails = objectMapper.readValue(propertyFormDto.getAdditionalDetails(),
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                property.setAdditionalDetails(additionalDetails);
            }
        } catch (Exception e) {
            log.error("Error parsing JSON fields: {}", e.getMessage());
        }

        // Handle new images (append to existing)
        if (images != null && !images.isEmpty()) {
            try {
                List<String> newImageUrls = s3Service.uploadPropertyImages(images, subAdmin, property);
                List<String> currentImages = property.getImages() != null ? property.getImages() : new ArrayList<>();
                currentImages.addAll(newImageUrls);
                property.setImages(currentImages);
            } catch (Exception e) {
                log.error("Error uploading images: {}", e.getMessage());
            }
        }

        // Handle new video
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String videoUrl = s3Service.uploadPropertyVideo(videoFile, subAdmin, property);
                property.setVideoUrl(videoUrl);
            } catch (Exception e) {
                log.error("Error uploading video: {}", e.getMessage());
            }
        }

        // For subadmin: keep  approved
        property.setApproved(true);

        Property updatedProperty = propertyRepository.save(property);
        return mapToDto(updatedProperty);
    }


    @Override
    @Transactional
    @CacheEvict(value = {"properties", "user-properties", "property-search"}, allEntries = true)
    public PropertyDto updateProperty(Long propertyId, CreatePropertyDto propertyDto, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        if (!property.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        // Update district if districtId is provided
        District district = null;
        if (propertyDto.getDistrictId() != null) {
            district = districtRepository.findById(propertyDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyDto.getDistrictId()));
            property.setDistrict(district);
            property.setDistrictValue(district.getName()); // Set district value for the column
            property.setDistrictName(district.getName());
            
            // If city/state/pincode aren't explicitly provided, use district values
            if (propertyDto.getCity() == null) {
                property.setCity(district.getCity());
            }
            
            if (propertyDto.getState() == null) {
                property.setState(district.getState());
            }
            
            if (propertyDto.getPincode() == null) {
                property.setPincode(district.getPincode());
            }
        }
        
        property.setTitle(propertyDto.getTitle());
        property.setDescription(propertyDto.getDescription());
        property.setType(propertyDto.getType());
        property.setStatus(propertyDto.getStatus());
        property.setLabel(propertyDto.getLabel());
        property.setPrice(propertyDto.getPrice());
        property.setArea(propertyDto.getArea());
        property.setSizePostfix(propertyDto.getSizePostfix());
        property.setLandArea(propertyDto.getLandArea());
        property.setLandAreaPostfix(propertyDto.getLandAreaPostfix());
        property.setAddress(propertyDto.getAddress());
        
        if (propertyDto.getCity() != null) {
            property.setCity(propertyDto.getCity());
        }
        
        if (propertyDto.getState() != null) {
            property.setState(propertyDto.getState());
        }
        
        if (propertyDto.getPincode() != null) {
            property.setPincode(propertyDto.getPincode());
        }
        
        property.setStreetNumber(propertyDto.getStreetNumber());
        property.setPlaceName(propertyDto.getPlaceName());
        property.setBedrooms(propertyDto.getBedrooms());
        property.setBathrooms(propertyDto.getBathrooms());
        property.setGarages(propertyDto.getGarages());
        property.setGarageSize(propertyDto.getGarageSize());
        property.setYearBuilt(propertyDto.getYearBuilt());
        property.setAvailability(propertyDto.getAvailability());
        property.setRenovated(propertyDto.getRenovated());
        property.setVideoUrl(propertyDto.getVideoUrl());
        property.setYoutubeUrl(propertyDto.getYoutubeUrl());
        property.setLatitude(propertyDto.getLatitude());
        property.setLongitude(propertyDto.getLongitude());
        property.setNote(propertyDto.getNote());
        property.setPrivateNote(propertyDto.getPrivateNote());
        property.setAgreementAccepted(propertyDto.getAgreementAccepted());
        
        if (propertyDto.getAmenities() != null) {
            property.setAmenities(propertyDto.getAmenities());
        }
        
        if (propertyDto.getSecurityFeatures() != null) {
            property.setSecurityFeatures(propertyDto.getSecurityFeatures());
        }
        
        if (propertyDto.getLuxuriousFeatures() != null) {
            property.setLuxuriousFeatures(propertyDto.getLuxuriousFeatures());
        }
        
        if (propertyDto.getFeatures() != null) {
            property.setFeatures(propertyDto.getFeatures());
        }
        
        if (propertyDto.getImages() != null) {
            property.setImages(propertyDto.getImages());
        }
        
        if (propertyDto.getAdditionalDetails() != null) {
            property.setAdditionalDetails(propertyDto.getAdditionalDetails());
        }
        
        Property updatedProperty = propertyRepository.save(property);
        return mapToDto(updatedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "properties", key = "#id")
    public PropertyDto getProperty(Long id) {
        Property property = propertyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + id));
        
        // Check if the property is visible based on subscription status
        boolean hasActiveSubscription = property.getSubscriptionExpiry() != null && 
                                       property.getSubscriptionExpiry().isAfter(LocalDateTime.now());
        
        // If property is not active or approved and doesn't have active subscription, 
        // it should only be visible to admins and the owner (handled by controller layer)
        if (!property.isActive() || !property.isApproved() || !hasActiveSubscription) {
            // We'll let the controller handle the authorization check
            // Just add a flag to the DTO indicating this property requires special access
            PropertyDto dto = mapToDto(property);
            dto.setRequiresSpecialAccess(true);
            return dto;
        }
        
        return mapToDto(property);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getAllProperties(Pageable pageable) {
        // Only return properties that are active and have a valid subscription
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("active"), true),
                cb.equal(root.get("approved"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        return propertyRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "properties", key = "'all_without_pagination'")
    public List<PropertyDto> getAllPropertiesWithoutPagination() {
        // Only return properties that are active and have a valid subscription
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("active"), true),
                cb.equal(root.get("approved"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        return propertyRepository.findAll(spec).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getApprovedProperties(Pageable pageable) {
        return propertyRepository.findByApprovedTrueAndActiveTrue(pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDto> getApprovedPropertiesWithoutPagination() {
        return propertyRepository.findByApprovedTrueAndActiveTrue().stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getFeaturedProperties(Pageable pageable) {
        // Only return featured properties that are active and have valid subscriptions
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("featured"), true),
                cb.equal(root.get("approved"), true),
                cb.equal(root.get("active"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        return propertyRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
   // @Cacheable(value = "properties", key = "'featured_without_pagination'")
    public List<PropertyDto> getFeaturedPropertiesWithoutPagination() {
        // Only return featured properties that are active and have valid subscriptions
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("featured"), true),
                cb.equal(root.get("approved"), true),
                cb.equal(root.get("active"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        return propertyRepository.findAll(spec).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getUserProperties(Long userId, Pageable pageable) {
        return propertyRepository.findByOwnerId(userId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDto> getUserPropertiesWithoutPagination(Long userId) {
        return propertyRepository.findByOwnerId(userId).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> searchProperties(
            PropertyType type, 
            PropertyStatus status, 
            String district, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            Integer minBedrooms, 
            Pageable pageable) {
        
        // Only search among properties that are approved, active, and have valid subscriptions
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("approved"), true),
                cb.equal(root.get("active"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        
        if (district != null && !district.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("districtName"), district));
        }
        
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        if (minBedrooms != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
        }
        
        return propertyRepository.findAll(spec, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDto> searchPropertiesWithoutPagination(
            PropertyType type, 
            PropertyStatus status, 
            String district, 
            BigDecimal minPrice, 
            BigDecimal maxPrice, 
            Integer minBedrooms) {
        
        // Only search among properties that are approved, active, and have valid subscriptions
        Specification<Property> spec = Specification.where((root, query, cb) -> 
            cb.and(
                cb.equal(root.get("approved"), true),
                cb.equal(root.get("active"), true),
                cb.or(
                    cb.isNull(root.get("subscriptionExpiry")),
                    cb.greaterThan(root.get("subscriptionExpiry"), LocalDateTime.now())
                )
            )
        );
        
        if (type != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("type"), type));
        }
        
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        
        if (district != null && !district.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("districtName"), district));
        }
        
        if (minPrice != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        if (minBedrooms != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("bedrooms"), minBedrooms));
        }
        
        return propertyRepository.findAll(spec).stream().map(this::mapToDto).collect(Collectors.toList());
    }


    @Override
    @Transactional
    @CacheEvict(value = {"properties", "property-search", "user-properties"}, key = "#id")
    public void hardDelete(Long id, Long subAdminId) {
        log.info("SubAdmin {} deleting property {}", subAdminId, id);
        propertyRepository.deleteById(id); // cascade handles 95%
    }

    @Override
    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = {"properties", "property-search", "user-properties"}, allEntries = true)
    public void deleteProperty(Long propertyId, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));

        if (!property.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to delete this property");
        }

        propertyRepository.delete(property);
    }

    @Override
    @Transactional
    public PropertyDto approveProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        property.setApproved(true);
        property.setStatus(PropertyStatus.AVAILABLE);
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyApprovedNotification(savedProperty);
            log.info("Property approval notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property approval notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }

    @Override
    @Transactional
    public PropertyDto rejectProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        property.setApproved(false);
        property.setStatus(PropertyStatus.PENDING_APPROVAL);
        
        // If property has an active subscription, pause it
        if (property.getSubscriptionId() != null) {
            log.info("Property {} has subscription ID {}. Pausing subscription.", propertyId, property.getSubscriptionId());
            
            // Store the original expiry date
            LocalDateTime originalExpiry = property.getSubscriptionExpiry();
            
            if (originalExpiry != null && originalExpiry.isAfter(LocalDateTime.now())) {
                // Calculate remaining time on subscription
                long remainingDays = java.time.Duration.between(LocalDateTime.now(), originalExpiry).toDays();
                log.info("Subscription has {} days remaining", remainingDays);
                
                // Store remaining days in additional details
                if (property.getAdditionalDetails() == null) {
                    property.setAdditionalDetails(new HashMap<>());
                }
                property.getAdditionalDetails().put("pausedSubscriptionDays", String.valueOf(remainingDays));
                property.getAdditionalDetails().put("subscriptionPausedAt", LocalDateTime.now().toString());
                property.getAdditionalDetails().put("originalExpiryDate", originalExpiry.toString());
                
                // Set property as inactive while rejected
                property.setActive(false);
            }
        }
        
        // Disable any active advertisements for this property
        try {
            // Check if advertisements table exists
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "advertisements", null);
            
            if (rs.next()) {
                log.info("Disabling advertisements for rejected property ID: {}", propertyId);
                jdbcTemplate.execute("UPDATE advertisements SET active = false, updated_at = now() WHERE property_id = " + propertyId);
            }
            
            rs.close();
            connection.close();
        } catch (Exception e) {
            log.warn("Error disabling advertisements for property {}: {}", propertyId, e.getMessage());
            // Continue with the process even if this fails
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyRejectedNotification(savedProperty, "Property does not meet our guidelines");
            log.info("Property rejection notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property rejection notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }

    @Override
    @Transactional
   // @CacheEvict(value = "properties", key = "'featured_without_pagination'")
    @CacheEvict(value = "properties", allEntries = true)
    public PropertyDto markFeatured(Long propertyId, boolean featured) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + propertyId));
        
        property.setFeatured(featured);
        
        return mapToDto(propertyRepository.save(property));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "districts", key = "'all'")
    public List<String> getAllDistricts() {
        return propertyRepository.findAll().stream()
                .map(Property::getDistrictName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllStates() {
        return propertyRepository.findAll().stream()
                .map(Property::getState)
                .filter(state -> state != null && !state.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getDistrictsByState(String state) {
        return propertyRepository.findAll().stream()
                .filter(property -> state.equalsIgnoreCase(property.getState()))
                .map(Property::getDistrictName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Map<PropertyType, Long> getPropertyCountByType() {
        return propertyRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Property::getType, Collectors.counting()));
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Long> getPropertyCountByDistrict() {
        return propertyRepository.findAll()
                .stream()
                .collect(Collectors.groupingBy(Property::getDistrictName, Collectors.counting()));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> getPendingApprovalProperties(Pageable pageable) {
        return propertyRepository.findByApprovedFalse(pageable).map(this::mapToDto);
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<PropertyDto> getPendingApprovalPropertiesWithoutPagination() {
        return propertyRepository.findByApprovedFalse().stream().map(this::mapToDto).collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public PropertyDto activateProperty(Long propertyId, Long userId, Long subscriptionId, LocalDateTime expiryDate) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
                
        // Verify ownership
        if (!property.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to activate this property");
        }
        
        // Activate the property and set subscription details
        property.setActive(true);
        property.setSubscriptionExpiry(expiryDate);
        property.setSubscriptionId(subscriptionId);
        
        // If property is already approved by admin, it's ready to be shown
        // Otherwise, it will still need admin approval
        
        Property updatedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyActivatedNotification(updatedProperty);
            log.info("Property activation notification email sent for property ID: {}", updatedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property activation notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(updatedProperty);
    }
    
   /* private PropertyDto mapToDto(Property property) {
        int favoriteCount = property.getFavoritedBy() != null ? property.getFavoritedBy().size() : 0;
        int reelCount = property.getReels() != null ? property.getReels().size() : 0;
        
        return PropertyDto.builder()
                .id(property.getId())
                .permanentId(property.getPermanentId())
                .title(property.getTitle())
                .description(property.getDescription())
                .type(property.getType())
                .status(property.getStatus())
                .label(property.getLabel())
                .price(property.getPrice())
                .area(property.getArea())
                .sizePostfix(property.getSizePostfix())
                .landArea(property.getLandArea())
                .landAreaPostfix(property.getLandAreaPostfix())
                .address(property.getAddress())
                .districtId(property.getDistrict() != null ? property.getDistrict().getId() : null)
                .districtName(property.getDistrictName())
                .city(property.getCity())
                .state(property.getState())
                .pincode(property.getPincode())
                .streetNumber(property.getStreetNumber())
                .placeName(property.getPlaceName())
                .bedrooms(property.getBedrooms())
                .bathrooms(property.getBathrooms())
                .garages(property.getGarages())
                .garageSize(property.getGarageSize())
                .yearBuilt(property.getYearBuilt())
                .availability(property.getAvailability())
                .renovated(property.getRenovated())
                .videoUrl(property.getVideoUrl())
                .youtubeUrl(property.getYoutubeUrl())
                .latitude(property.getLatitude())
                .longitude(property.getLongitude())
                .note(property.getNote())
                .privateNote(property.getPrivateNote())
                .agreementAccepted(property.getAgreementAccepted())
                .approved(property.isApproved())
                .featured(property.isFeatured())
                .active(property.isActive())
                .amenities(property.getAmenities())
                .securityFeatures(property.getSecurityFeatures())
                .luxuriousFeatures(property.getLuxuriousFeatures())
                .features(property.getFeatures())
                .imageUrls(property.getImages())
                .additionalDetails(property.getAdditionalDetails())
                .owner(mapToUserSummaryDto(property.getOwner()))
                .ownerPermanentId(property.getOwnerPermanentId())
                .addedByUser(property.getAddedByUser() != null ? mapToUserSummaryDto(property.getAddedByUser()) : null)
                .addedByFranchisee(property.isAddedByFranchisee())
                .createdAt(property.getCreatedAt())
                .updatedAt(property.getUpdatedAt())
                .subscriptionExpiry(property.getSubscriptionExpiry())
                .scheduledDeletion(property.getScheduledDeletion())
                .subscriptionId(property.getSubscriptionId())
                .favoriteCount(favoriteCount)
                .reelCount(reelCount)
                // Developer-specific fields
                .unitType(property.getUnitType())
                .unitCount(property.getUnitCount())
                .stock(property.getStock())
                .build();
    }*/

    private PropertyDto mapToDto(Property property) {
	    int favoriteCount = property.getFavoritedBy() != null ? property.getFavoritedBy().size() : 0;
	    int reelCount = property.getReels() != null ? property.getReels().size() : 0;

	    // Build base DTO
	    PropertyDto dto = PropertyDto.builder()
	            .id(property.getId())
	            .permanentId(property.getPermanentId())
	            .title(property.getTitle())
	            .description(property.getDescription())
	            .type(property.getType())
	            .status(property.getStatus())
	            .label(property.getLabel())
	            .price(property.getPrice())
	            .area(property.getArea())
	            .sizePostfix(property.getSizePostfix())
	            .landArea(property.getLandArea())
	            .landAreaPostfix(property.getLandAreaPostfix())
	            .address(property.getAddress())
	            .districtId(property.getDistrict() != null ? property.getDistrict().getId() : null)
	            .districtName(property.getDistrictName())
	            .city(property.getCity())
	            .state(property.getState())
	            .pincode(property.getPincode())
	            .streetNumber(property.getStreetNumber())
	            .placeName(property.getPlaceName())
	            .bedrooms(property.getBedrooms())
	            .bathrooms(property.getBathrooms())
	            .garages(property.getGarages())
	            .garageSize(property.getGarageSize())
	            .yearBuilt(property.getYearBuilt())
	            .availability(property.getAvailability())
	            .renovated(property.getRenovated())
	            .videoUrl(property.getVideoUrl())
	            .youtubeUrl(property.getYoutubeUrl())
	            .latitude(property.getLatitude())
	            .longitude(property.getLongitude())
	            .note(property.getNote())
	            .privateNote(property.getPrivateNote())
	            .agreementAccepted(property.getAgreementAccepted())
	            .approved(property.isApproved())
	            .featured(property.isFeatured())
	            .active(property.isActive())
	            .amenities(property.getAmenities())
	            .securityFeatures(property.getSecurityFeatures())
	            .luxuriousFeatures(property.getLuxuriousFeatures())
	            .features(property.getFeatures())
	            .imageUrls(property.getImages())
	            .additionalDetails(property.getAdditionalDetails())
	            .owner(mapToUserSummaryDto(property.getOwner()))
	            .ownerPermanentId(property.getOwnerPermanentId())
	            .addedByUser(property.getAddedByUser() != null ? mapToUserSummaryDto(property.getAddedByUser()) : null)
	            .addedByFranchisee(property.isAddedByFranchisee())
	            .createdAt(property.getCreatedAt())
	            .updatedAt(property.getUpdatedAt())
	            .subscriptionExpiry(property.getSubscriptionExpiry())
	            .scheduledDeletion(property.getScheduledDeletion())
	            .subscriptionId(property.getSubscriptionId())
	            .favoriteCount(favoriteCount)
	            .reelCount(reelCount)
	            // Developer-specific fields
	            .unitType(property.getUnitType())
	            .unitCount(property.getUnitCount())
	            .stock(property.getStock())
	            .build();

	    // Add subscription plan name (NEW PART)
	    if (property.getSubscriptionId() != null) {
	        subscriptionRepository.findById(property.getSubscriptionId()).ifPresent(subscription -> {
	            if (subscription.getPlan() != null) {
	                dto.setSubscriptionPlanName(subscription.getPlan().getName());
	            } else {
	                dto.setSubscriptionPlanName("Unknown Plan");
	            }
	        });
	    }
	    else {
	        dto.setSubscriptionPlanName("No Subscription");
	    }


	    return dto;
	}
    
    private UserSummaryDto mapToUserSummaryDto(User user) {
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
		.profileImageUrl(user.getProfileImageUrl())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }

   /* @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void processPropertyVisibility() {
        LocalDateTime now = LocalDateTime.now();
        
        // Find properties with expired subscriptions
        List<Property> expiredProperties = propertyRepository.findBySubscriptionExpiryBeforeAndActiveTrue(now);
        log.info("Found {} properties with expired subscriptions", expiredProperties.size());
        
        for (Property property : expiredProperties) {
            property.setActive(false);
            propertyRepository.save(property);
            log.info("Deactivated property {} due to subscription expiry", property.getId());
            
            // Send email notification
            try {
                propertyEmailService.sendPropertyDeactivatedNotification(property);
                log.info("Property deactivation notification email sent for property ID: {}", property.getId());
            } catch (Exception e) {
                log.error("Failed to send property deactivation notification email: {}", e.getMessage(), e);
            }
        }
    }*/

    @Scheduled(cron = "0 0 0 * * *") // Run daily at midnight
    @Transactional
    public void processPropertyVisibility() {
        LocalDateTime now = LocalDateTime.now();

        // Find properties with expired subscriptions
        List<Property> expiredProperties = propertyRepository.findBySubscriptionExpiryBeforeAndActiveTrue(now);
        log.info("Found {} properties with expired subscriptions", expiredProperties.size());

        for (Property property : expiredProperties) {

            // 1
            property.setActive(false);
            propertyRepository.save(property);
            log.info("Deactivated property {} due to subscription expiry", property.getId());

            // 2. Deactivate reels associated with this property
            List<Reel> reels = reelRepository.findActiveReelsByPropertyId(property.getId());
            for (Reel reel : reels) {
                reel.setStatus(Reel.ReelStatus.HIDDEN); // instead of setActive(false)
                reelRepository.save(reel);
            }
            if (!reels.isEmpty()) {
                log.info("Deactivated {} reels for property {}", reels.size(), property.getId());
            }
            // Send email notification
            try {
                propertyEmailService.sendPropertyDeactivatedNotification(property);
                log.info("Property deactivation notification email sent for property ID: {}", property.getId());
            } catch (Exception e) {
                log.error("Failed to send property deactivation notification email: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    @Transactional
    public PropertyDto createPropertyFromForm(PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        // Validate that the district exists
        District district = null;
        if (propertyFormDto.getDistrictId() != null) {
            log.info("Looking for district with ID: {}", propertyFormDto.getDistrictId());
            district = districtRepository.findById(propertyFormDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyFormDto.getDistrictId()));
            log.info("Found district: {}", district.getName());
        } else {
            throw new IllegalArgumentException("District ID is required");
        }
        
        // Check if user has an active subscription with available property slots
        Optional<Subscription> availableSubscription = Optional.empty();
        
        // Try all plan types that can have properties
        for (PlanType planType : new PlanType[] {
                PlanType.PROPERTY, 
                PlanType.ADVISOR, 
                PlanType.SELLER, 
                PlanType.DEVELOPER, 
                PlanType.FRANCHISEE
            }) {
            availableSubscription = subscriptionService.getSubscriptionWithAvailablePropertySlots(userId, planType);
            if (availableSubscription.isPresent()) {
                log.info("Found active subscription of type {} for user {} with available slots", 
                    planType, userId);
                break;
            }
        }
        
        boolean hasActiveSubscription = false;
        if (availableSubscription.isEmpty()) {
            // Check if user has any active subscription
            for (PlanType planType : new PlanType[] {
                    PlanType.PROPERTY, 
                    PlanType.ADVISOR, 
                    PlanType.SELLER, 
                    PlanType.DEVELOPER, 
                    PlanType.FRANCHISEE
                }) {
                hasActiveSubscription = subscriptionService.hasActiveSubscription(userId, planType);
                if (hasActiveSubscription) break;
            }
            
            if (hasActiveSubscription) {
                // User has a subscription but has reached the property limit
                throw new BadRequestException("You have reached the maximum number of properties allowed by your subscription plan. Please upgrade your plan or purchase a new subscription.");
            } else {
                // User has no active subscription
                log.info("User has no active subscription. Property will be created as inactive.");
            }
        } else {
            hasActiveSubscription = true;
        }
        
        // Generate permanent ID for the property
        String permanentId = IdGenerator.generatePropertyId();
        
        // Create property entity (initially without images/video URLs)
        Property property = Property.builder()
                .title(propertyFormDto.getTitle())
                .description(propertyFormDto.getDescription())
                .type(propertyFormDto.getType())
                .status(propertyFormDto.getStatus())
                .label(propertyFormDto.getLabel())
                .price(propertyFormDto.getPrice())
                .area(propertyFormDto.getArea())
                .sizePostfix(propertyFormDto.getSizePostfix())
                .landArea(propertyFormDto.getLandArea())
                .landAreaPostfix(propertyFormDto.getLandAreaPostfix())
                .address(propertyFormDto.getAddress())
                .city(propertyFormDto.getCity() != null ? propertyFormDto.getCity() : district.getCity())
                .state(propertyFormDto.getState() != null ? propertyFormDto.getState() : district.getState())
                .pincode(propertyFormDto.getPincode() != null ? propertyFormDto.getPincode() : district.getPincode())
                .streetNumber(propertyFormDto.getStreetNumber())
                .placeName(propertyFormDto.getPlaceName())
                .bedrooms(propertyFormDto.getBedrooms())
                .bathrooms(propertyFormDto.getBathrooms())
                .garages(propertyFormDto.getGarages())
                .garageSize(propertyFormDto.getGarageSize())
                .yearBuilt(propertyFormDto.getYearBuilt())
                .availability(propertyFormDto.getAvailability())
                .renovated(propertyFormDto.getRenovated())
                .latitude(propertyFormDto.getLatitude())
                .longitude(propertyFormDto.getLongitude())
                .note(propertyFormDto.getNote())
                .privateNote(propertyFormDto.getPrivateNote())
                .agreementAccepted(propertyFormDto.getAgreementAccepted())
                .owner(user)
                .ownerPermanentId(user.getPermanentId())
                .featured(false)
                .approved(false)
                .district(district)
                .districtValue(district.getName())
                .districtName(district.getName())
                .permanentId(permanentId)
                .build();
        
        // Save property first to get the ID
        Property savedProperty = propertyRepository.save(property);
        
        // Parse JSON strings for collections
        if (propertyFormDto.getAmenities() != null && !propertyFormDto.getAmenities().isEmpty()) {
            try {
                Set<String> amenities = objectMapper.readValue(propertyFormDto.getAmenities(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setAmenities(amenities);
            } catch (Exception e) {
                log.error("Error parsing amenities JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getSecurityFeatures() != null && !propertyFormDto.getSecurityFeatures().isEmpty()) {
            try {
                Set<String> securityFeatures = objectMapper.readValue(propertyFormDto.getSecurityFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setSecurityFeatures(securityFeatures);
            } catch (Exception e) {
                log.error("Error parsing security features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getLuxuriousFeatures() != null && !propertyFormDto.getLuxuriousFeatures().isEmpty()) {
            try {
                Set<String> luxuriousFeatures = objectMapper.readValue(propertyFormDto.getLuxuriousFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setLuxuriousFeatures(luxuriousFeatures);
            } catch (Exception e) {
                log.error("Error parsing luxurious features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getFeatures() != null && !propertyFormDto.getFeatures().isEmpty()) {
            try {
                Set<String> features = objectMapper.readValue(propertyFormDto.getFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setFeatures(features);
            } catch (Exception e) {
                log.error("Error parsing features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getAdditionalDetails() != null && !propertyFormDto.getAdditionalDetails().isEmpty()) {
            try {
                Map<String, String> additionalDetails = objectMapper.readValue(propertyFormDto.getAdditionalDetails(), 
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                savedProperty.setAdditionalDetails(additionalDetails);
            } catch (Exception e) {
                log.error("Error parsing additional details JSON: {}", e.getMessage());
            }
        }
        
        // Upload images
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            try {
                imageUrls = s3Service.uploadPropertyImages(images, user, savedProperty);
                savedProperty.setImages(imageUrls);
            } catch (Exception e) {
                log.error("Error uploading property images: {}", e.getMessage());
            }
        }
        
        // Upload video if provided
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String videoUrl = s3Service.uploadPropertyVideo(videoFile, user, savedProperty);
                savedProperty.setVideoUrl(videoUrl);
            } catch (Exception e) {
                log.error("Error uploading property video: {}", e.getMessage());
            }
        }
        
        // If user has an available subscription, assign it to the property
        if (availableSubscription.isPresent()) {
            Subscription subscription = availableSubscription.get();
            savedProperty.setActive(true);
            savedProperty.setApproved(true); // Make sure property is approved
            savedProperty.setSubscriptionExpiry(subscription.getEndDate());
            savedProperty.setSubscriptionId(subscription.getId());
            log.info("Property assigned to subscription ID: {} with expiry date: {}", 
                    subscription.getId(), subscription.getEndDate());
            
            // Log that we're activating the property
            log.info("Activating property {} with subscription {}", savedProperty.getId(), subscription.getId());
        } else {
            // Property will be inactive until a subscription is purchased
            savedProperty.setActive(false);
            savedProperty.setApproved(true); // Auto-approve even without subscription
            log.info("Property created as inactive but approved. Needs subscription to be activated.");
        }
        
        // Save property with all updates
        savedProperty = propertyRepository.save(savedProperty);

// 🔥 Notification Trigger Add Karo
        if (savedProperty.isActive() && savedProperty.isApproved()) {
            notificationService.notifyPropertyAddedFiltered(
                    savedProperty.getDistrict().getId(),
                    savedProperty.getId()
            );
        }
        return mapToDto(savedProperty);
    }
    @Override
    @Transactional
    public PropertyDto createPropertyFromFormBySubAdmin(PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long subAdminId) {
        User subAdmin = userRepository.findById(subAdminId)
                .orElseThrow(() -> new ResourceNotFoundException("SubAdmin not found with ID: " + subAdminId));

        //  Validate that the district exists
        District district = null;
        if (propertyFormDto.getDistrictId() != null) {
            log.info("Looking for district with ID: {}", propertyFormDto.getDistrictId());
            district = districtRepository.findById(propertyFormDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyFormDto.getDistrictId()));
            log.info("Found district: {}", district.getName());
        } else {
            throw new IllegalArgumentException("District ID is required");
        }

        //  Generate permanent ID
        String permanentId = IdGenerator.generatePropertyId();

        //  Create property entity
        Property property = Property.builder()
                .title(propertyFormDto.getTitle())
                .description(propertyFormDto.getDescription())
                .type(propertyFormDto.getType())
                .status(propertyFormDto.getStatus())
                .label(propertyFormDto.getLabel())
                .price(propertyFormDto.getPrice())
                .area(propertyFormDto.getArea())
                .sizePostfix(propertyFormDto.getSizePostfix())
                .landArea(propertyFormDto.getLandArea())
                .landAreaPostfix(propertyFormDto.getLandAreaPostfix())
                .address(propertyFormDto.getAddress())
                .city(propertyFormDto.getCity() != null ? propertyFormDto.getCity() : district.getCity())
                .state(propertyFormDto.getState() != null ? propertyFormDto.getState() : district.getState())
                .pincode(propertyFormDto.getPincode() != null ? propertyFormDto.getPincode() : district.getPincode())
                .streetNumber(propertyFormDto.getStreetNumber())
                .placeName(propertyFormDto.getPlaceName())
                .bedrooms(propertyFormDto.getBedrooms())
                .bathrooms(propertyFormDto.getBathrooms())
                .garages(propertyFormDto.getGarages())
                .garageSize(propertyFormDto.getGarageSize())
                .yearBuilt(propertyFormDto.getYearBuilt())
                .availability(propertyFormDto.getAvailability())
                .renovated(propertyFormDto.getRenovated())
                .latitude(propertyFormDto.getLatitude())
                .longitude(propertyFormDto.getLongitude())
                .note(propertyFormDto.getNote())
                .privateNote(propertyFormDto.getPrivateNote())
                .agreementAccepted(propertyFormDto.getAgreementAccepted())
                .owner(subAdmin)
                .ownerPermanentId(subAdmin.getPermanentId())
                .featured(false)
                .approved(true) //  Auto-approved for SubAdmin
                .active(true)   //  Auto-active for SubAdmin
                .district(district)
                .districtValue(district.getName())
                .districtName(district.getName())
                .permanentId(permanentId)
                .build();

        //  Save property to generate ID
        Property savedProperty = propertyRepository.save(property);

        //  Parse JSON fields
        try {
            if (propertyFormDto.getAmenities() != null && !propertyFormDto.getAmenities().isEmpty()) {
                Set<String> amenities = objectMapper.readValue(propertyFormDto.getAmenities(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setAmenities(amenities);
            }

            if (propertyFormDto.getSecurityFeatures() != null && !propertyFormDto.getSecurityFeatures().isEmpty()) {
                Set<String> securityFeatures = objectMapper.readValue(propertyFormDto.getSecurityFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setSecurityFeatures(securityFeatures);
            }

            if (propertyFormDto.getLuxuriousFeatures() != null && !propertyFormDto.getLuxuriousFeatures().isEmpty()) {
                Set<String> luxuriousFeatures = objectMapper.readValue(propertyFormDto.getLuxuriousFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setLuxuriousFeatures(luxuriousFeatures);
            }

            if (propertyFormDto.getFeatures() != null && !propertyFormDto.getFeatures().isEmpty()) {
                Set<String> features = objectMapper.readValue(propertyFormDto.getFeatures(),
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                savedProperty.setFeatures(features);
            }

            if (propertyFormDto.getAdditionalDetails() != null && !propertyFormDto.getAdditionalDetails().isEmpty()) {
                Map<String, String> additionalDetails = objectMapper.readValue(propertyFormDto.getAdditionalDetails(),
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                savedProperty.setAdditionalDetails(additionalDetails);
            }
        } catch (Exception e) {
            log.error("Error parsing property details JSON: {}", e.getMessage());
        }

        //  Upload images to S3
        if (images != null && !images.isEmpty()) {
            try {
                List<String> imageUrls = s3Service.uploadPropertyImages(images, subAdmin, savedProperty);
                savedProperty.setImages(imageUrls);
            } catch (Exception e) {
                log.error("Error uploading property images: {}", e.getMessage());
            }
        }

        //  Upload video if provided
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String videoUrl = s3Service.uploadPropertyVideo(videoFile, subAdmin, savedProperty);
                savedProperty.setVideoUrl(videoUrl);
            } catch (Exception e) {
                log.error("Error uploading property video: {}", e.getMessage());
            }
        }

        //  Save property again after uploads
        savedProperty = propertyRepository.save(savedProperty);

        log.info(" Property created successfully by SubAdmin ID: {}", subAdminId);
        // 🔥 Notification Trigger Add Karo
        notificationService.notifyPropertyAddedFiltered(
                savedProperty.getDistrict().getId(),
                savedProperty.getId()
        );
        return mapToDto(savedProperty);
    }


    @Override
    @Transactional
    public PropertyDto updatePropertyFromForm(Long propertyId, PropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));

        if (!property.getOwner().getId().equals(userId)) {
            throw new UnauthorizedException("You are not authorized to update this property");
        }

        // Update district if districtId is provided
        District district = null;
        if (propertyFormDto.getDistrictId() != null) {
            district = districtRepository.findById(propertyFormDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyFormDto.getDistrictId()));
            property.setDistrict(district);
            property.setDistrictValue(district.getName());
            property.setDistrictName(district.getName());
            
            // If city/state/pincode aren't explicitly provided, use district values
            if (propertyFormDto.getCity() == null) {
                property.setCity(district.getCity());
            }
            
            if (propertyFormDto.getState() == null) {
                property.setState(district.getState());
            }
            
            if (propertyFormDto.getPincode() == null) {
                property.setPincode(district.getPincode());
            }
        }
        
        // Update basic properties
        property.setTitle(propertyFormDto.getTitle());
        property.setDescription(propertyFormDto.getDescription());
        property.setType(propertyFormDto.getType());
        property.setStatus(propertyFormDto.getStatus());
        property.setLabel(propertyFormDto.getLabel());
        property.setPrice(propertyFormDto.getPrice());
        property.setArea(propertyFormDto.getArea());
        property.setSizePostfix(propertyFormDto.getSizePostfix());
        property.setLandArea(propertyFormDto.getLandArea());
        property.setLandAreaPostfix(propertyFormDto.getLandAreaPostfix());
        property.setAddress(propertyFormDto.getAddress());
        
        if (propertyFormDto.getCity() != null) {
            property.setCity(propertyFormDto.getCity());
        }
        
        if (propertyFormDto.getState() != null) {
            property.setState(propertyFormDto.getState());
        }
        
        if (propertyFormDto.getPincode() != null) {
            property.setPincode(propertyFormDto.getPincode());
        }
        
        property.setStreetNumber(propertyFormDto.getStreetNumber());
        property.setPlaceName(propertyFormDto.getPlaceName());
        property.setBedrooms(propertyFormDto.getBedrooms());
        property.setBathrooms(propertyFormDto.getBathrooms());
        property.setGarages(propertyFormDto.getGarages());
        property.setGarageSize(propertyFormDto.getGarageSize());
        property.setYearBuilt(propertyFormDto.getYearBuilt());
        property.setAvailability(propertyFormDto.getAvailability());
        property.setRenovated(propertyFormDto.getRenovated());
        property.setLatitude(propertyFormDto.getLatitude());
        property.setLongitude(propertyFormDto.getLongitude());
        property.setNote(propertyFormDto.getNote());
        property.setPrivateNote(propertyFormDto.getPrivateNote());
        property.setAgreementAccepted(propertyFormDto.getAgreementAccepted());
        
        // Parse JSON strings for collections
        if (propertyFormDto.getAmenities() != null && !propertyFormDto.getAmenities().isEmpty()) {
            try {
                Set<String> amenities = objectMapper.readValue(propertyFormDto.getAmenities(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setAmenities(amenities);
            } catch (Exception e) {
                log.error("Error parsing amenities JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getSecurityFeatures() != null && !propertyFormDto.getSecurityFeatures().isEmpty()) {
            try {
                Set<String> securityFeatures = objectMapper.readValue(propertyFormDto.getSecurityFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setSecurityFeatures(securityFeatures);
            } catch (Exception e) {
                log.error("Error parsing security features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getLuxuriousFeatures() != null && !propertyFormDto.getLuxuriousFeatures().isEmpty()) {
            try {
                Set<String> luxuriousFeatures = objectMapper.readValue(propertyFormDto.getLuxuriousFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setLuxuriousFeatures(luxuriousFeatures);
            } catch (Exception e) {
                log.error("Error parsing luxurious features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getFeatures() != null && !propertyFormDto.getFeatures().isEmpty()) {
            try {
                Set<String> features = objectMapper.readValue(propertyFormDto.getFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setFeatures(features);
            } catch (Exception e) {
                log.error("Error parsing features JSON: {}", e.getMessage());
            }
        }
        
        if (propertyFormDto.getAdditionalDetails() != null && !propertyFormDto.getAdditionalDetails().isEmpty()) {
            try {
                Map<String, String> additionalDetails = objectMapper.readValue(propertyFormDto.getAdditionalDetails(), 
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                property.setAdditionalDetails(additionalDetails);
            } catch (Exception e) {
                log.error("Error parsing additional details JSON: {}", e.getMessage());
            }
        }
        
        // Upload new images if provided
        if (images != null && !images.isEmpty()) {
            try {
                List<String> newImageUrls = s3Service.uploadPropertyImages(images, property.getOwner(), property);
                
                // Append new images to existing ones
                List<String> currentImages = property.getImages();
                if (currentImages == null) {
                    currentImages = new ArrayList<>();
                }
                currentImages.addAll(newImageUrls);
                property.setImages(currentImages);
            } catch (Exception e) {
                log.error("Error uploading property images: {}", e.getMessage());
            }
        }
        
        // Upload new video if provided
        if (videoFile != null && !videoFile.isEmpty()) {
            try {
                String videoUrl = s3Service.uploadPropertyVideo(videoFile, property.getOwner(), property);
                property.setVideoUrl(videoUrl);
            } catch (Exception e) {
                log.error("Error uploading property video: {}", e.getMessage());
            }
        }
        
        Property updatedProperty = propertyRepository.save(property);
        return mapToDto(updatedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public PropertyDto getPropertyByPermanentId(String permanentId) {
        Property property = propertyRepository.findByPermanentId(permanentId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with permanent ID: " + permanentId));
        
        PropertyDto propertyDto = mapToDto(property);
        
        // Check if user is allowed to see this property
        boolean requiresSpecialAccess = !property.isApproved() || !property.isActive() || 
                (property.getSubscriptionExpiry() != null && property.getSubscriptionExpiry().isBefore(LocalDateTime.now()));
        propertyDto.setRequiresSpecialAccess(requiresSpecialAccess);
        
        return propertyDto;
    }

    /**
     * Activate a property with a subscription
     * 
     * @param propertyId The property ID
     * @param subscriptionId The subscription ID
     * @return The updated property
     */
    @Override
    @Transactional
    public PropertyDto activatePropertyWithSubscription(Long propertyId, Long subscriptionId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new ResourceNotFoundException("Subscription not found with ID: " + subscriptionId));
        
        // Verify the subscription belongs to the property owner
        if (!property.getOwner().getId().equals(subscription.getUser().getId())) {
            throw new UnauthorizedException("This subscription does not belong to the property owner");
        }
        
        // Verify the subscription is active
        if (subscription.getStatus() != SubscriptionStatus.ACTIVE) {
            throw new BadRequestException("Cannot activate property with an inactive subscription");
        }
        
        // Activate the property
        property.setActive(true);
        property.setApproved(true); // Auto-approve when subscription is available
        property.setSubscriptionExpiry(subscription.getEndDate());
        property.setSubscriptionId(subscription.getId());
        
        log.info("Property {} activated with subscription ID: {} with expiry date: {}", 
                property.getId(), subscription.getId(), subscription.getEndDate());
        
        Property updatedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyActivatedNotification(updatedProperty);
            log.info("Property activation notification email sent for property ID: {}", updatedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property activation notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(updatedProperty);
    }

    @Override
    public Page<PropertyDto> getPendingProperties(Pageable pageable) {
        return propertyRepository.findByApprovedFalseAndActiveTrue(pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public PropertyDto createDeveloperPropertyFromForm(DeveloperPropertyFormDto propertyFormDto, List<MultipartFile> images, MultipartFile videoFile, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
                
        // Verify user has developer role
        if (!user.getRoles().contains(com.nearprop.entity.Role.DEVELOPER)) {
            throw new UnauthorizedException("Only developers can create developer properties");
        }
        
        // Validate that the district exists
        District district = null;
        if (propertyFormDto.getDistrictId() != null) {
            log.info("Looking for district with ID: {}", propertyFormDto.getDistrictId());
            district = districtRepository.findById(propertyFormDto.getDistrictId())
                    .orElseThrow(() -> new ResourceNotFoundException("District not found with ID: " + propertyFormDto.getDistrictId()));
            log.info("Found district: {}", district.getName());
        } else {
            throw new IllegalArgumentException("District ID is required");
        }
        
        // Check if user has an active subscription with available property slots
        // Use List instead of Optional to avoid the non-unique result exception
        boolean hasActiveSubscription = false;
        Subscription activeSubscription = null;

        // Try all plan types that can have properties, starting with DEVELOPER
        for (PlanType planType : new PlanType[] {
                PlanType.DEVELOPER, 
                PlanType.PROPERTY, 
                PlanType.ADVISOR, 
                PlanType.SELLER, 
                PlanType.FRANCHISEE
            }) {
            List<Subscription> subscriptions = subscriptionRepository.findSubscriptionsWithAvailablePropertySlots(userId, planType);
            if (!subscriptions.isEmpty()) {
                // Take the first one (they're ordered by end date DESC)
                activeSubscription = subscriptions.get(0);
                hasActiveSubscription = true;
                log.info("Found active subscription of type {} for user {} with available slots", 
                    planType, userId);
                break;
            }
        }
        
        if (!hasActiveSubscription) {
            // Check if user has any active subscription
            for (PlanType planType : new PlanType[] {
                    PlanType.DEVELOPER, 
                    PlanType.PROPERTY, 
                    PlanType.ADVISOR, 
                    PlanType.SELLER, 
                    PlanType.FRANCHISEE
                }) {
                hasActiveSubscription = subscriptionService.hasActiveSubscription(userId, planType);
                if (hasActiveSubscription) break;
            }
            
            if (hasActiveSubscription) {
                // User has a subscription but has reached the property limit
                throw new BadRequestException("You have reached the maximum number of properties allowed by your subscription plan. Please upgrade your plan or purchase a new subscription.");
            } else {
                // User has no active subscription
                log.info("User has no active subscription. Property will be created as inactive.");
            }
        }

        // Generate permanent ID for the property
        String permanentId = IdGenerator.generatePropertyId();
        
        // Create property
        Property property = Property.builder()
                .title(propertyFormDto.getTitle())
                .description(propertyFormDto.getDescription())
                .type(propertyFormDto.getType())
                .status(propertyFormDto.getStatus())
                .label(propertyFormDto.getLabel())
                .price(propertyFormDto.getPrice())
                .area(propertyFormDto.getArea())
                .sizePostfix(propertyFormDto.getSizePostfix())
                .landArea(propertyFormDto.getLandArea())
                .landAreaPostfix(propertyFormDto.getLandAreaPostfix())
                .address(propertyFormDto.getAddress())
                .city(propertyFormDto.getCity() != null ? propertyFormDto.getCity() : district.getCity())
                .state(propertyFormDto.getState() != null ? propertyFormDto.getState() : district.getState())
                .pincode(propertyFormDto.getPincode() != null ? propertyFormDto.getPincode() : district.getPincode())
                .streetNumber(propertyFormDto.getStreetNumber())
                .placeName(propertyFormDto.getPlaceName())
                .bedrooms(propertyFormDto.getBedrooms())
                .bathrooms(propertyFormDto.getBathrooms())
                .garages(propertyFormDto.getGarages())
                .garageSize(propertyFormDto.getGarageSize())
                .yearBuilt(propertyFormDto.getYearBuilt())
                .availability(propertyFormDto.getAvailability())
                .renovated(propertyFormDto.getRenovated())
                .latitude(propertyFormDto.getLatitude())
                .longitude(propertyFormDto.getLongitude())
                .note(propertyFormDto.getNote())
                .privateNote(propertyFormDto.getPrivateNote())
                .agreementAccepted(propertyFormDto.getAgreementAccepted())
                .owner(user)
                .ownerPermanentId(user.getPermanentId())
                .featured(false)
                .district(district)
                .districtValue(district.getName())
                .districtName(district.getName())
                .permanentId(permanentId)
                // Developer-specific fields
                .unitType(propertyFormDto.getUnitType())
                .unitCount(propertyFormDto.getUnitCount())
                .stock(propertyFormDto.getStock())
                .build();
        
        // If user has an available subscription, assign it to the property
        if (activeSubscription != null) {
            property.setActive(true);
            property.setApproved(true); // Auto-approve when subscription is available
            property.setSubscriptionExpiry(activeSubscription.getEndDate());
            property.setSubscriptionId(activeSubscription.getId());
            log.info("Property assigned to subscription ID: {} with expiry date: {}", 
                    activeSubscription.getId(), activeSubscription.getEndDate());
        } else {
            // Property will be inactive until a subscription is purchased
            property.setActive(false);
            property.setApproved(true); // Auto-approve even without subscription
            log.info("Property created as inactive but approved. Needs subscription to be activated.");
        }
        
        // Process JSON fields
        try {
            if (propertyFormDto.getAmenities() != null && !propertyFormDto.getAmenities().isEmpty()) {
                Set<String> amenities = objectMapper.readValue(propertyFormDto.getAmenities(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setAmenities(amenities);
            } else {
                property.setAmenities(new HashSet<>());
            }
            
            if (propertyFormDto.getSecurityFeatures() != null && !propertyFormDto.getSecurityFeatures().isEmpty()) {
                Set<String> securityFeatures = objectMapper.readValue(propertyFormDto.getSecurityFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setSecurityFeatures(securityFeatures);
            } else {
                property.setSecurityFeatures(new HashSet<>());
            }
            
            if (propertyFormDto.getLuxuriousFeatures() != null && !propertyFormDto.getLuxuriousFeatures().isEmpty()) {
                Set<String> luxuriousFeatures = objectMapper.readValue(propertyFormDto.getLuxuriousFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setLuxuriousFeatures(luxuriousFeatures);
            } else {
                property.setLuxuriousFeatures(new HashSet<>());
            }
            
            if (propertyFormDto.getFeatures() != null && !propertyFormDto.getFeatures().isEmpty()) {
                Set<String> features = objectMapper.readValue(propertyFormDto.getFeatures(), 
                        objectMapper.getTypeFactory().constructCollectionType(Set.class, String.class));
                property.setFeatures(features);
            } else {
                property.setFeatures(new HashSet<>());
            }
            
            if (propertyFormDto.getAdditionalDetails() != null && !propertyFormDto.getAdditionalDetails().isEmpty()) {
                Map<String, String> additionalDetails = objectMapper.readValue(propertyFormDto.getAdditionalDetails(), 
                        objectMapper.getTypeFactory().constructMapType(Map.class, String.class, String.class));
                property.setAdditionalDetails(additionalDetails);
            } else {
                property.setAdditionalDetails(new HashMap<>());
            }
        } catch (Exception e) {
            log.error("Error parsing JSON fields: {}", e.getMessage());
            // Set default empty collections to avoid null pointer exceptions
            property.setAmenities(new HashSet<>());
            property.setSecurityFeatures(new HashSet<>());
            property.setLuxuriousFeatures(new HashSet<>());
            property.setFeatures(new HashSet<>());
            property.setAdditionalDetails(new HashMap<>());
        }
        
        // Initialize empty image list
        property.setImages(new ArrayList<>());
        
        // Save property first to get the ID
        Property savedProperty = propertyRepository.save(property);
        
        // Now that we have the property ID, upload images and video
        List<String> imageUrls = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            // Upload each image to S3
            for (MultipartFile image : images) {
                if (!image.isEmpty()) {
                    String imageUrl = s3Service.uploadPropertyImage(image, user, savedProperty);
                    imageUrls.add(imageUrl);
                }
            }
        }
        
        // Set the images on the saved property
        if (!imageUrls.isEmpty()) {
            savedProperty.setImages(imageUrls);
            savedProperty = propertyRepository.save(savedProperty);
        }
        
        // Upload video if provided
        if (videoFile != null && !videoFile.isEmpty()) {
            String videoUrl = s3Service.uploadPropertyVideo(videoFile, user, savedProperty);
            savedProperty.setVideoUrl(videoUrl);
            savedProperty = propertyRepository.save(savedProperty);
            // 🔥 Notification Trigger Add Karo
            if (savedProperty.isActive()) {
                notificationService.notifyPropertyAddedFiltered(
                        savedProperty.getDistrict().getId(),
                        savedProperty.getId()
                );
            }
        }
        
        // Send notification email
        PropertyDto propertyDto = mapToDto(savedProperty);
        try {
            propertyEmailService.sendPropertyCreationEmail(user.getEmail(), propertyDto);
            log.info("Developer property creation email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send developer property creation email: {}", e.getMessage(), e);
        }
        
        return propertyDto;
    }
    
    @Override
    @Transactional
    public PropertyDto updatePropertyStock(Long propertyId, Integer stock, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if user is property owner or admin
        if (!property.getOwner().getId().equals(userId) && 
                !user.getRoles().contains(com.nearprop.entity.Role.ADMIN)) {
            throw new UnauthorizedException("You do not have permission to update this property");
        }
        
        // Update the stock
        property.setStock(stock);
        Property updatedProperty = propertyRepository.save(property);
        
        // Send notification email
        try {
            propertyEmailService.sendPropertyStockUpdateEmail(user.getEmail(), mapToDto(updatedProperty));
            log.info("Property stock update email sent for property ID: {}", propertyId);
        } catch (Exception e) {
            log.error("Failed to send property stock update email: {}", e.getMessage(), e);
        }
        
        return mapToDto(updatedProperty);
    }

    @Override
    @Transactional
    public PropertyDto updateDeveloperPropertyDetails(Long propertyId, String unitType, Integer unitCount, Integer stock, Long userId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        
        // Check if user is property owner, has developer role, or admin
        boolean isDeveloper = user.getRoles().contains(com.nearprop.entity.Role.DEVELOPER);
        boolean isAdmin = user.getRoles().contains(com.nearprop.entity.Role.ADMIN);
        boolean isOwner = property.getOwner().getId().equals(userId);
        
        if (!(isOwner || isDeveloper || isAdmin)) {
            throw new UnauthorizedException("You do not have permission to update this property");
        }
        
        // Update the developer fields if provided
        if (unitType != null) {
            property.setUnitType(unitType);
        }
        
        if (unitCount != null) {
            property.setUnitCount(unitCount);
        }
        
        if (stock != null) {
            property.setStock(stock);
        }
        
        Property updatedProperty = propertyRepository.save(property);
        
        // Send notification email
        try {
            propertyEmailService.sendPropertyStockUpdateEmail(user.getEmail(), mapToDto(updatedProperty));
            log.info("Developer property details update email sent for property ID: {}", propertyId);
        } catch (Exception e) {
            log.error("Failed to send developer property details update email: {}", e.getMessage(), e);
        }
        
        return mapToDto(updatedProperty);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PropertyDto> advancedSearch(
            PropertyStatus category, 
            String city,
            String district,
            Double latitude,
            Double longitude,
            Double radius,
            PropertyType propertyType,
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            Double minArea,
            Double maxArea, 
            Integer bedrooms,
            Integer bathrooms,
            String keyword,
            Pageable pageable) {
        
        // Build the specification for the search
        Specification<Property> spec = Specification.where((Specification<Property>)
                (root, query, cb) -> cb.equal(root.get("approved"), true)
        ).and((Specification<Property>)
                (root, query, cb) -> cb.equal(root.get("active"), true)
        );
        
        // Add filter for category/status
        if (category != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("status"), category));
        }
        
        // Add filter for city
        if (city != null && !city.isEmpty()) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }
        
        // Add filter for district
        if (district != null && !district.isEmpty()) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(cb.lower(root.get("districtName")), district.toLowerCase()));
        }
        
        // Add filter for property type
        if (propertyType != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("type"), propertyType));
        }
        
        // Add filter for price range
        if (minPrice != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        // Add filter for area range
        if (minArea != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("area"), minArea));
        }
        if (maxArea != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("area"), maxArea));
        }
        
        // Add filter for bedrooms
        if (bedrooms != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("bedrooms"), bedrooms));
        }
        
        // Add filter for bathrooms
        if (bathrooms != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("bathrooms"), bathrooms));
        }
        
        // Add filter for keyword search in title and description
        if (keyword != null && !keyword.isEmpty()) {
            String searchTerm = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> 
                    cb.or(
                            cb.like(cb.lower(root.get("title")), searchTerm),
                            cb.like(cb.lower(root.get("description")), searchTerm)
                    ));
        }
        
        // Location-based search (simplified - in a real implementation would use geospatial functions)
        if (latitude != null && longitude != null && radius != null) {
            // This is a simplified approach for demonstration purposes
            // In a real application, you might want to use a more sophisticated approach
            spec = spec.and((Specification<Property>)(root, query, cb) -> {
                // Rough filter based on bounding box (for performance)
                // Convert radius from km to degrees (approximate)
                double latDegrees = radius / 111.0; // 1 degree latitude is approximately 111 km
                double lngDegrees = radius / (111.0 * Math.cos(Math.toRadians(latitude)));
                
                return cb.and(
                        cb.between(root.get("latitude"), latitude - latDegrees, latitude + latDegrees),
                        cb.between(root.get("longitude"), longitude - lngDegrees, longitude + lngDegrees)
                );
            });
        }
        
        // Execute the search with all the filters
        return propertyRepository.findAll(spec, pageable)
                .map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PropertyDto> advancedSearchWithoutPagination(
            PropertyStatus category, 
            String city,
            String district,
            Double latitude,
            Double longitude,
            Double radius,
            PropertyType propertyType,
            BigDecimal minPrice, 
            BigDecimal maxPrice,
            Double minArea,
            Double maxArea, 
            Integer bedrooms,
            Integer bathrooms,
            String keyword) {
        
        // Build the specification for the search
        Specification<Property> spec = Specification.where((Specification<Property>)
                (root, query, cb) -> cb.equal(root.get("approved"), true)
        ).and((Specification<Property>)
                (root, query, cb) -> cb.equal(root.get("active"), true)
        );
        
        // Add filter for category/status
        if (category != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("status"), category));
        }
        
        // Add filter for city
        if (city != null && !city.isEmpty()) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(cb.lower(root.get("city")), city.toLowerCase()));
        }
        
        // Add filter for district
        if (district != null && !district.isEmpty()) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(cb.lower(root.get("districtName")), district.toLowerCase()));
        }
        
        // Add filter for property type
        if (propertyType != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("type"), propertyType));
        }
        
        // Add filter for price range
        if (minPrice != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("price"), minPrice));
        }
        
        if (maxPrice != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), maxPrice));
        }
        
        // Add filter for area range
        if (minArea != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("area"), minArea));
        }
        
        if (maxArea != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.lessThanOrEqualTo(root.get("area"), maxArea));
        }
        
        // Add filter for bedrooms
        if (bedrooms != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("bedrooms"), bedrooms));
        }
        
        // Add filter for bathrooms
        if (bathrooms != null) {
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.equal(root.get("bathrooms"), bathrooms));
        }
        
        // Add keyword search
        if (keyword != null && !keyword.trim().isEmpty()) {
            String keywordLower = keyword.toLowerCase();
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.or(
                            cb.like(cb.lower(root.get("title")), "%" + keywordLower + "%"),
                            cb.like(cb.lower(root.get("description")), "%" + keywordLower + "%"),
                            cb.like(cb.lower(root.get("address")), "%" + keywordLower + "%")
                    ));
        }
        
        // Add location-based search if coordinates are provided
        if (latitude != null && longitude != null && radius != null) {
            // This is a simplified distance calculation
            // In a real implementation, you might want to use a more sophisticated approach
            spec = spec.and((Specification<Property>)
                    (root, query, cb) -> cb.and(
                            cb.isNotNull(root.get("latitude")),
                            cb.isNotNull(root.get("longitude"))
                    ));
        }
        
        return propertyRepository.findAll(spec).stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "properties", key = "'location_' + #latitude + '_' + #longitude + '_' + #radius")
    public List<PropertyDto> getAllPropertiesWithoutPagination(Double latitude, Double longitude, Double radius) {
        if (latitude != null && longitude != null && radius != null) {
            // Convert radius from kilometers to meters
            double radiusMeters = radius * 1000.0;
            // Use PostGIS-based DB-side filtering and sorting for performance
            return propertyRepository.findAllWithinRadius(latitude, longitude, radiusMeters)
                    .stream().map(p -> mapToDto(p, latitude, longitude)).collect(Collectors.toList());
        } else if (latitude != null && longitude != null) {
            // Fallback to all sorted by distance if radius not provided
            return propertyRepository.findAllOrderByDistance(latitude, longitude)
                    .stream().map(p -> mapToDto(p, latitude, longitude)).collect(Collectors.toList());
        } else {
            return getAllPropertiesWithoutPagination();
        }
    }

    // Haversine formula for distance in km
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    private PropertyDto mapToDto(Property property, Double userLat, Double userLng) {
        PropertyDto dto = mapToDto(property);
        if (userLat != null && userLng != null && property.getLatitude() != null && property.getLongitude() != null) {
            // Calculate distance
            double distance = calculateDistance(userLat, userLng, property.getLatitude(), property.getLongitude());
            dto.setDistanceKm(distance);
            // Set route link
            dto.setRouteLink("https://www.google.com/maps/dir/?api=1&origin=" + userLat + "," + userLng + "&destination=" + property.getLatitude() + "," + property.getLongitude() + "&travelmode=driving");
            // Set address using Google Maps Reverse Geocoding
            String address = fetchAddressFromLatLng(property.getLatitude(), property.getLongitude());
            dto.setAddress(address);
        }
        return dto;
    }

    private String fetchAddressFromLatLng(Double lat, Double lng) {
        try {
            String url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + lat + "," + lng + "&key=" + googleMapsApiKey;
            var response = restTemplate.getForObject(url, java.util.Map.class);
            if (response != null && response.containsKey("results")) {
                var results = (java.util.List<?>) response.get("results");
                if (!results.isEmpty()) {
                    var first = (java.util.Map<?, ?>) results.get(0);
                    Object formatted = first.get("formatted_address");
                    if (formatted != null) return formatted.toString();
                }
            }
        } catch (Exception e) {
            log.warn("Failed to reverse geocode lat/lng {} {}: {}", lat, lng, e.getMessage());
        }
        return null;
    }

    @Override
    @Transactional(propagation = Propagation.NEVER)
    @org.springframework.cache.annotation.CacheEvict(value = {"properties", "property-search", "user-properties"}, allEntries = true)
    public boolean adminDeleteProperty(Long propertyId) {
        log.info("Admin deleting property with ID: {}", propertyId);
        
        try {
            // First check if the property exists
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM properties WHERE id = ?", 
                Integer.class, 
                propertyId
            );
            
            if (count == null || count == 0) {
                throw new ResourceNotFoundException("Property not found with ID: " + propertyId);
            }
            
            // Execute each delete statement individually with proper error handling
            
            // 1. Get property data for notifications
            Property property = propertyRepository.findById(propertyId).orElse(null);
            
            // 2. Delete related data one by one
            executeDeleteSafely("DELETE FROM property_visits WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_images WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_amenities WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_features WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_security_features WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_luxurious_features WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM property_additional_details WHERE property_id = ?", propertyId);
            
            // Delete property reviews and review likes
            try {
                List<Long> reviewIds = jdbcTemplate.queryForList(
                    "SELECT id FROM property_reviews WHERE property_id = ?", 
                    Long.class, 
                    propertyId
                );
                
                for (Long reviewId : reviewIds) {
                    executeDeleteSafely("DELETE FROM review_likes WHERE review_id = ?", reviewId);
                }
                
                executeDeleteSafely("DELETE FROM property_reviews WHERE property_id = ?", propertyId);
            } catch (Exception e) {
                log.warn("Error deleting property reviews: {}", e.getMessage());
            }
            
            // Check if advertisements table has property_id column
            executeDeleteSafely("DELETE FROM advertisements WHERE property_id = ?", propertyId);
            // Alternative column name
            executeDeleteSafely("DELETE FROM advertisements WHERE property = ?", propertyId);
            
            // Handle property update requests
            try {
                List<Long> updateRequestIds = jdbcTemplate.queryForList(
                    "SELECT id FROM property_update_requests WHERE property_id = ?", 
                    Long.class, 
                    propertyId
                );
                
                for (Long requestId : updateRequestIds) {
                    // Try different column names
                    executeDeleteSafely("DELETE FROM property_update_fields WHERE property_update_request_id = ?", requestId);
                    executeDeleteSafely("DELETE FROM property_update_fields WHERE request_id = ?", requestId);
                }
                
                executeDeleteSafely("DELETE FROM property_update_requests WHERE property_id = ?", propertyId);
            } catch (Exception e) {
                log.warn("Error deleting property update requests: {}", e.getMessage());
            }
            
            // Handle reels
            /*try {
                List<Long> reelIds = jdbcTemplate.queryForList(
                    "SELECT id FROM reels WHERE property_id = ?", 
                    Long.class, 
                    propertyId
                );
                
                for (Long reelId : reelIds) {
                    executeDeleteSafely("DELETE FROM reel_interactions WHERE reel_id = ?", reelId);
                    executeDeleteSafely("DELETE FROM reel_comments WHERE reel_id = ?", reelId);
                }
                
                executeDeleteSafely("DELETE FROM reels WHERE property_id = ?", propertyId);
            } catch (Exception e) {
                log.warn("Error deleting reels: {}", e.getMessage());
            }*/

	    // Handle reels
            try {
           List<Long> reelIds = jdbcTemplate.queryForList(
            "SELECT id FROM property_reels WHERE property_id = ?", Long.class, propertyId);

         for (Long reelId : reelIds) {
        executeDeleteSafely("DELETE FROM reel_interactions WHERE reel_id = ?", reelId);
        executeDeleteSafely("DELETE FROM reel_comments WHERE reel_id = ?", reelId);
         }

        executeDeleteSafely("DELETE FROM property_reels WHERE property_id = ?", propertyId);
      } catch (Exception e) {
       log.warn("Error deleting reels: {}", e.getMessage());
       }
            
            // Handle chat rooms
            try {
                List<Long> chatRoomIds = jdbcTemplate.queryForList(
                    "SELECT id FROM chat_rooms WHERE property_id = ?", 
                    Long.class, 
                    propertyId
                );
                
                for (Long roomId : chatRoomIds) {
                    // Get message IDs first
                    List<Long> messageIds = jdbcTemplate.queryForList(
                        "SELECT id FROM chat_messages WHERE chat_room_id = ?", 
                        Long.class, 
                        roomId
                    );
                    
                    for (Long messageId : messageIds) {
                        executeDeleteSafely("DELETE FROM chat_message_reports WHERE message_id = ?", messageId);
                    }
                    
                    executeDeleteSafely("DELETE FROM chat_attachments WHERE chat_room_id = ?", roomId);
                    executeDeleteSafely("DELETE FROM chat_messages WHERE chat_room_id = ?", roomId);
                    executeDeleteSafely("DELETE FROM chat_room_participants WHERE chat_room_id = ?", roomId);
                }
                
                executeDeleteSafely("DELETE FROM chat_rooms WHERE property_id = ?", propertyId);
            } catch (Exception e) {
                log.warn("Error deleting chat rooms: {}", e.getMessage());
            }
            
            // Delete user favorites
            executeDeleteSafely("DELETE FROM user_favorites WHERE property_id = ?", propertyId);
            executeDeleteSafely("DELETE FROM user_favorite_properties WHERE property_id = ?", propertyId);
            
            // Finally delete the property
            int deleted = jdbcTemplate.update("DELETE FROM properties WHERE id = ?", propertyId);
            
            // Send notification if needed
            if (property != null && property.getAddedByUser() != null) {
                try {
                    propertyEmailService.sendPropertyDeactivatedNotification(property);
                } catch (Exception e) {
                    log.error("Failed to send property deleted email: {}", e.getMessage());
                }
            }
            
            return deleted > 0;
        } catch (Exception e) {
            log.error("Error deleting property: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to delete property: " + e.getMessage(), e);
        }
    }
    
    /**
     * Executes a delete statement safely, catching and logging any exceptions
     */
    private void executeDeleteSafely(String sql, Object... args) {
        try {
            jdbcTemplate.update(sql, args);
        } catch (Exception e) {
            log.warn("Error executing SQL: {} - Error: {}", sql, e.getMessage());
            // Continue execution, don't throw exception
        }
    }

    @Override
    @Transactional
    public PropertyDto deactivateProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        log.info("Admin deactivating property with ID: {}", propertyId);
        
        // Set property as inactive
        property.setActive(false);
        
        // Add deactivation info
        if (property.getAdditionalDetails() == null) {
            property.setAdditionalDetails(new HashMap<>());
        }
        property.getAdditionalDetails().put("deactivatedByAdmin", "true");
        property.getAdditionalDetails().put("deactivatedAt", LocalDateTime.now().toString());
        
        // Disable any active advertisements for this property
        try {
            // Check if advertisements table exists
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "advertisements", null);
            
            if (rs.next()) {
                log.info("Disabling advertisements for deactivated property ID: {}", propertyId);
                jdbcTemplate.execute("UPDATE advertisements SET active = false, updated_at = now() WHERE property_id = " + propertyId);
            }
            
            rs.close();
            connection.close();
        } catch (Exception e) {
            log.warn("Error disabling advertisements for property {}: {}", propertyId, e.getMessage());
            // Continue with the process even if this fails
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyDeactivatedNotification(savedProperty, "Property has been deactivated by admin");
            log.info("Property deactivation notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property deactivation notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }
    
    @Override
    @Transactional
    public PropertyDto holdProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        log.info("Admin holding property with ID: {}", propertyId);
        
        // If property has an active subscription, pause it
        if (property.getSubscriptionId() != null) {
            log.info("Property {} has subscription ID {}. Holding subscription.", propertyId, property.getSubscriptionId());
            
            // Store the original expiry date
            LocalDateTime originalExpiry = property.getSubscriptionExpiry();
            
            if (originalExpiry != null && originalExpiry.isAfter(LocalDateTime.now())) {
                // Calculate remaining time on subscription
                long remainingDays = java.time.Duration.between(LocalDateTime.now(), originalExpiry).toDays();
                log.info("Subscription has {} days remaining", remainingDays);
                
                // Store remaining days in additional details
                if (property.getAdditionalDetails() == null) {
                    property.setAdditionalDetails(new HashMap<>());
                }
                property.getAdditionalDetails().put("heldSubscriptionDays", String.valueOf(remainingDays));
                property.getAdditionalDetails().put("subscriptionHeldAt", LocalDateTime.now().toString());
                property.getAdditionalDetails().put("originalExpiryDate", originalExpiry.toString());
                
                // Set property as inactive while held
                property.setActive(false);
                property.getAdditionalDetails().put("holdStatus", "HELD");
            }
        }
        
        // Disable any active advertisements for this property
        try {
            // Check if advertisements table exists
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "advertisements", null);
            
            if (rs.next()) {
                log.info("Disabling advertisements for held property ID: {}", propertyId);
                jdbcTemplate.execute("UPDATE advertisements SET active = false, updated_at = now() WHERE property_id = " + propertyId);
            }
            
            rs.close();
            connection.close();
        } catch (Exception e) {
            log.warn("Error disabling advertisements for property {}: {}", propertyId, e.getMessage());
            // Continue with the process even if this fails
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyHeldNotification(savedProperty, "Your property listing has been temporarily held");
            log.info("Property hold notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property hold notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }
    
    @Override
    @Transactional
    public PropertyDto blockProperty(Long propertyId, String reason) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        log.info("Admin blocking property with ID: {}", propertyId);
        
        // Set property as blocked
        property.setActive(false);
        property.setApproved(false);
        property.setStatus(PropertyStatus.BLOCKED);
        
        // Add blocking info
        if (property.getAdditionalDetails() == null) {
            property.setAdditionalDetails(new HashMap<>());
        }
        property.getAdditionalDetails().put("blockedByAdmin", "true");
        property.getAdditionalDetails().put("blockedAt", LocalDateTime.now().toString());
        property.getAdditionalDetails().put("blockReason", reason != null ? reason : "Violated platform policies");
        
        // If property has an active subscription, pause it
        if (property.getSubscriptionId() != null) {
            log.info("Property {} has subscription ID {}. Pausing subscription due to block.", 
                    propertyId, property.getSubscriptionId());
            
            // Store the original expiry date
            LocalDateTime originalExpiry = property.getSubscriptionExpiry();
            
            if (originalExpiry != null && originalExpiry.isAfter(LocalDateTime.now())) {
                // Calculate remaining time on subscription
                long remainingDays = java.time.Duration.between(LocalDateTime.now(), originalExpiry).toDays();
                log.info("Subscription has {} days remaining", remainingDays);
                
                // Store remaining days in additional details
                property.getAdditionalDetails().put("blockedSubscriptionDays", String.valueOf(remainingDays));
                property.getAdditionalDetails().put("originalExpiryDate", originalExpiry.toString());
            }
        }
        
        // Disable any active advertisements for this property
        try {
            // Check if advertisements table exists
            Connection connection = jdbcTemplate.getDataSource().getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet rs = metaData.getTables(null, null, "advertisements", null);
            
            if (rs.next()) {
                log.info("Disabling advertisements for blocked property ID: {}", propertyId);
                jdbcTemplate.execute("UPDATE advertisements SET active = false, updated_at = now() WHERE property_id = " + propertyId);
            }
            
            rs.close();
            connection.close();
        } catch (Exception e) {
            log.warn("Error disabling advertisements for property {}: {}", propertyId, e.getMessage());
            // Continue with the process even if this fails
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyBlockedNotification(savedProperty, reason);
            log.info("Property block notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property block notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }
    
    @Override
    @Transactional
    public PropertyDto unblockProperty(Long propertyId) {
        Property property = propertyRepository.findById(propertyId)
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with ID: " + propertyId));
        
        log.info("Admin unblocking property with ID: {}", propertyId);
        
        // Check if property was blocked
        if (!PropertyStatus.BLOCKED.equals(property.getStatus())) {
            throw new BadRequestException("Property is not currently blocked");
        }
        
        // Restore property to pending approval state
        property.setStatus(PropertyStatus.PENDING_APPROVAL);
        property.getAdditionalDetails().put("unblockedAt", LocalDateTime.now().toString());
        
        // If subscription was paused, restore remaining days
        if (property.getAdditionalDetails().containsKey("blockedSubscriptionDays")) {
            String remainingDaysStr = property.getAdditionalDetails().get("blockedSubscriptionDays");
            String originalExpiryStr = property.getAdditionalDetails().get("originalExpiryDate");
            
            if (remainingDaysStr != null && originalExpiryStr != null) {
                try {
                    long remainingDays = Long.parseLong(remainingDaysStr);
                    log.info("Restoring {} subscription days for property ID: {}", remainingDays, propertyId);
                    
                    // Set new expiry date
                    LocalDateTime newExpiry = LocalDateTime.now().plusDays(remainingDays);
                    property.setSubscriptionExpiry(newExpiry);
                    
                    // Clean up temporary fields
                    property.getAdditionalDetails().remove("blockedSubscriptionDays");
                    property.getAdditionalDetails().remove("originalExpiryDate");
                    
                    // Property remains inactive until approved
                    property.setActive(false);
                } catch (NumberFormatException e) {
                    log.error("Error parsing remaining days: {}", e.getMessage());
                }
            }
        }
        
        Property savedProperty = propertyRepository.save(property);
        
        // Send email notification
        try {
            propertyEmailService.sendPropertyUnblockedNotification(savedProperty);
            log.info("Property unblock notification email sent for property ID: {}", savedProperty.getId());
        } catch (Exception e) {
            log.error("Failed to send property unblock notification email: {}", e.getMessage(), e);
        }
        
        return mapToDto(savedProperty);
    }
    @Override
    public boolean hasActivePropertySubscription(Long userId) {
        if (userId == null) return false;

        return subscriptionRepository.existsActiveByUserIdAndPlanType(
                userId,
                SubscriptionPlan.PlanType.PROPERTY, // Property check ke liye
                LocalDateTime.now()
        );
    }

    @Override
    public boolean hasActiveProfileSubscription(Long userId) {
        return subscriptionRepository.existsActiveByUserIdAndPlanType(
                userId, PlanType.PROFILE, LocalDateTime.now());


    }



} 

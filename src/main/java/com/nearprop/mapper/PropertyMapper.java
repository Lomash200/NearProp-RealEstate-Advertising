package com.nearprop.mapper;

import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

/**
 * Mapper class to convert between Property entity and PropertyDto
 */
@Component
public class PropertyMapper {

    /**
     * Convert Property entity to PropertyDto
     * @param property The property entity
     * @return The property DTO
     */
    public PropertyDto toDto(Property property) {
        if (property == null) {
            return null;
        }
        
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
                .build();
    }
    
    /**
     * Convert User entity to UserSummaryDto
     * @param user The user entity
     * @return The user summary DTO
     */
    private UserSummaryDto mapToUserSummaryDto(User user) {
        if (user == null) {
            return null;
        }
        
        return UserSummaryDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getMobileNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .build();
    }
} 
package com.nearprop.service.impl;

import com.nearprop.dto.CreateVisitDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.dto.UpdateVisitStatusDto;
import com.nearprop.dto.UserSummaryDto;
import com.nearprop.dto.VisitDto;
import com.nearprop.entity.Property;
import com.nearprop.entity.Role;
import com.nearprop.entity.User;
import com.nearprop.entity.Visit;
import com.nearprop.entity.Visit.VisitStatus;
import com.nearprop.exception.ResourceNotFoundException;
import com.nearprop.exception.ScheduleConflictException;
import com.nearprop.exception.UnauthorizedException;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.repository.UserRepository;
import com.nearprop.repository.VisitRepository;
import com.nearprop.service.VisitService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VisitServiceImpl implements VisitService {

    private final VisitRepository visitRepository;
    private final PropertyRepository propertyRepository;
    private final UserRepository userRepository;

    private static final int VISIT_DURATION_MINUTES = 60; // Default visit duration in minutes

    @Override
    @Transactional
    public VisitDto scheduleVisit(CreateVisitDto visitDto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        Property property = propertyRepository.findById(visitDto.getPropertyId())
                .orElseThrow(() -> new ResourceNotFoundException("Property not found with id: " + visitDto.getPropertyId()));
        
        // Check if the property is approved and available
        if (!property.isApproved()) {
            throw new IllegalStateException("Cannot schedule a visit to an unapproved property");
        }
        
        // Check for scheduling conflicts
        if (hasScheduleConflict(property.getId(), visitDto.getScheduledTime())) {
            throw new ScheduleConflictException("This time slot is already booked. Please select another time.");
        }
        
        Visit visit = Visit.builder()
                .property(property)
                .user(user)
                .scheduledTime(visitDto.getScheduledTime())
                .notes(visitDto.getNotes())
                .status(VisitStatus.PENDING)
                .build();
                
        return mapToDto(visitRepository.save(visit));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "visits", key = "#visitId")
    public VisitDto getVisit(Long visitId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + visitId));
                
        return mapToDto(visit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitDto> getAllVisits(Pageable pageable) {
        return visitRepository.findAll(pageable)
                .map(this::mapToDto);
    }


    @Override
    @Transactional
    public VisitDto updateVisitStatus(Long visitId, UpdateVisitStatusDto updateDto, Long userId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + visitId));
        
        // Check authorization - only owner or the user who scheduled can update
        boolean isOwner = visit.getProperty().getOwner().getId().equals(userId);
        boolean isVisitor = visit.getUser().getId().equals(userId);
        
        // Get the user with roles
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        boolean isSellerOrAdvisor = user.getRoles().contains(Role.SELLER) || 
                                   user.getRoles().contains(Role.ADVISOR) ||
                                   user.getRoles().contains(Role.ADMIN);
                                   
        if (!isOwner && !isVisitor) {
            throw new UnauthorizedException("You are not authorized to update this visit");
        }
        
        // If it's a customer (visitor) and they're not a seller/advisor, they can only cancel
        if (isVisitor && !isSellerOrAdvisor && updateDto.getStatus() != VisitStatus.CANCELLED) {
            throw new UnauthorizedException("Customers can only cancel visits");
        }
        
        // Ensure property owners can update any status on their properties
        if (isOwner && !isSellerOrAdvisor) {
            // Validate the property owner has appropriate permissions
            User owner = visit.getProperty().getOwner();
            boolean ownerIsSellerOrAdvisor = owner.getRoles().contains(Role.SELLER) || 
                                           owner.getRoles().contains(Role.ADVISOR) ||
                                           owner.getRoles().contains(Role.ADMIN);
            
            if (!ownerIsSellerOrAdvisor) {
                throw new UnauthorizedException("Property owner does not have sufficient permissions to update visit status");
            }
        }
        
        visit.setStatus(updateDto.getStatus());
        
        if (updateDto.getNotes() != null) {
            // Append notes with a timestamp
            String existingNotes = visit.getNotes() != null ? visit.getNotes() : "";
            String updaterName = isOwner ? "Owner" : "Customer";
            visit.setNotes(existingNotes + "\n[" + LocalDateTime.now() + " - " + updaterName + "]: " + updateDto.getNotes());
        }
        
        return mapToDto(visitRepository.save(visit));
    }

    @Override
    @Transactional
    public void cancelVisit(Long visitId, Long userId) {
        Visit visit = visitRepository.findById(visitId)
                .orElseThrow(() -> new ResourceNotFoundException("Visit not found with id: " + visitId));
        
        boolean isOwner = visit.getProperty().getOwner().getId().equals(userId);
        boolean isVisitor = visit.getUser().getId().equals(userId);
        
        // Get the user with roles
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
                
        boolean isSellerOrAdvisor = user.getRoles().contains(Role.SELLER) || 
                                   user.getRoles().contains(Role.ADVISOR) ||
                                   user.getRoles().contains(Role.ADMIN);
        
        // Anyone with the right role should be able to cancel
        if (!isOwner && !isVisitor && !isSellerOrAdvisor) {
            throw new UnauthorizedException("You are not authorized to cancel this visit");
        }
        
        // Always allow cancellations regardless of user role
        visit.setStatus(VisitStatus.CANCELLED);
        
        // Add a note about who cancelled
        String canceller;
        if (isSellerOrAdvisor) {
            canceller = isOwner ? "Owner (Seller/Advisor)" : (isVisitor ? "Customer (Seller/Advisor)" : "Administrator");
        } else {
            canceller = isOwner ? "Owner" : "Customer";
        }
        
        visit.setNotes((visit.getNotes() != null ? visit.getNotes() + "\n" : "") 
                + "[" + LocalDateTime.now() + " - Cancelled by " + canceller + "]");
                
        visitRepository.save(visit);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = "visits", key = "'user_' + #userId + '_' + #pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<VisitDto> getUserVisits(Long userId, Pageable pageable) {
        return visitRepository.findByUserId(userId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitDto> getPropertyVisits(Long propertyId, Pageable pageable) {
        return visitRepository.findByPropertyId(propertyId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitDto> getOwnerVisits(Long ownerId, Pageable pageable) {
        return visitRepository.findByPropertyOwnerId(ownerId, pageable).map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VisitDto> getVisitsByStatus(VisitStatus status, Pageable pageable) {
        // Add debug logging to verify the query is executing correctly
        Page<Visit> visits = visitRepository.findByStatus(status, pageable);
        
        if (visits == null || visits.isEmpty()) {
            // If no visits found with this status, return empty page instead of null
            return Page.empty(pageable);
        }
        
        return visits.map(this::mapToDto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VisitDto> checkAvailability(Long propertyId, LocalDateTime startTime, LocalDateTime endTime) {
        return visitRepository.findByPropertyIdAndScheduledTimeBetween(propertyId, startTime, endTime)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasScheduleConflict(Long propertyId, LocalDateTime scheduledTime) {
        LocalDateTime visitStart = scheduledTime.minusMinutes(VISIT_DURATION_MINUTES / 2);
        LocalDateTime visitEnd = scheduledTime.plusMinutes(VISIT_DURATION_MINUTES / 2);
        
        List<Visit> conflicts = visitRepository.findScheduleConflicts(propertyId, visitStart, visitEnd);
        return !conflicts.isEmpty();
    }
    
    private VisitDto mapToDto(Visit visit) {
        return VisitDto.builder()
                .id(visit.getId())
                .property(mapToPropertyDto(visit.getProperty()))
                .user(mapToUserSummaryDto(visit.getUser()))
                .scheduledTime(visit.getScheduledTime())
                .notes(visit.getNotes())
                .status(visit.getStatus())
                .createdAt(visit.getCreatedAt())
                .updatedAt(visit.getUpdatedAt())
                .build();
    }
    
    private PropertyDto mapToPropertyDto(Property property) {
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
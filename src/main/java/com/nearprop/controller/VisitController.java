package com.nearprop.controller;

import com.nearprop.dto.CreateVisitDto;
import com.nearprop.dto.UpdateVisitStatusDto;
import com.nearprop.dto.VisitDto;
import com.nearprop.entity.User;
import com.nearprop.entity.Visit.VisitStatus;
import com.nearprop.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/visits")
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VisitDto> scheduleVisit(
            @Valid @RequestBody CreateVisitDto visitDto,
            @AuthenticationPrincipal User currentUser) {
        VisitDto visitResponse = visitService.scheduleVisit(visitDto, currentUser.getId());
        return new ResponseEntity<>(visitResponse, HttpStatus.CREATED);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<VisitDto>> getAllVisitsForAdmin(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction) {

        Sort sort = direction.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<VisitDto> visits = visitService.getAllVisits(pageable);
        return ResponseEntity.ok(visits);
    }


    @GetMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VisitDto> getVisit(
            @PathVariable("id") Long visitId,
            @AuthenticationPrincipal User currentUser) {
        VisitDto visit = visitService.getVisit(visitId);
        return ResponseEntity.ok(visit);
    }
    
    @PutMapping("/{id}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VisitDto> updateVisitStatus(
            @PathVariable("id") Long visitId,
            @Valid @RequestBody UpdateVisitStatusDto updateDto,
            @AuthenticationPrincipal User currentUser) {
        VisitDto updatedVisit = visitService.updateVisitStatus(visitId, updateDto, currentUser.getId());
        return ResponseEntity.ok(updatedVisit);
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> cancelVisit(
            @PathVariable("id") Long visitId,
            @AuthenticationPrincipal User currentUser) {
        visitService.cancelVisit(visitId, currentUser.getId());
        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<VisitDto> cancelVisitPost(
            @PathVariable("id") Long visitId,
            @AuthenticationPrincipal User currentUser) {
        visitService.cancelVisit(visitId, currentUser.getId());
        VisitDto visit = visitService.getVisit(visitId);
        return ResponseEntity.ok(visit);
    }
    
    @GetMapping("/my-visits")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<VisitDto>> getUserVisits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VisitDto> visits = visitService.getUserVisits(currentUser.getId(), pageable);
        return ResponseEntity.ok(visits);
    }
    
    @GetMapping("/my-properties")
    @PreAuthorize("hasAnyRole('ADVISOR', 'SELLER', 'ADMIN')")
    public ResponseEntity<Page<VisitDto>> getOwnerVisits(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VisitDto> visits = visitService.getOwnerVisits(currentUser.getId(), pageable);
        return ResponseEntity.ok(visits);
    }
    
    @GetMapping("/property/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<VisitDto>> getPropertyVisits(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VisitDto> visits = visitService.getPropertyVisits(propertyId, pageable);
        return ResponseEntity.ok(visits);
    }
    
    @GetMapping("/check-availability/{propertyId}")
    public ResponseEntity<List<VisitDto>> checkAvailability(
            @PathVariable("propertyId") Long propertyId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        List<VisitDto> bookedSlots = visitService.checkAvailability(propertyId, startTime, endTime);
        return ResponseEntity.ok(bookedSlots);
    }
    
    @GetMapping("/admin/by-status")
    @PreAuthorize("hasAnyRole('ADMIN', 'SELLER', 'ADVISOR')")
    public ResponseEntity<Page<VisitDto>> getVisitsByStatus(
            @RequestParam VisitStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "scheduledTime") String sortBy,
            @RequestParam(defaultValue = "ASC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<VisitDto> visits = visitService.getVisitsByStatus(status, pageable);
        return ResponseEntity.ok(visits);
    }
}
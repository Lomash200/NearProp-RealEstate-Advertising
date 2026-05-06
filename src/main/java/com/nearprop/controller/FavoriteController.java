package com.nearprop.controller;

import com.nearprop.dto.FavoriteResponseDto;
import com.nearprop.dto.PropertyDto;
import com.nearprop.entity.User;
import com.nearprop.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriteResponseDto> addToFavorites(
            @PathVariable("propertyId") Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        FavoriteResponseDto response = favoriteService.addToFavorites(propertyId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<FavoriteResponseDto> removeFromFavorites(
            @PathVariable("propertyId") Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        FavoriteResponseDto response = favoriteService.removeFromFavorites(propertyId, currentUser.getId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{propertyId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Boolean>> isFavorite(
            @PathVariable("propertyId") Long propertyId,
            @AuthenticationPrincipal User currentUser) {
        boolean isFavorite = favoriteService.isFavorite(propertyId, currentUser.getId());
        Map<String, Boolean> response = new HashMap<>();
        response.put("favorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<PropertyDto>> getUserFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String direction,
            @AuthenticationPrincipal User currentUser) {
        Sort sort = direction.equalsIgnoreCase("ASC") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<PropertyDto> favorites = favoriteService.getUserFavorites(currentUser.getId(), pageable);
        return ResponseEntity.ok(favorites);
    }
} 
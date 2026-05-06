package com.nearprop.service;

import com.nearprop.dto.FavoriteResponseDto;
import com.nearprop.dto.PropertyDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    
    FavoriteResponseDto addToFavorites(Long propertyId, Long userId);
    
    FavoriteResponseDto removeFromFavorites(Long propertyId, Long userId);
    
    boolean isFavorite(Long propertyId, Long userId);
    
    Page<PropertyDto> getUserFavorites(Long userId, Pageable pageable);
} 
package com.nearprop.service;

import com.nearprop.dto.PropertyInquiryDto;
import java.util.List;

public interface PropertyInquiryService {
    PropertyInquiryDto createInquiry(PropertyInquiryDto dto);
    List<PropertyInquiryDto> getAllInquiriesForAdmin();
    List<PropertyInquiryDto> getAllInquiriesForFranchise(Long districtId);
    PropertyInquiryDto updateInquiryStatus(Long inquiryId, PropertyInquiryDto.StatusHistoryDto statusDto, Long updatedBy);
    PropertyInquiryDto getInquiry(Long id);
} 
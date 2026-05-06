package com.nearprop.service.franchisee;

import com.nearprop.dto.franchisee.FranchiseeProfileDto;

public interface FranchiseeProfileService {
    
    /**
     * Get the complete profile of a franchisee including bank details, district assignments, etc.
     */
    FranchiseeProfileDto getCompleteProfile(Long userId);
}

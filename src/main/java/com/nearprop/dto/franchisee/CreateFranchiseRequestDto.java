package com.nearprop.dto.franchisee;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateFranchiseRequestDto {
    
    @NotNull(message = "District ID is required")
    private Long districtId;
    
    @NotBlank(message = "Business name is required")
    private String businessName;
    
    @NotBlank(message = "Business address is required")
    private String businessAddress;
    
    private String businessRegistrationNumber;
    
    private String gstNumber;
    
    @NotBlank(message = "PAN number is required")
    private String panNumber;
    
    @NotBlank(message = "Aadhar number is required")
    private String aadharNumber;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    private String contactPhone;
    
    @Min(value = 0, message = "Years of experience cannot be negative")
    private Integer yearsOfExperience;
    
    // Documents will be handled separately in multipart form
    private List<MultipartFile> documents;
} 
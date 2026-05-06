package com.nearprop.dto.auth;

import com.nearprop.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^\\+?[1-9]\\d{9,14}$", message = "Invalid mobile number format")
    private String mobileNumber;

    @Email(message = "Invalid email format")
    private String email;

    private String address;
    private String district;
    
    private Set<Role> roles;
} 
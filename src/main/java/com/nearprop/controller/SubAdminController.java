package com.nearprop.controller;

import com.nearprop.dto.CreateSubAdminDto;
import com.nearprop.dto.UserDto;
import com.nearprop.entity.User;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.SubAdminAnalyticDto;
import com.nearprop.repository.PropertyRepository;
import com.nearprop.service.AdminService;
import com.nearprop.service.SubAdminService;
import com.nearprop.service.UserManagementService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/subAdmin")
@Slf4j
@RequiredArgsConstructor
public class SubAdminController {
	
	private final PropertyRepository propertyRepository;
	private final SubAdminService subAdminService;

	@GetMapping("/health-check-2")
	public void demo()
	{
		System.out.println("Hello");
	}

	@GetMapping("/getAllProperties")
	@PreAuthorize("hasRole('SUBADMIN')")
	public ResponseEntity<ApiResponse<SubAdminAnalyticDto>> getAllDetails()
	{
		SubAdminAnalyticDto subAdminAnalyticDto=subAdminService.getSubAdminAnalytics();
        return ResponseEntity.ok(ApiResponse.success("Data Retrived Succesfully",subAdminAnalyticDto));
	}

	@PostMapping("/create")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<ApiResponse<UserDto>> createSubAdmin(@Valid @RequestBody CreateSubAdminDto subAdminDto) {
		log.info("Received request to create admin user with name: {}", subAdminDto.getName());

		User subAdminUser = subAdminService.createSubAdmin(subAdminDto);

		UserDto userDto = UserDto.builder()
				.id(subAdminUser.getId())
				.name(subAdminUser.getName())
				.email(subAdminUser.getEmail())
				.mobileNumber(subAdminUser.getMobileNumber())
				.roles(subAdminUser.getRoles())
				.build();

		return ResponseEntity.ok(ApiResponse.success("SubAdmin created successfully", userDto));
	}

}

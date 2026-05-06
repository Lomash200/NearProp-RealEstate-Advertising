package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.UserDetailDto;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/public/developers")
@RequiredArgsConstructor
public class DeveloperPublicController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getDevelopers() {

        List<UserDetailDto> developers = userService.getPublicDevelopers();

        return ResponseEntity.ok(
                ApiResponse.success("Developers fetched successfully", developers)
        );
    }
}
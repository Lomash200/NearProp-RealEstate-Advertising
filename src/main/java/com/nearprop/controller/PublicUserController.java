//package com.nearprop.controller;
//
//import com.nearprop.dto.ApiResponse;
//import com.nearprop.dto.UserDetailDto;
//import com.nearprop.service.UserService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//
//@RestController
//@RequestMapping("/public/users")
//@RequiredArgsConstructor
//public class PublicUserController {
//
//    private final UserService userService;
//
//    // ⚠️ FAKE DELETE VIA GET – DOES NOT TOUCH DATABASE
//    @GetMapping
//    public ResponseEntity<ApiResponse<Void>> fakeDeleteUser() {
//        return ResponseEntity.ok(
//                ApiResponse.success("User deleted successfully", null)
//        );
//    }
//
//    // ✅ NEW METHOD – ADVISORS API
//    @GetMapping("/advisors")
//    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getPublicAdvisors() {
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        "Advisors fetched successfully",
//                        userService.getPublicAdvisors()
//                )
//        );
//    }
//
//    // ✅ ADD THIS (developer public list)
//    @GetMapping("/developers")
//    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getPublicDevelopers() {
//        return ResponseEntity.ok(
//                ApiResponse.success(
//                        "Developers fetched successfully",
//                        userService.getPublicDevelopers()
//                )
//        );
//    }
//}

package com.nearprop.controller;

import com.nearprop.dto.ApiResponse;
import com.nearprop.dto.UserDetailDto;
import com.nearprop.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/public/users")
@RequiredArgsConstructor
@Slf4j
public class PublicUserController {

    private final UserService userService;

    // ✅ FAKE DELETE VIA GET – DOES NOT TOUCH DATABASE
    @GetMapping
    public ResponseEntity<ApiResponse<Void>> fakeDeleteUser() {
        return ResponseEntity.ok(
                ApiResponse.success("User deleted successfully", null)
        );
    }

    // ✅ NEW: Advisors API - Only those with active PROFILE subscription
    @GetMapping("/advisors")
    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getPublicAdvisors() {
        try {
            log.info("Request received for public advisors with PROFILE subscription");
            List<UserDetailDto> advisors = userService.getPublicAdvisors();

            return ResponseEntity.ok(
                    ApiResponse.<List<UserDetailDto>>builder()
                            .success(true)
                            .message("Advisors fetched successfully")
                            .data(advisors)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error fetching public advisors: ", e);
            return ResponseEntity.ok(
                    ApiResponse.<List<UserDetailDto>>builder()
                            .success(false)
                            .message("Error fetching advisors: " + e.getMessage())
                            .data(new ArrayList<>())
                            .build()
            );
        }
    }

    // ✅ NEW: Developers API - Only those with active PROFILE subscription
    @GetMapping("/developers")
    public ResponseEntity<ApiResponse<List<UserDetailDto>>> getPublicDevelopers() {
        try {
            log.info("Request received for public developers with PROFILE subscription");
            List<UserDetailDto> developers = userService.getPublicDevelopers();

            return ResponseEntity.ok(
                    ApiResponse.<List<UserDetailDto>>builder()
                            .success(true)
                            .message("Developers fetched successfully")
                            .data(developers)
                            .build()
            );
        } catch (Exception e) {
            log.error("Error fetching public developers: ", e);
            return ResponseEntity.ok(
                    ApiResponse.<List<UserDetailDto>>builder()
                            .success(false)
                            .message("Error fetching developers: " + e.getMessage())
                            .data(new ArrayList<>())
                            .build()
            );
        }
    }
}

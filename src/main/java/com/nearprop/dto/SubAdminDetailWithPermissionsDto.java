package com.nearprop.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class SubAdminDetailWithPermissionsDto {
    private Long id;
    private String name;
    private String email;
    private String mobileNumber;
    private String address;
    private String profileImageUrl;
    private List<String> roles;
    private List<String> permissionUsers; // PermissionUser
    private boolean mobileVerified;
    private List<String> actions; // Action
    private Map<String, List<String>> permissions; // e.g. {"PROPERTY": ["CREATE","UPDATE"]}


}
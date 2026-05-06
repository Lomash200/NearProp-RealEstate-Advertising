package com.nearprop.entity;


public enum Role {
    USER,
    SELLER,
    ADVISOR,
    DEVELOPER,
    FRANCHISEE,
    SUBADMIN,
    ADMIN;

    public String getName() {
        return name();
    }
    
    public boolean isNonExpiring() {
        return this == USER;
    }

    public boolean allowsMultipleDevices() {
        return this == FRANCHISEE;
    }

    public boolean requiresAdminApproval() {
        return this == DEVELOPER || this == ADVISOR;
    }

    public boolean requiresDocuments() {
        return this == DEVELOPER || this == ADVISOR;
    }
} 
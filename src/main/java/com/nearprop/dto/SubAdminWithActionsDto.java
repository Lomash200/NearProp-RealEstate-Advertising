package com.nearprop.dto;

import com.nearprop.enums.Action;
import com.nearprop.enums.PermissionUser;
import lombok.Data;

import java.util.List;

@Data
public class SubAdminWithActionsDto {
    private Long subAdminId;
    private String name; // Or other user details you want
    private PermissionUser module;
    private List<Action> actions;
}
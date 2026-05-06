package com.nearprop.dto;

import com.nearprop.enums.Action;
import com.nearprop.enums.PermissionUser;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
@Data
public class AssignPermissionDto {

    @NotNull(message = "SubAdmin Id cannot be null")
    private Long subAdminId; // SubAdmin user ID
//    @NotNull(message = "Module cannot be null")
//    private PermissionUser module; // PROPERTY, ADVERTISEMENT, FRANCHISEE
//    @NotNull(message = "Actions Cannot be null")
//    @Size(min =1, message ="At least one action must be Provided")
//    private List<Action> actions; // e.g., [VIEW, DELETE]

    @NotNull(message = "Module permissions cannot be null")
    @Size(min = 1, message = "At least one module permission must be provided")
    private List<ModulePermission> modulePermissions; // List of module-action pairs

    @Data
    public static class ModulePermission {
        @NotNull(message = "Module cannot be null")
        private PermissionUser module; // PROPERTY, ADVERTISEMENT, FRANCHISEE

        @NotNull(message = "Actions cannot be null")
        @Size(min = 1, message = "At least one action must be provided")
        private List<Action> actions; // e.g., [VIEW, CREATE, UPDATE, DELETE]
    }
}

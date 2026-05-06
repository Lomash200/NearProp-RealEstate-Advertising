    package com.nearprop.service;

    import java.util.ArrayList;
    import java.util.List;
    import java.util.Map;
    import java.util.Objects;
    import java.util.stream.Collectors;


    import com.nearprop.advertisement.repository.AdvertisementRepository;
    import com.nearprop.dto.SubAdminDetailWithPermissionsDto;
    import com.nearprop.dto.SubAdminWithActionsDto;
    import com.nearprop.dto.UserDetailDto;
    import com.nearprop.exception.DeleteBlockedException;
    import com.nearprop.repository.UserSessionRepository;
    import org.springframework.stereotype.Service;

    import com.nearprop.dto.AssignPermissionDto;
    import com.nearprop.entity.Role;
    import com.nearprop.entity.SubAdminPermission;
    import com.nearprop.entity.User;
    import com.nearprop.enums.Action;
    import com.nearprop.enums.PermissionUser;
    import com.nearprop.repository.SubAdminPermissionRepository;
    import com.nearprop.repository.UserRepository;

    import org.springframework.transaction.annotation.Transactional;
    import lombok.RequiredArgsConstructor;
    import lombok.extern.slf4j.Slf4j;

    @Service
    @RequiredArgsConstructor
    @Slf4j
    public class SubAdminPermissionService {

        private final SubAdminPermissionRepository permissionRepository;
        private final UserRepository userRepository;
        private final UserManagementService userManagementService; // Inject if needed
        private final UserSessionRepository userSessionRepository;
        private final AdvertisementRepository advertisementRepository;   // <-- NEW


//        @Transactional
//        public String deleteSubAdminById(Long subAdminId) {
//            User subAdmin = userRepository.findById(subAdminId)
//                    .orElseThrow(() -> new RuntimeException("SubAdmin not found with ID: " + subAdminId));
//
//            // 1. OPTIONAL: block delete if the sub-admin still owns advertisements
//            if (advertisementRepository.existsByUserId(subAdminId)) {
//                log.warn("Cannot delete SubAdmin {} – it still owns advertisements.", subAdminId);
//                throw new DeleteBlockedException(
//                        "SubAdmin cannot be deleted because it has active advertisements. " +
//                                "Remove or re-assign them first.");
//            }
//
//            // Delete all related permissions first
//            userSessionRepository.deleteByUser(subAdmin); // must exist
//            permissionRepository.deleteBySubAdmin(subAdmin);
//            userRepository.delete(subAdmin);
//
//
//            // Then delete SubAdmin
//            userRepository.delete(subAdmin);
//
//            log.info("   SubAdmin deleted successfully with ID: {}", subAdminId);
//            return "SubAdmin deleted successfully with ID: " + subAdminId;
//        }


        @Transactional
        public String deleteSubAdminById(Long subAdminId) {

            User subAdmin = userRepository.findById(subAdminId)
                    .orElseThrow(() ->
                            new RuntimeException("SubAdmin not found with ID: " + subAdminId));

            // 🔐 Safety check – ensure it is actually a SUBADMIN
            if (!subAdmin.getRoles().contains(Role.SUBADMIN)) {
                throw new RuntimeException("User is not a SubAdmin");
            }

            // 1️⃣ OPTIONAL: block if owns advertisements
            if (advertisementRepository.existsByUserId(subAdminId)) {
                log.warn("Cannot delete SubAdmin {} – it still owns advertisements.", subAdminId);
                throw new DeleteBlockedException(
                        "SubAdmin cannot be removed because it has active advertisements. " +
                                "Remove or re-assign them first."
                );
            }

            // 2️⃣ Remove SUBADMIN role only ✅
            subAdmin.getRoles().remove(Role.SUBADMIN);

            // 3️⃣ Optional cleanup
            permissionRepository.deleteBySubAdmin(subAdmin);
            userSessionRepository.deleteByUser(subAdmin);

            // 4️⃣ Save user (NOT delete)
            userRepository.save(subAdmin);

            log.info("SubAdmin role removed successfully for user ID: {}", subAdminId);
            return "SubAdmin removed successfully (role revoked)";
        }



        @Transactional(readOnly = true)
        public List<SubAdminDetailWithPermissionsDto> getAllSubAdminsWithPermissions() {
            log.info("Fetching all sub-admins with grouped permissions at {}", java.time.LocalDateTime.now());

            // Fetch all users with SUBADMIN role
            List<User> subAdmins = userRepository.findAllByRole(Role.SUBADMIN);

            return subAdmins.stream().map(subAdmin -> {
                // Fetch permissions for this SubAdmin
                List<SubAdminPermission> permissions = permissionRepository.findBySubAdminId(subAdmin.getId());

                //  Group permissions by module -> list of actions
                Map<String, List<String>> permissionsMap = permissions.stream()
                        .filter(p -> p.getModule() != null && p.getAction() != null)
                        .collect(Collectors.groupingBy(
                                p -> p.getModule().name(),
                                Collectors.mapping(p -> p.getAction().name(), Collectors.toList())
                        ));

                //  Optionally keep flattened lists for backward compatibility
                List<String> permissionUsers = new ArrayList<>(permissionsMap.keySet());
                List<String> actions = permissionsMap.values().stream()
                        .flatMap(List::stream)
                        .distinct()
                        .collect(Collectors.toList());

                //  Map to DTO
                SubAdminDetailWithPermissionsDto dto = new SubAdminDetailWithPermissionsDto();
                dto.setId(subAdmin.getId());
                dto.setName(subAdmin.getName());
                dto.setEmail(subAdmin.getEmail());
                dto.setMobileNumber(subAdmin.getMobileNumber());
                dto.setAddress(subAdmin.getAddress());
                dto.setProfileImageUrl(subAdmin.getProfileImageUrl());
                dto.setRoles(subAdmin.getRoles().stream()
                        .map(Role::name)
                        .collect(Collectors.toList()));
                dto.setMobileVerified(subAdmin.isMobileVerified());

                //  New structured permissions
                dto.setPermissions(permissionsMap);

                return dto;
            }).collect(Collectors.toList());
        }


        @Transactional(readOnly = true)
        public SubAdminDetailWithPermissionsDto  getSubAdminDetailsWithPermissions(Long subAdminId) {
            // Fetch user details
            UserDetailDto userDetail = userManagementService.getUserDetailById(subAdminId);
            if (userDetail == null) {
                log.warn("SubAdmin not found with ID: {}", subAdminId);
                throw new RuntimeException("SubAdmin not found");
            }

            // Fetch permissions
            User subAdmin = userRepository.findById(subAdminId)
                    .orElseThrow(() -> new RuntimeException("SubAdmin not found"));
            List<SubAdminPermission> permissions = permissionRepository.findBySubAdmin(subAdmin);

            // Extract unique PermissionUsers and Actions
            List<String> permissionUsers = permissions.stream()
                    .map(SubAdminPermission::getModule)
                    .filter(Objects::nonNull)
                    .map(PermissionUser::name)
                    .distinct()
                    .collect(Collectors.toList());

            List<String> actions = permissions.stream()
                    .map(SubAdminPermission::getAction)
                    .filter(Objects::nonNull)
                    .map(Action::name)
                    .distinct()
                    .collect(Collectors.toList());

            // Map to DTO
            SubAdminDetailWithPermissionsDto dto = new SubAdminDetailWithPermissionsDto();
            dto.setId(userDetail.getId());
            dto.setName(userDetail.getName());
            dto.setEmail(userDetail.getEmail());
            dto.setMobileNumber(userDetail.getMobileNumber());
            dto.setAddress(userDetail.getAddress());
            dto.setProfileImageUrl(userDetail.getProfileImageUrl());
    // Convert roles to List<String> - Adjust based on actual type of getRoles()
            // If getRoles() returns Set<Role> or List<Role>
            dto.setRoles(userDetail.getRoles().stream()
                    .map(role -> role instanceof Role ? ((Role) role).name() : role.toString())
                    .collect(Collectors.toList()));
            dto.setMobileVerified(userDetail.isMobileVerified());
            dto.setPermissionUsers(permissionUsers);
            dto.setActions(actions);

            return dto;
        }


        public List<SubAdminWithActionsDto> getSubAdminsWithActionsByModule(PermissionUser module) {
            // Fetch all permissions for the given module
            List<SubAdminPermission> permissions = permissionRepository.findByModule(module);

            // Group by subAdminId and collect actions
            Map<Long, List<Action>> subAdminActions = permissions.stream()
                    .collect(Collectors.groupingBy(
                            permission -> permission.getSubAdmin().getId(),
                            Collectors.mapping(SubAdminPermission::getAction, Collectors.toList())
                    ));

            // Convert to DTO with subadmin details
            return subAdminActions.entrySet().stream()
                    .map(entry -> {
                        Long subAdminId = entry.getKey();
                        User subAdmin = userRepository.findById(subAdminId)
                                .orElseThrow(() -> new RuntimeException("SubAdmin not found: " + subAdminId));
                        SubAdminWithActionsDto dto = new SubAdminWithActionsDto();
                        dto.setSubAdminId(subAdminId);
                        dto.setName(subAdmin.getName()); // Add other fields as needed
                        dto.setModule(module); // Same module for all
                        dto.setActions(entry.getValue());
                        return dto;
                    })
                    .collect(Collectors.toList());
        }

        public List<UserDetailDto> getSubAdminsByModule(PermissionUser module) {
            // Fetch all permissions for the given module
            List<SubAdminPermission> permissions = permissionRepository.findByModule(module);

            // Extract unique subAdmin IDs
            List<Long> subAdminIds = permissions.stream()
                    .map(permission -> permission.getSubAdmin().getId())
                    .distinct()
                    .collect(Collectors.toList());

            // Fetch UserDetailDto for these subAdmin IDs
            return subAdminIds.stream()
                    .map(id -> userManagementService.getUserDetailById(id)) // Assuming this method exists
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }

    //    @Transactional
    //    public void assignPermissions(AssignPermissionDto dto) {
    //        userRepository.findById(dto.getSubAdminId()).ifPresentOrElse(subAdmin -> {
    //            for (Action action : dto.getActions()) {
    //                SubAdminPermission permission = SubAdminPermission.builder()
    //                        .subAdmin(subAdmin)
    //                        .module(dto.getModule())
    //                        .action(action)
    //                        .build();
    //                permissionRepository.save(permission);
    //            }
    //        }, () -> {
    //            log.warn("SubAdmin not found with ID: {}", dto.getSubAdminId());
    //            // Instead of throwing exception, just log or handle gracefully
    //        });
    //    }

    //    @Transactional
    //    public void assignPermissions(AssignPermissionDto dto) {
    //        userRepository.findById(dto.getSubAdminId()).ifPresentOrElse(subAdmin -> {
    //            // Iterate over each module-action pair
    //            for (AssignPermissionDto.ModulePermission modulePermission : dto.getModulePermissions()) {
    //                PermissionUser module = modulePermission.getModule();
    //                for (Action action : modulePermission.getActions()) {
    //                    SubAdminPermission permission = SubAdminPermission.builder()
    //                            .subAdmin(subAdmin)
    //                            .module(module)
    //                            .action(action)
    //                            .build();
    //                    permissionRepository.save(permission);
    //                    log.info("Assigned permission for subAdmin ID: {}, module: {}, action: {}",
    //                            dto.getSubAdminId(), module, action);
    //                }
    //            }
    //        }, () -> {
    //            log.warn("SubAdmin not found with ID: {}", dto.getSubAdminId());
    //            throw new RuntimeException("SubAdmin not found with ID: " + dto.getSubAdminId());
    //        });
    //    }

        @Transactional
        public void assignPermissions(AssignPermissionDto dto) {
            User subAdmin = userRepository.findById(dto.getSubAdminId()).orElse(null);
            if (subAdmin == null) {
                log.warn("SubAdmin not found with ID: {}", dto.getSubAdminId());
                throw new RuntimeException("SubAdmin not found with ID: " + dto.getSubAdminId());
            }

            // Check if permissions already exist for any module
            for (AssignPermissionDto.ModulePermission modulePermission : dto.getModulePermissions()) {
                PermissionUser module = modulePermission.getModule();
                List<SubAdminPermission> existingPermissions = permissionRepository.findBySubAdminAndModule(subAdmin, module);
                if (!existingPermissions.isEmpty()) {
                    log.warn("Permissions already exist for subAdmin ID: {}, module: {}. Use PUT /admin/permissions/update to update.",
                            dto.getSubAdminId(), module);
                    throw new RuntimeException("Permissions already assigned for module: " + module + ". Use PUT to update.");
                }
            }

            // Assign new permissions if no duplicates
            for (AssignPermissionDto.ModulePermission modulePermission : dto.getModulePermissions()) {
                PermissionUser module = modulePermission.getModule();
                for (Action action : modulePermission.getActions()) {
                    SubAdminPermission permission = SubAdminPermission.builder()
                            .subAdmin(subAdmin)
                            .module(module)
                            .action(action)
                            .build();
                    permissionRepository.save(permission);
                    log.info("Assigned permission for subAdmin ID: {}, module: {}, action: {}",
                            dto.getSubAdminId(), module, action);
                }
            }
        }

    //    @Transactional
    //    public void updatePermissions(AssignPermissionDto dto) {
    //        User subAdmin = userRepository.findById(dto.getSubAdminId())
    //                .orElseThrow(() -> new RuntimeException("SubAdmin not found"));
    //
    //        // Delete existing permissions for this module
    //        permissionRepository.deleteBySubAdminAndModule(subAdmin, dto.getModule());
    //
    //        // Add new permissions
    //        for (Action action : dto.getActions()) {
    //            SubAdminPermission permission = SubAdminPermission.builder()
    //                    .subAdmin(subAdmin)
    //                    .module(dto.getModule())
    //                    .action(action)
    //                    .build();
    //            permissionRepository.save(permission);
    //        }
    //    }


    //    @Transactional
    //    public String updatePermissions(AssignPermissionDto dto) {
    //        return userRepository.findById(dto.getSubAdminId())
    //                .map(subAdmin -> {
    //                    // Delete existing permissions for this module
    //                    permissionRepository.deleteBySubAdminAndModule(subAdmin, dto.getModule());
    //                    for (Action action : dto.getActions()) {
    //                        SubAdminPermission permission = SubAdminPermission.builder()
    //                                .subAdmin(subAdmin)
    //                                .module(dto.getModule())
    //                                .action(action)
    //                                .build();
    //                        permissionRepository.save(permission);
    //                    }
    //                    return "Permissions updated successfully";
    //                })
    //                .orElse("SubAdmin not found with ID: " + dto.getSubAdminId());
    //    }

    //    @Transactional
    //    public void updatePermissions(AssignPermissionDto dto) {
    //        userRepository.findById(dto.getSubAdminId()).ifPresentOrElse(subAdmin -> {
    //            // Delete existing permissions for the subAdmin
    //            permissionRepository.deleteBySubAdminId(dto.getSubAdminId());
    //
    //            // Assign new permissions for each module-action pair
    //            for (AssignPermissionDto.ModulePermission modulePermission : dto.getModulePermissions()) {
    //                PermissionUser module = modulePermission.getModule();
    //                for (Action action : modulePermission.getActions()) {
    //                    SubAdminPermission permission = SubAdminPermission.builder()
    //                            .subAdmin(subAdmin)
    //                            .module(module)
    //                            .action(action)
    //                            .build();
    //                    permissionRepository.save(permission);
    //                    log.info("Updated permission for subAdmin ID: {}, module: {}, action: {}",
    //                            dto.getSubAdminId(), module, action);
    //                }
    //            }
    //        }, () -> {
    //            log.warn("SubAdmin not found with ID: {}", dto.getSubAdminId());
    //            throw new RuntimeException("SubAdmin not found");
    //        });
    //    }

        @Transactional
        public String updatePermissions(AssignPermissionDto dto) {
            return userRepository.findById(dto.getSubAdminId())
                    .map(subAdmin -> {
                        // Delete existing permissions only for the modules being updated
                        List<PermissionUser> modulesToUpdate = dto.getModulePermissions().stream()
                                .map(AssignPermissionDto.ModulePermission::getModule)
                                .collect(Collectors.toList());
                        for (PermissionUser module : modulesToUpdate) {
                            permissionRepository.deleteBySubAdminAndModule(subAdmin, module);
                        }

                        // Assign new permissions for each module-action pair
                        for (AssignPermissionDto.ModulePermission modulePermission : dto.getModulePermissions()) {
                            PermissionUser module = modulePermission.getModule();
                            for (Action action : modulePermission.getActions()) {
                                SubAdminPermission permission = SubAdminPermission.builder()
                                        .subAdmin(subAdmin)
                                        .module(module)
                                        .action(action)
                                        .build();
                                permissionRepository.save(permission);
                                log.info("Updated permission for subAdmin ID: {}, module: {}, action: {}",
                                        dto.getSubAdminId(), module, action);
                            }
                        }
                        return "Permissions updated successfully";
                    })
                    .orElseThrow(() -> {
                        log.warn("SubAdmin not found with ID: {}", dto.getSubAdminId());
                        return new RuntimeException("SubAdmin not found");
                    });
        }

        public boolean hasPermission(User subAdmin, PermissionUser module, Action action) {
            return permissionRepository.existsBySubAdminAndModuleAndAction(subAdmin, module, action);
        }

        public List<SubAdminPermission> getPermissions(Long subAdminId) {
            User subAdmin = userRepository.findById(subAdminId)
                    .orElseThrow(() -> new RuntimeException("SubAdmin not found"));
            System.out.println("SubAdmin: " + subAdmin);
            return permissionRepository.findBySubAdmin(subAdmin);
        }

        public boolean hasPermissionForAnyAction(User user, PermissionUser permissionUser) {
            boolean hasRole = user.getRoles().contains(Role.SUBADMIN);
            boolean hasPermission = permissionRepository.existsBySubAdmin_IdAndModule(user.getId(), permissionUser);

            // 🔎 Debug log
            log.info("Checking permission: userId={}, roleOk={}, module={}, permissionOk={}",
                    user.getId(), hasRole, permissionUser, hasPermission);

            return hasRole && hasPermission;
        }

        public List<SubAdminPermission> getPermissionsByModule(PermissionUser module) {
            return permissionRepository.findByModule(module);
        }

        @Transactional
        public String deletePermissionsByModule(Long subAdminId, PermissionUser module) {
            log.info("Attempting to delete permissions for subAdminId: {} and module: {}", subAdminId, module);
            User subAdmin = userRepository.findById(subAdminId).orElse(null);
            if (subAdmin == null) {
                log.warn("SubAdmin not found with ID: {}", subAdminId);
                throw new RuntimeException("SubAdmin not found with ID: " + subAdminId);
            }
            log.info("Found subAdmin: {}", subAdmin);
            permissionRepository.deleteBySubAdminAndModule(subAdmin, module);
            log.info("Deleted permissions for subAdmin ID: {}, module: {}", subAdminId, module);
            return "Permissions deleted successfully for module: " + module;
        }

    }

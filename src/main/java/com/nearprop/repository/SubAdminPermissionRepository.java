package com.nearprop.repository;

import java.util.List;

import io.lettuce.core.dynamic.annotation.Param;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.nearprop.entity.SubAdminPermission;
import com.nearprop.entity.User;
import com.nearprop.enums.Action;
import com.nearprop.enums.PermissionUser;

@Repository
public interface SubAdminPermissionRepository extends JpaRepository<SubAdminPermission, Long> {
    List<SubAdminPermission> findBySubAdmin(User subAdmin);

    boolean existsBySubAdminAndModuleAndAction(User subAdmin, PermissionUser module, Action action);

    void deleteBySubAdminAndModule(User subAdmin, PermissionUser module);

    boolean existsBySubAdmin_IdAndModule(Long subAdminId, PermissionUser module);

    List<SubAdminPermission> findByModule(PermissionUser module);

    @Query("SELECT p FROM SubAdminPermission p WHERE p.subAdmin.id = :subAdminId")
    List<SubAdminPermission> findBySubAdminId(@Param("subAdminId") Long subAdminId);

    void deleteBySubAdmin(User subAdmin);


    void deleteBySubAdminId(@NotNull(message = "SubAdmin Id cannot be null") Long subAdminId);

    List<SubAdminPermission> findBySubAdminAndModule(User subAdmin, PermissionUser module);
}
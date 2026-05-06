package com.nearprop.entity;

import com.nearprop.enums.Action;
import com.nearprop.enums.PermissionUser;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "sub_admin_permission")
public class SubAdminPermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private PermissionUser module; // PROPERTY, ADVERTISEMENT, etc.

        @Enumerated(EnumType.STRING)
    private Action action; // VIEW, CREATE, UPDATE, DELETE

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subadmin_id", nullable = false)
    private User subAdmin; // Linked with User having SUBADMIN role
}

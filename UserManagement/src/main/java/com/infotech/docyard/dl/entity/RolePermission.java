package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "ROLE_PERMISSIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RolePermission extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PERMISSION_ID", nullable = false)
    private Permission permission;

    public RolePermission() {
    }

    public RolePermission(Role role, Permission permission) {
        this.role = role;
        this.permission = permission;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
        this.setUpdatedBy(role.getUpdatedBy());
        this.setCreatedBy(role.getCreatedBy());
    }

    public RolePermission(Role role) {
        this.role = role;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
        this.setUpdatedBy(role.getUpdatedBy());
        this.setCreatedBy(role.getCreatedBy());
    }
}

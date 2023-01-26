package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.time.ZonedDateTime;

@EqualsAndHashCode(callSuper = false)
@Data
@Entity
@Table(name = "UM_ROLE_PERMISSIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class RolePermission extends BaseEntity {

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MA_ID", nullable = false)
    private ModuleAction moduleAction;

    public RolePermission() {
    }

    public RolePermission(Role role, ModuleAction moduleAction) {
        this.role = role;
        this.moduleAction = moduleAction;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
        this.setUpdatedBy(role.getUpdatedBy());
        this.setCreatedBy(role.getCreatedBy());
    }
}

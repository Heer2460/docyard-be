package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "GROUP_ROLES")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class GroupRole extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "GROUP_ID", nullable = false)
    private Group group;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ROLE_ID", nullable = false)
    private Role role;

    public GroupRole() {
    }

    public GroupRole(Group group,Role role) {
        this.group = group;
        this.role = role;
        this.setCreatedOn(ZonedDateTime.now());
        this.setUpdatedOn(ZonedDateTime.now());
        this.setUpdatedBy(group.getUpdatedBy());
        this.setCreatedBy(group.getCreatedBy());
    }
}

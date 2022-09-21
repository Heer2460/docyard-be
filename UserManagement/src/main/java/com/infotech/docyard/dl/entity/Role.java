package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "ROLES")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Role extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS", columnDefinition = "varchar(255) default 'Active'", nullable = false)
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @OneToMany(mappedBy = "role", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RolePermission> rolePermissions;

    public Role() {
    }

    public Role(Long id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return "code: " + code + "name: " + name + "status: " + status + "remarks" + remarks;
    }
}

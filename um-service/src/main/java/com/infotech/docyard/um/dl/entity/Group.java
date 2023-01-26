package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "UM_GROUPS") // GROUPS is reserve word for MySQL
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Group extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;
    @Column(name = "CODE" , unique = true)
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS", columnDefinition = "varchar(255) default 'Active'", nullable = false)
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @OneToMany(mappedBy = "group", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupRole> groupRoles;

    public Group() {
    }

    public Group(Long id) {
        this.setId(id);
    }

    @Override
    public String toString() {
        return "code: " + code + "name: " + name + "status: " + status + "remarks" + remarks;
    }
}

package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "GROUPS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Group extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "CODE")
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "REMARKS")
    private String remarks;

    @OneToMany(mappedBy = "group", fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GroupRole> groupRoles;

    public Group() {
    }
}
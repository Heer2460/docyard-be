package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
@Table(name = "UM_MODULES")
@JsonIgnoreProperties(value = {"hibernateLazyInitializer", "handler"}, ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Module extends BaseEntity {

    @Column(name = "NAME", unique = true, nullable = false)
    private String name;

    @Column(name = "SLUG", unique = true, nullable = false)
    private String slug;

    @Column(name = "ROUTE")
    private String route;

    @Column(name = "ICON")
    private String icon;

    @Column(name = "SEQ")
    private Integer seq;

    @Column(name = "STATUS", columnDefinition = "varchar(255) default 'Active'", nullable = false)
    private String status;

    @Column(name = "CAT_SLUG")
    private String catSlug;

    @Column(name = "CAT_NAME")
    private String catName;

    @Column(name = "PARENT_ID")
    private Long parentId;

    @OneToMany(mappedBy = "module", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ModuleAction> moduleActions;
}
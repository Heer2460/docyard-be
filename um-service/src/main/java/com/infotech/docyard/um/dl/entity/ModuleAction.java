package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@Entity
@Table(name = "MODULES_ACTIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class ModuleAction extends BaseEntity {

    @Column(name = "TITLE", unique = true, nullable = false)
    private String title;

    @Column(name = "SLUG", unique = true, nullable = false)
    private String slug;

    @Column(name = "SEQ", nullable = false)
    private Integer seq;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MODULE_ID", nullable = false)
    private Module module;

    public ModuleAction() {
    }

    public ModuleAction(Long id) {
        this.setId(id);
    }
}
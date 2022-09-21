package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

@Data
@Entity
@Table(name = "PERMISSIONS")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Permission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "NAME")
    private String name;

    public Permission() {
    }

}

package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Entity
@Table(name = "UM_DEPARTMENTS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class Department extends BaseEntity implements Serializable {

    @Column(name = "CODE" , unique = true)
    private String code;

    @Column(name = "NAME")
    private String name;

    @Column(name = "STATUS")
    private String status;

    public Department() {
    }
}
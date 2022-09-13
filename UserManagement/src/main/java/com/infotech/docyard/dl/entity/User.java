package com.infotech.docyard.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "USERS")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 7657451394244852266L;

    @Column(name = "USERNAME")
    private String username;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "NAME")
    private String name;

    @Column(name = "PHONE_NUMBER")
    private Long phoneNumber;

    @Column(name = "GROUP_ID")
    private Long groupId;

    @Column(name="STATUS")
    private String status;

    @Column(name="ADDRESS")
    private String address;

    @Column(name="DEPARTMENT_ID")
    private Long departmentId;

    public User() {
    }

}

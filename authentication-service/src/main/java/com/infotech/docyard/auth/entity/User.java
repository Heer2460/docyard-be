package com.infotech.docyard.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

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

    @Column(name = "IS_ONLINE", columnDefinition = "boolean default false")
    private Boolean online;

    @Column(name = "FORCE_PASSWORD_CHANGE", columnDefinition = "boolean default false")
    private Boolean forcePasswordChange;

    @Column(name = "LAST_LOGIN", columnDefinition = "TIMESTAMP")
    private ZonedDateTime lastLogin;

    @Column(name = "LAST_PASS_UPDATED_ON", columnDefinition = "TIMESTAMP")
    private ZonedDateTime lastPassUpdatedOn;

    @Column(name = "IS_PASS_EXPIRED", columnDefinition = "boolean default false")
    private Boolean passwordExpired;




    public User() {
    }

}
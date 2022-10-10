package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @Column(name = "MOBILE_NUMBER")
    private Long mobileNumber;

    @Column(name = "STATUS")
    private String status;

    @Column(name = "ADDRESS")
    private String address;

    @Column(name = "DEPARTMENT_IDS")
    private String departmentIds;

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

    @Column(name = "PASSWORD_REST_TOKEN")
    private String passwordResetToken;

    @JsonIgnore
    @Lob
    @Column(name = "PROFILE_PHOTO")
    private byte[] profilePhoto;

    @OneToOne(targetEntity = Group.class)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    public User() {
    }

}

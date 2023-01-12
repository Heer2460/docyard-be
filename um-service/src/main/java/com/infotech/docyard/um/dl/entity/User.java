package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "USER")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class User extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "STATUS")
    private String status;

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

    @Column(name = "UN_SUC_LOGIN_ATTEMPTS")
    private Integer unsuccessfulLoginAttempt;

    @OneToOne(targetEntity = Group.class)
    @JoinColumn(name = "GROUP_ID")
    private Group group;

    @Column(name = "DEPARTMENT_IDS")
    private String departmentIds;

    @OneToOne(targetEntity = UserProfile.class, orphanRemoval = true, fetch = FetchType.EAGER, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "PROFILE_ID")
    private UserProfile userProfile;
}

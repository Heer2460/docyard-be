package com.infotech.docyard.um.dl.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;

@Data
@Entity
@Table(name = "USER_PROFILE")
@JsonIgnoreProperties(ignoreUnknown = true)
@EqualsAndHashCode(callSuper = false)
public class UserProfile extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1111111L;

    @Column(name = "NAME")
    private String name;

    @Column(name = "EMAIL")
    private String email;

    @Column(name = "PHONE_NUMBER")
    private String phoneNumber;

    @Column(name = "MOBILE_NUMBER")
    private String mobileNumber;

    @Column(name = "ADDRESS")
    private String address;

    @JsonIgnore
    @Lob
    @Column(name = "PROFILE_PHOTO")
    private byte[] profilePhoto;
}

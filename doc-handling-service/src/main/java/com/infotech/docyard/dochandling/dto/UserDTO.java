package com.infotech.docyard.dochandling.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
public class UserDTO {

    public Long id;
    public ZonedDateTime createdOn;
    public ZonedDateTime updatedOn;
    public Long createdBy;
    public Long updatedBy;
    private String username;
    private String email;
    private String name;
    private Long phoneNumber;
    private Long mobileNumber;
    private Long groupId;
    private String status;
    private String address;
    private String password;
    private byte[] profilePhoto;
    private String groupName;
    private Boolean online;
    private Boolean forcePasswordChange;
    private ZonedDateTime lastLogin;
    private ZonedDateTime lastPassUpdatedOn;
    private Boolean passwordExpired;
    private String passwordResetToken;

    public UserDTO() {

    }

}

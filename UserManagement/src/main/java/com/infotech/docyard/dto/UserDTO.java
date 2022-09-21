package com.infotech.docyard.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.util.AppUtility;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.Column;
import javax.persistence.Lob;
import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO extends BaseDTO<UserDTO, User> implements Serializable {

    MultipartFile profilePhotoReceived;
    private Long id;
    private String username;
    private String email;
    private String name;
    private Long phoneNumber;
    private Long mobileNumber;
    private Long groupId;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> departmentIds;
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

    public UserDTO() {

    }

    public void setProfilePhotoFromDTO(User user, MultipartFile profilePhotoReceived) throws IOException {
        if (!AppUtility.isEmpty(profilePhotoReceived)) {
            user.setProfilePhoto(profilePhotoReceived.getBytes());
            this.setProfilePhoto(profilePhotoReceived.getBytes());
        }
    }

    @Override
    public User convertToEntity() throws IOException {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setPhoneNumber(this.phoneNumber);
        user.setMobileNumber(this.mobileNumber);
        user.setGroupId(this.groupId);
        if(!AppUtility.isEmpty(this.departmentIds)){
            user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));
        }
        setProfilePhotoFromDTO(user, this.profilePhotoReceived);
        user.setProfilePhoto(this.profilePhoto);
        user.setStatus(this.status);
        user.setAddress(this.address);
        user.setPassword(this.password);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedBy(this.getUpdatedBy());
        return user;
    }

    public User convertToEntityForUpdate() throws IOException {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setPhoneNumber(this.phoneNumber);
        user.setMobileNumber(this.mobileNumber);
        user.setGroupId(this.groupId);
        if(!AppUtility.isEmpty(this.departmentIds)){
            user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));
        }
        setProfilePhotoFromDTO(user, this.profilePhotoReceived);
        user.setProfilePhoto(this.profilePhoto);
        user.setStatus(this.status);
        user.setAddress(this.address);
        user.setPassword(this.password);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedBy(this.getUpdatedBy());
        return user;
    }

    @Override
    public void convertToDTO(User entity, boolean partialFill) {
        this.id = entity.getId();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.phoneNumber = entity.getPhoneNumber();
        this.mobileNumber = entity.getMobileNumber();
        this.groupId = entity.getGroupId();
        if (!AppUtility.isEmpty(entity.getDepartmentIds())) {
            this.setDepartmentIds(Arrays.asList(entity.getDepartmentIds().split(",")));
        }
        this.profilePhoto = entity.getProfilePhoto();
        this.status = entity.getStatus();
        this.address = entity.getAddress();
        this.profilePhoto = entity.getProfilePhoto();
        this.lastLogin = entity.getLastLogin();
        this.lastPassUpdatedOn = entity.getLastPassUpdatedOn();
        this.passwordExpired = entity.getPasswordExpired();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public UserDTO convertToNewDTO(User entity, boolean partialFill) {
        UserDTO userDTO = new UserDTO();
        userDTO.convertToDTO(entity, partialFill);
        return userDTO;
    }

}

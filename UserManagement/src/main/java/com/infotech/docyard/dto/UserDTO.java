package com.infotech.docyard.dto;

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
public class UserDTO  extends  BaseDTO<UserDTO, User> implements Serializable {

    private Long id;
    private String username;
    private String email;
    private String name;
    private Long phoneNumber;
    private Long mobileNumber;
    private Long groupId;
    private List<String> departmentIds;
    private String status;
    private String address;
    private String password;
    private byte[] profilePhoto;
    MultipartFile profilePhotoReceived;

    private String groupName;

    private String departmentName;

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
        user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));
        setProfilePhotoFromDTO(user, this.profilePhotoReceived);
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
        if(!AppUtility.isEmpty(entity.getDepartmentIds())) {
            this.setDepartmentIds(Arrays.asList(entity.getDepartmentIds().split(",")));
        }
        this.status = entity.getStatus();
        this.address = entity.getAddress();
        this.password = entity.getPassword();
        this.profilePhoto = entity.getProfilePhoto();
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

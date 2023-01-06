package com.infotech.docyard.um.dto;

import com.infotech.docyard.um.dl.entity.UserProfile;
import com.infotech.docyard.um.util.AppUtility;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
public class UserProfileDTO extends BaseDTO<UserProfileDTO, UserProfile> implements Serializable {

    private MultipartFile profilePhotoReceived;
    private Long id;
    private String email;
    private String name;
    private String phoneNumber;
    private String mobileNumber;
    private Long groupId;
    private String address;
    private byte[] profilePhoto;

    public UserProfileDTO() {
    }

    public void setProfilePhotoFromDTO(UserProfile user, MultipartFile profilePhotoReceived) throws IOException {
        if (!AppUtility.isEmpty(profilePhotoReceived)) {
            user.setProfilePhoto(profilePhotoReceived.getBytes());
            this.setProfilePhoto(profilePhotoReceived.getBytes());
        }
    }

    @Override
    public UserProfile convertToEntity() throws IOException {
        UserProfile user = new UserProfile();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setName(AppUtility.isEmpty(this.name) ? this.name : this.name.trim());
        user.setPhoneNumber(this.phoneNumber);
        user.setMobileNumber(this.mobileNumber);
        setProfilePhotoFromDTO(user, this.profilePhotoReceived);
        user.setProfilePhoto(this.profilePhoto);
        user.setAddress(this.address);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedBy(this.getUpdatedBy());

        return user;
    }

    public UserProfile convertToEntityForUpdate() throws IOException {
        UserProfile user = new UserProfile();
        user.setId(this.id);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setPhoneNumber(this.phoneNumber);
        user.setMobileNumber(this.mobileNumber);
        setProfilePhotoFromDTO(user, this.profilePhotoReceived);
        user.setProfilePhoto(this.profilePhoto);
        user.setAddress(this.address);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        user.setUpdatedBy(this.getUpdatedBy());
        user.setCreatedBy(this.getCreatedBy());

        return user;
    }

    @Override
    public void convertToDTO(UserProfile entity, boolean partialFill) {
        this.id = entity.getId();
        this.email = entity.getEmail();
        this.name = entity.getName();
        this.phoneNumber = entity.getPhoneNumber();
        this.mobileNumber = entity.getMobileNumber();
        this.address = entity.getAddress();
        this.profilePhoto = entity.getProfilePhoto();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public UserProfileDTO convertToNewDTO(UserProfile entity, boolean partialFill) {
        UserProfileDTO userDTO = new UserProfileDTO();
        userDTO.convertToDTO(entity, partialFill);
        return userDTO;
    }
}

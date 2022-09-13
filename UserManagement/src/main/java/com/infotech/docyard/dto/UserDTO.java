package com.infotech.docyard.dto;

import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class UserDTO  extends  BaseDTO<UserDTO, User>{

    private Long id;
    private String username;
    private String email;
    private String name;
    private Long phoneNumber;
    private Long groupId;
    private Long departmentId;
    private String status;
    private String address;
    private String password;
    private byte[] profilePicture;

    @Override
    public User convertToEntity() {
        User user = new User();
        user.setId(this.id);
        user.setUsername(this.username);
        user.setEmail(this.email);
        user.setName(this.name);
        user.setPhoneNumber(this.phoneNumber);
        user.setGroupId(this.groupId);
        user.setDepartmentId(this.departmentId);
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
        this.groupId = entity.getGroupId();
        this.departmentId = entity.getDepartmentId();
        this.status = entity.getStatus();
        this.address = entity.getAddress();
        this.password = entity.getPassword();
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

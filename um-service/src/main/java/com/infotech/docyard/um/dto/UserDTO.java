package com.infotech.docyard.um.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.infotech.docyard.um.dl.entity.Group;
import com.infotech.docyard.um.dl.entity.ModuleAction;
import com.infotech.docyard.um.dl.entity.User;
import com.infotech.docyard.um.util.AppUtility;
import lombok.Data;

import java.io.IOException;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class UserDTO extends BaseDTO<UserDTO, User> implements Serializable {

    private Long id;
    private String userName;
    private String password;
    private Boolean online;
    private String status;
    private Boolean forcePasswordChange;
    private ZonedDateTime lastLogin;
    private ZonedDateTime lastPassUpdatedOn;
    private Boolean passwordExpired;
    private String passwordResetToken;
    private Integer unsuccessfulLoginAttempt = 0;
    private Long groupId;
    private String groupName;
    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<String> departmentIds;
    private List<ModuleAction> moduleActionList;
    private List<ModuleDTO> moduleDTOList;
    private Long totalAllottedSize = 52428800000L;
    private Long totalUsedSpace;
    private String spaceUsedFormatted;
    private UserProfileDTO userProfile;

    public UserDTO() {
    }

    @Override
    public User convertToEntity() throws IOException {
        User user = new User();
        user.setId(this.id);
        user.setUserName(this.userName);
        user.setPassword(this.password);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setCreatedBy(this.getCreatedBy());
        user.setUpdatedBy(this.getUpdatedBy());
        user.setStatus(this.status);
        user.setUnsuccessfulLoginAttempt(this.unsuccessfulLoginAttempt);

        if(!AppUtility.isEmpty(this.userProfile))
            user.setUserProfile(this.userProfile.convertToEntity());

        if (!AppUtility.isEmpty(this.groupId))
            user.setGroup(new Group(this.groupId));

        if (!AppUtility.isEmpty(this.departmentIds))
            user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));

        return user;
    }


    public User convertToEntityUpdate(UserDTO userDTO) throws IOException {
        User user = new User();
        user.setId(this.id);
        user.setUserName(this.userName);
        user.setPassword(this.password);
        user.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        user.setCreatedBy(userDTO.getCreatedBy());
        user.setUpdatedBy(this.getUpdatedBy());
        user.setStatus(this.status);
        user.setUnsuccessfulLoginAttempt(this.unsuccessfulLoginAttempt);

        if(!AppUtility.isEmpty(this.userProfile))
            user.setUserProfile(this.userProfile.convertToEntity());

        if (!AppUtility.isEmpty(this.groupId))
            user.setGroup(new Group(this.groupId));

        if (!AppUtility.isEmpty(this.departmentIds))
            user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));

        return user;
    }


    public User convertToEntityForUpdate(User user,UserDTO userDTO) throws IOException {

        user.setId(this.id);
        user.setUserName(this.userName);
        user.setPassword(this.password);
        user.setPasswordResetToken(this.passwordResetToken);
        user.setStatus(this.status);

        if(!AppUtility.isEmpty(this.userProfile))
            user.setUserProfile(this.userProfile.convertToEntity());

        if (!AppUtility.isEmpty(this.groupId))
            user.setGroup(new Group(this.groupId));

        if (!AppUtility.isEmpty(this.departmentIds))
            user.setDepartmentIds(this.getDepartmentIds().stream().collect(Collectors.joining(",")));

        user.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        user.setUpdatedBy(userDTO.getUpdatedBy());
        user.setCreatedBy(this.getCreatedBy());
        user.setUnsuccessfulLoginAttempt(this.unsuccessfulLoginAttempt);
        return user;
    }

    @Override
    public void convertToDTO(User entity, boolean partialFill) {
        this.id = entity.getId();
        this.userName = entity.getUserName();
        this.lastLogin = entity.getLastLogin();
        this.lastPassUpdatedOn = entity.getLastPassUpdatedOn();
        this.passwordExpired = entity.getPasswordExpired();
        this.groupId = AppUtility.isEmpty(entity.getGroup()) ? null : entity.getGroup().getId();
        this.groupName = AppUtility.isEmpty(entity.getGroup()) ? null : entity.getGroup().getName();

        if (!AppUtility.isEmpty(entity.getDepartmentIds()))
            this.setDepartmentIds(Arrays.asList(entity.getDepartmentIds().split(",")));

        this.status = entity.getStatus();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
        this.password = entity.getPassword();
        this.unsuccessfulLoginAttempt = entity.getUnsuccessfulLoginAttempt();

        if(!partialFill) {
            this.userProfile = new UserProfileDTO();
            this.userProfile.convertToDTO(entity.getUserProfile(), partialFill);
        }
    }

    @Override
    public UserDTO convertToNewDTO(User entity, boolean partialFill) {
        UserDTO userDTO = new UserDTO();
        userDTO.convertToDTO(entity, partialFill);
        return userDTO;
    }


}

package com.infotech.docyard.um.dto;

import com.infotech.docyard.um.dl.entity.Permission;
import com.infotech.docyard.um.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class PermissionDTO extends BaseDTO<PermissionDTO, Permission> {

    private Long id;
    private String name;


    @Override
    public Permission convertToEntity() {
        Permission permission = new Permission();
        permission.setId(this.id);
        permission.setName(this.name);
        permission.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        permission.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        permission.setCreatedBy(this.getCreatedBy());
        permission.setUpdatedBy(this.getUpdatedBy());

        return permission;
    }

    @Override
    public void convertToDTO(Permission entity, boolean partialFill) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public PermissionDTO convertToNewDTO(Permission entity, boolean partialFill) {
        PermissionDTO permissionDTO = new PermissionDTO();
        permissionDTO.convertToDTO(entity, partialFill);
        return permissionDTO;
    }
}

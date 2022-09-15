package com.infotech.docyard.dto;

import com.infotech.docyard.dl.entity.Permission;
import com.infotech.docyard.dl.entity.Role;
import com.infotech.docyard.dl.entity.RolePermission;
import com.infotech.docyard.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class RoleDTO extends BaseDTO<RoleDTO, Role> {

    private Long id;
    private String code;
    private String name;
    private String status;
    private String remarks;
    private List<Long> permissionId;


    @Override
    public Role convertToEntity() {
        Role role = new Role();
        role.setId(this.id);
        role.setCode(this.code);
        role.setStatus(this.status);
        role.setName(this.name);
        role.setRemarks(this.remarks);
        role.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        role.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        role.setCreatedBy(this.getCreatedBy());
        role.setUpdatedBy(this.getUpdatedBy());

        return role;
    }

    @Override
    public void convertToDTO(Role entity, boolean partialFill) {
           this.id = entity.getId();
           this.code = entity.getCode();
           this.status = entity.getStatus();
           this.name = entity.getName();
           this.remarks = entity.getRemarks();
           this.updatedOn = entity.getUpdatedOn();
           this.createdOn = entity.getCreatedOn();
           this.updatedBy = entity.getUpdatedBy();
           this.createdBy = entity.getCreatedBy();
    }

    @Override
    public RoleDTO convertToNewDTO(Role entity, boolean partialFill) {
        RoleDTO roleDTO = new RoleDTO();
        roleDTO.convertToDTO(entity, partialFill);
        return roleDTO;
    }

    public List<RolePermission> RolePermission(Role role) {
        List<RolePermission> rolePermissionList = new ArrayList<>();
        if (!AppUtility.isEmpty(this.permissionId)) {
            for (Long id : this.permissionId) {
                Permission permission = new Permission(id);
                RolePermission rp = new RolePermission(role, permission);
                rolePermissionList.add(rp);
            }
        }
        return rolePermissionList;
    }
}

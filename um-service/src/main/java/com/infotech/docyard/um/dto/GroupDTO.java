package com.infotech.docyard.um.dto;

import com.infotech.docyard.um.dl.entity.Group;
import com.infotech.docyard.um.dl.entity.GroupRole;
import com.infotech.docyard.um.dl.entity.Role;
import com.infotech.docyard.um.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class GroupDTO extends BaseDTO<GroupDTO, Group> {

    private Long id;
    private String code;
    private String name;
    private String status;
    private String remarks;
    private List<Long> role;
    private List<String> rolesNameList;


    @Override
    public Group convertToEntity() {
        Group group = new Group();
        group.setId(this.id);
        group.setCode(this.code);
        group.setStatus(this.status);
        group.setName(AppUtility.isEmpty(this.name) ? this.name : this.name.trim());
        group.setRemarks(this.remarks);
        group.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        group.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        group.setCreatedBy(this.getCreatedBy());
        group.setUpdatedBy(this.getUpdatedBy());

        return group;
    }

    @Override
    public void convertToDTO(Group entity, boolean partialFill) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.status = entity.getStatus();
        this.name = entity.getName();
        this.remarks = entity.getRemarks();
        this.role = entity.getGroupRoles().stream().map(GroupRole::getRole).map(Role::getId).collect(Collectors.toList());
        this.rolesNameList = entity.getGroupRoles().stream().map(GroupRole::getRole).map(Role::getCode).collect(Collectors.toList());
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public GroupDTO convertToNewDTO(Group entity, boolean partialFill) {
        GroupDTO groupDTO = new GroupDTO();
        groupDTO.convertToDTO(entity, partialFill);
        return groupDTO;
    }

    public List<GroupRole> groupRoles(Group group) {
        List<GroupRole> groupRoleList = new ArrayList<>();
        if (!AppUtility.isEmpty(this.role)) {
            for (Long id : this.role) {
                Role role = new Role(id);
                GroupRole gr = new GroupRole(group, role);
                groupRoleList.add(gr);
            }
        }
        return groupRoleList;
    }
}

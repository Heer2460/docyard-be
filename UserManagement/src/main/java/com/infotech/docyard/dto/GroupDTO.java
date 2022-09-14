package com.infotech.docyard.dto;

import com.infotech.docyard.dl.entity.*;
import com.infotech.docyard.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
public class GroupDTO extends BaseDTO<GroupDTO, Group> {

    private Long id;
    private String code;
    private String name;
    private String status;
    private String remarks;
    private List<Long> roleId;


    @Override
    public Group convertToEntity() {
        Group group = new Group();
        group.setId(this.id);
        group.setCode(this.code);
        group.setStatus(this.status);
        group.setName(this.name);
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
        if (!AppUtility.isEmpty(this.roleId)) {
            for (Long id : this.roleId) {
                Role role = new Role(id);
                GroupRole gr = new GroupRole(group,role);
                groupRoleList.add(gr);
            }
        }
        return groupRoleList;
    }
}

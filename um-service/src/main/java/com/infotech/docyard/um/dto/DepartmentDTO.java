package com.infotech.docyard.um.dto;

import com.infotech.docyard.um.dl.entity.Department;
import com.infotech.docyard.um.util.AppUtility;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class DepartmentDTO  extends BaseDTO<DepartmentDTO, Department> {

    private Long id;
    private String code;
    private String name;
    private String status;


    @Override
    public Department convertToEntity() {
        Department department = new Department();
        department.setId(this.id);
        department.setCode(this.code);
        department.setStatus(this.status);
        department.setName(this.name);
        department.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
        department.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
        department.setCreatedBy(this.getCreatedBy());
        department.setUpdatedBy(this.getUpdatedBy());

        return department;
    }

    @Override
    public void convertToDTO(Department entity, boolean partialFill) {
        this.id = entity.getId();
        this.code = entity.getCode();
        this.status = entity.getStatus();
        this.name = entity.getName();
        this.updatedOn = entity.getUpdatedOn();
        this.createdOn = entity.getCreatedOn();
        this.updatedBy = entity.getUpdatedBy();
        this.createdBy = entity.getCreatedBy();
    }

    @Override
    public DepartmentDTO convertToNewDTO(Department entity, boolean partialFill) {
        DepartmentDTO departmentDTO = new DepartmentDTO();
        departmentDTO.convertToDTO(entity, partialFill);
        return departmentDTO;
    }
}

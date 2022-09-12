//package com.infotech.docyard.dto;
//
//import com.infotech.docyard.dl.entity.Department;
//import com.infotech.docyard.util.AppUtility;
//import lombok.Data;
//
//import java.time.ZonedDateTime;
//
//@Data
//public class DepartmentDTO  extends BaseDTO<DepartmentDTO, Department> {
//
//    private String code;
//    private String description;
//    private String status;
//
//
//    @Override
//    public Department convertToEntity() {
//        Department department = new Department();
//        department.setCode(this.code);
//        department.setStatus(this.status);
//        department.setDescription(this.description);
//        department.setCreatedOn(AppUtility.isEmpty(this.createdOn) ? ZonedDateTime.now() : this.createdOn);
//        department.setUpdatedOn(AppUtility.isEmpty(this.updatedOn) ? ZonedDateTime.now() : this.updatedOn);
//        department.setCreatedBy(this.getCreatedBy());
//        department.setUpdatedBy(this.getUpdatedBy());
//
//        return department;
//    }
//
//    @Override
//    public void convertToDTO(Department entity, boolean partialFill) {
//        this.id = entity.getId();
//        this.code = entity.getCode();
//        this.status = entity.getStatus();
//        this.description = entity.getDescription();
//        this.updatedOn = entity.getUpdatedOn();
//        this.createdOn = entity.getCreatedOn();
//        this.updatedBy = entity.getUpdatedBy();
//        this.createdBy = entity.getCreatedBy();
//    }
//
//    @Override
//    public DepartmentDTO convertToNewDTO(Department entity, boolean partialFill) {
//        DepartmentDTO departmentDTO = new DepartmentDTO();
//        departmentDTO.convertToDTO(entity, partialFill);
//        return departmentDTO;
//    }
//}

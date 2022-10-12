package com.infotech.docyard.um.api;

import com.infotech.docyard.um.dl.entity.Department;
import com.infotech.docyard.um.dto.DepartmentDTO;
import com.infotech.docyard.um.exceptions.CustomException;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.exceptions.NoDataFoundException;
import com.infotech.docyard.um.service.DepartmentService;
import com.infotech.docyard.um.util.AppConstants;
import com.infotech.docyard.um.util.AppUtility;
import com.infotech.docyard.um.util.CustomResponse;
import com.infotech.docyard.um.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/department")
@Log4j2
public class DepartmentAPI {

    @Autowired
    private DepartmentService departmentService;


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CustomResponse searchDepartment(HttpServletRequest request,
                                           @RequestParam String code,
                                           @RequestParam String name,
                                           @RequestParam String status
    )
            throws CustomException, NoDataFoundException {
        log.info("searchDepartment API initiated...");

        List<Department> departments = null;
        try {
            departments = departmentService.searchDepartmentByCodeAndDescription(code, name, status);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(departments, new DepartmentDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllDepartments(HttpServletRequest request)
            throws CustomException, NoDataFoundException {
        log.info("getAllDepartments API initiated...");

        List<Department> departmentList = null;
        try {
            departmentList = departmentService.getAllDepartments();
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(departmentList, new DepartmentDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CustomResponse getDepartmentById(HttpServletRequest request, @PathVariable Long id)
            throws CustomException, NoDataFoundException {
        log.info("getAllDepartments API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        Department department = null;
        try {
            department = departmentService.getDepartmentById(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(department, new DepartmentDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse createDepartment(HttpServletRequest request,
                                           @RequestBody DepartmentDTO departmentDTO)
            throws CustomException, NoDataFoundException {
        log.info("createDepartment API initiated...");
        Department department = null;
        try {
            department = departmentService.saveDepartment(departmentDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e, AppConstants.DBConstraints.UNQ_DEPARTMENT_CODE);
        }
        return ResponseUtility.buildResponseObject(department, new DepartmentDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public CustomResponse updateDepartment(HttpServletRequest request,
                                           @RequestBody DepartmentDTO departmentDTO)
            throws CustomException, NoDataFoundException {
        log.info("updateDepartment API initiated...");

        Department department = null;
        try {
            department = departmentService.UpdateDepartment(departmentDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e, AppConstants.DBConstraints.UNQ_DEPARTMENT_CODE);
        }
        return ResponseUtility.buildResponseObject(department, new DepartmentDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public CustomResponse deleteDepartment(HttpServletRequest request,
                                           @PathVariable("id") Long id)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("deleteDepartment API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            departmentService.deleteDepartment(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }
}

package com.infotech.docyard.api;

import com.infotech.docyard.dl.entity.Role;
import com.infotech.docyard.dto.RoleDTO;
import com.infotech.docyard.exceptions.CustomException;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.service.RoleService;
import com.infotech.docyard.util.AppUtility;
import com.infotech.docyard.util.CustomResponse;
import com.infotech.docyard.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/role")
@Log4j2
public class RoleAPI {

    @Autowired
    private RoleService roleService;


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CustomResponse searchRole(HttpServletRequest request,
                                              @RequestParam String code,
                                              @RequestParam String name,
                                              @RequestParam String status
    )
            throws CustomException, NoDataFoundException {
        log.info("searchRole API initiated...");

        List<Role> roles = null;
        try {
            roles = roleService.searchRole(code, name, status);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(roles, new RoleDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllRole(HttpServletRequest request)
            throws CustomException, NoDataFoundException {
        log.info("getAllRole API initiated...");

        List<Role> roleList = null;
        try {
            roleList = roleService.getAllRole();
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(roleList, new RoleDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CustomResponse getRoleById(HttpServletRequest request, @PathVariable Long id)
            throws CustomException, NoDataFoundException {
        log.info("getRoleById API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        Role role = null;
        try {
            role = roleService.getRoleById(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(role, new RoleDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse createRole(HttpServletRequest request,
                                           @RequestBody RoleDTO roleDTO)
            throws CustomException, NoDataFoundException {
        log.info("createRole API initiated...");
        Role role = null;
        try {
            role = roleService.saveAndUpdateRole(roleDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(role, new RoleDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public CustomResponse updateRole(HttpServletRequest request,
                                           @RequestBody RoleDTO roleDTO)
            throws CustomException, NoDataFoundException {
        log.info("updateRole API initiated...");

        Role role = null;
        try {
            role = roleService.saveAndUpdateRole(roleDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(role, new RoleDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public CustomResponse deleteRole(HttpServletRequest request,
                                           @PathVariable("id") Long id)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("deleteRole API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            roleService.deleteRole(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }
}

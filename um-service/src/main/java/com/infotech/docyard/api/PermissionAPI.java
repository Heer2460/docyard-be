package com.infotech.docyard.api;

import com.infotech.docyard.dl.entity.Permission;
import com.infotech.docyard.dto.PermissionDTO;
import com.infotech.docyard.exceptions.CustomException;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.service.PermissionService;
import com.infotech.docyard.util.AppUtility;
import com.infotech.docyard.util.CustomResponse;
import com.infotech.docyard.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/permission")
@Log4j2
public class PermissionAPI {

    @Autowired
    private PermissionService permissionService;

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllPermission(HttpServletRequest request)
            throws CustomException, NoDataFoundException {
        log.info("getAllPermission API initiated...");

        List<Permission> permissionList = null;
        try {
            permissionList = permissionService.getAllPermissions();
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(permissionList, new PermissionDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CustomResponse getPermissionById(HttpServletRequest request, @PathVariable Long id)
            throws CustomException, NoDataFoundException {
        log.info("getPermissionById API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        Permission permission = null;
        try {
            permission = permissionService.getPermissionsById(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(permission, new PermissionDTO(), false);
    }

}

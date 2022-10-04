package com.infotech.docyard.um.api;

import com.infotech.docyard.um.dl.entity.Group;
import com.infotech.docyard.um.dto.GroupDTO;
import com.infotech.docyard.um.exceptions.CustomException;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.exceptions.NoDataFoundException;
import com.infotech.docyard.um.service.GroupService;
import com.infotech.docyard.um.util.AppUtility;
import com.infotech.docyard.um.util.CustomResponse;
import com.infotech.docyard.um.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/group")
@Log4j2
public class GroupAPI {

    @Autowired
    private GroupService groupService;


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CustomResponse searchGroup(HttpServletRequest request,
                                      @RequestParam (value = "code",required = false)String code,
                                      @RequestParam (value = "name",required = false)String name,
                                      @RequestParam (value = "status",required = false)String status,
                                      @RequestParam (value = "role",required = false) List<Long> role
                                      )
            throws CustomException, NoDataFoundException {
        log.info("searchGroup API initiated...");

        List<Group> groupList = null;
        try {
            groupList = groupService.searchGroup(code, name, status,role);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(groupList, new GroupDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllGroup(HttpServletRequest request)
            throws CustomException, NoDataFoundException {
        log.info("getAllGroup API initiated...");

        List<Group> groupList = null;
        try {
            groupList = groupService.getAllGroup();
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(groupList, new GroupDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public CustomResponse getGroupById(HttpServletRequest request, @PathVariable Long id)
            throws CustomException, NoDataFoundException {
        log.info("getGroupById API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        Group group = null;
        try {
            group = groupService.getGroupById(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(group, new GroupDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse createGroup(HttpServletRequest request,
                                      @RequestBody GroupDTO groupDTO)
            throws CustomException, NoDataFoundException {
        log.info("createGroup API initiated...");
        Group group = null;
        try {
            group = groupService.saveAndUpdateGroup(groupDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(group, new GroupDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public CustomResponse updateGroup(HttpServletRequest request,
                                      @RequestBody GroupDTO groupDTO)
            throws CustomException, NoDataFoundException {
        log.info("updateGroup API initiated...");

        Group group = null;
        try {
            group = groupService.saveAndUpdateGroup(groupDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(group, new GroupDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public CustomResponse deleteGroup(HttpServletRequest request,
                                      @PathVariable("id") Long id)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("deleteGroup API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            groupService.deleteGroup(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }
}

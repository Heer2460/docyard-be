package com.infotech.docyard.um.api;

import com.infotech.docyard.um.dl.entity.User;
import com.infotech.docyard.um.dto.UserDTO;
import com.infotech.docyard.um.exceptions.CustomException;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.exceptions.NoDataFoundException;
import com.infotech.docyard.um.service.UserService;
import com.infotech.docyard.um.util.AppUtility;
import com.infotech.docyard.um.util.CustomResponse;
import com.infotech.docyard.um.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/un-auth")
@Log4j2
public class UnAuthAPI {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET)
    public CustomResponse getUserById(HttpServletRequest request, @PathVariable Long id)
            throws CustomException, NoDataFoundException {
        log.info("getUserById API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        User user = null;
        try {
            user = userService.getUserById(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(user, new UserDTO(), false);
    }
}

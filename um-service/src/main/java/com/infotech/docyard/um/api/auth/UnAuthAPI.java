package com.infotech.docyard.um.api.auth;

import com.infotech.docyard.um.dl.entity.User;
import com.infotech.docyard.um.dto.ChangePasswordDTO;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping("/un-auth")
@Log4j2
public class UnAuthAPI {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "/forgot-password", method = RequestMethod.PUT)
    public CustomResponse forgotPassword(HttpServletRequest request,
                                         @RequestParam(value = "email") String email)
            throws DataValidationException, NoDataFoundException, IOException, CustomException {
        log.info("forgotPassword API initiated...");

        try {
            userService.forgotPassword(email);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponseForPut(null, AppUtility.getResourceMessage("password.reset.link.token"));
    }

    @RequestMapping(value = "/check-token-expiry", method = RequestMethod.PUT)
    public ResponseEntity<?> checkTokenExpiry(HttpServletRequest request,
                                              @RequestParam(value = "token") String token)
            throws DataValidationException, NoDataFoundException {
        if (AppUtility.isEmpty(token)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        log.info("checkTokenExpiry API initiated...");

        HttpStatus status = userService.checkTokenExpiry(token);
        return new ResponseEntity<>(status, status);
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.PUT)
    public CustomResponse resetPassword(HttpServletRequest request,
                                        @RequestBody ChangePasswordDTO changePasswordDTO)
            throws DataValidationException, NoDataFoundException {
        log.info("resetPassword API initiated...");

        if (AppUtility.isEmpty(changePasswordDTO) && changePasswordDTO.getUserId() <= 0) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        User user = userService.resetPassword(changePasswordDTO);

        return ResponseUtility.buildResponseObject(user, new UserDTO(), true);
    }

    @RequestMapping(value = "/un-success/{username}", method = RequestMethod.PUT)
    public CustomResponse unsuccessfulLoginAttempt(HttpServletRequest request,
                                        @PathVariable(name = "username") String username)
            throws DataValidationException, NoDataFoundException {
        log.info("unsuccessfulLoginAttempt API initiated...");

        if (AppUtility.isEmpty(username)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        userService.unsuccessfulLoginAttempt(username);

        return ResponseUtility.buildResponseObject(new User());
    }
}

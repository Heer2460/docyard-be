package com.infotech.docyard.api.auth;

import com.infotech.docyard.dto.UserDTO;
import com.infotech.docyard.exceptions.CustomException;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.service.UserService;
import com.infotech.docyard.util.AppUtility;
import com.infotech.docyard.util.CustomResponse;
import com.infotech.docyard.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

@RestController
@RequestMapping("/auth")
@Log4j2
public class AuthAPI {

    @Autowired
    private UserService umService;

    @RequestMapping(value = "/sign-in", method = RequestMethod.POST)
    public ResponseEntity<?> getSignIn(HttpServletRequest request,
                                       Principal principal) throws NoDataFoundException {
        UserDTO userDTO = null;
        log.info("User Sign In API initiated...");

        if(!AppUtility.isEmpty(principal)){
            userDTO = umService.userSignIn(principal.getName());
            if (!AppUtility.isEmpty(userDTO)) {
                if (userDTO.getId() == -1) {
                    return new ResponseEntity<>(userDTO, HttpStatus.LOCKED);
                }
            }
        }
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @RequestMapping(value = "/sign-out", method = RequestMethod.POST)
    public CustomResponse getSignOut(HttpServletRequest request,
                                     Principal principal) throws CustomException, DataValidationException {
        log.info("User Sign out API initiated...");
        String authHeader = request.getHeader("Authorization");
        if (AppUtility.isEmpty(authHeader)) {
            throw new DataValidationException(AppUtility.getResourceMessage("validation.error"));
        }
        try {
            umService.getLoggedOutUser(principal, authHeader);
        } catch (Exception ex) {
            throw new CustomException(ex);
        }
        return ResponseUtility.buildResponseObject(null);
    }

}

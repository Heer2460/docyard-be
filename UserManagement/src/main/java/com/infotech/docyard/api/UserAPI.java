package com.infotech.docyard.api;


import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.dto.ChangePasswordDTO;
import com.infotech.docyard.dto.ResetPasswordDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@Log4j2
public class UserAPI {
    @Autowired
    private UserService userService;

    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public CustomResponse searchUser(HttpServletRequest request,
                                     @RequestParam String username,
                                     @RequestParam String name,
                                     @RequestParam String status
    )
            throws CustomException, NoDataFoundException {
        log.info("getDepartmentByName API initiated...");

        List<User> users = null;
        try {
            users = userService.searchUser(username, name, status);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(users, new UserDTO(), false);
    }

    @RequestMapping(value = "/username/{username}", method = RequestMethod.GET)
    public User searchByUserName(HttpServletRequest request,
                                           @PathVariable(name = "username") String username) throws CustomException {
        log.info("searchByUserName API initiated...");

        User user = null;
        try {
            user = userService.searchUserByUserName(username);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return user;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public CustomResponse getAllUsers(HttpServletRequest request)
            throws CustomException, NoDataFoundException {
        log.info("getAllUsers API initiated...");

        List<User> userList = null;
        try {
            userList = userService.getAllUsers();
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseList(userList, new UserDTO(), false);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
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

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public CustomResponse createUser(HttpServletRequest request,
                                     @RequestPart("data") UserDTO userDTO,
                                     @RequestPart(value = "logo", required = false) MultipartFile profileImg)
            throws CustomException, NoDataFoundException {
        log.info("createUser API initiated...");
        User user = null;
        try {
            user = userService.saveUser(userDTO, profileImg);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.buildResponseObject(user, new UserDTO(), false);
    }

    @RequestMapping(value = "/", method = RequestMethod.PUT)
    public CustomResponse updateUser(HttpServletRequest request,
                                     @RequestPart("data") UserDTO userDTO,
                                     @RequestPart(value = "logo", required = false) MultipartFile profileImg)
            throws CustomException, NoDataFoundException {
        log.info("updateUser API initiated...");

        User user = null;
        try {
            user = userService.updateUser(userDTO, profileImg);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(user, "User successfully updated.");
    }

    @RequestMapping(value = "/profile-picture", method = RequestMethod.PUT)
    public CustomResponse updateProfilePicture(HttpServletRequest request,
                                               @RequestPart("data") UserDTO userDTO,
                                               @RequestPart(value = "profilePicture", required = false) MultipartFile profileImg)
            throws CustomException, NoDataFoundException {
        log.info("updateProfilePicture API initiated...");

        User user = null;
        try {
            user = userService.updateProfilePicture(userDTO, profileImg);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(user, "User profile successfully updated.");
    }

    @RequestMapping(value = "/updateUserStatus", method = RequestMethod.PUT)
    public CustomResponse updateUserStatus(HttpServletRequest request,
                                           @RequestBody UserDTO userDTO)
            throws CustomException, NoDataFoundException {
        log.info("updateUserStatus API initiated...");

        User user = null;
        try {
            user = userService.updateUserStatus(userDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(user, "Profile picture successfully updated.");
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public CustomResponse deleteUser(HttpServletRequest request,
                                     @PathVariable("id") Long id)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("deleteUser API initiated...");

        if (AppUtility.isEmpty(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("id.not.found"));
        }
        try {
            userService.deleteUser(id);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.deleteSuccessResponse(null, AppUtility.getResourceMessage("deleted.success"));
    }

    @RequestMapping(value = "/change-password", method = RequestMethod.PUT)
    public CustomResponse changePassword(HttpServletRequest request,
                                         @RequestBody ChangePasswordDTO changePasswordDTO)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("changePassword API initiated...");

        User user = null;
        try {
            user = userService.changePassword(changePasswordDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(user, "Password successfully updated.");
    }

    @RequestMapping(value = "/reset-password", method = RequestMethod.PUT)
    public CustomResponse resetPassword(HttpServletRequest request,
                                        @RequestBody ResetPasswordDTO resetPasswordDTO)
            throws DataValidationException, NoDataFoundException, CustomException {
        log.info("resetPassword API initiated...");

        User user = null;
        try {
            user = userService.resetPassword(resetPasswordDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        return ResponseUtility.successResponse(user, "Password successfully reset.");
    }

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
        return ResponseUtility.successResponseForPut(null,"Password reset Link and Token successfully mailed.");
    }

    @RequestMapping(value = "/validate-token-reset-password", method = RequestMethod.PUT)
    public CustomResponse verifyTokenAndResetPassword(HttpServletRequest request,
                                                   @RequestBody ResetPasswordDTO resetPasswordDTO)
            throws DataValidationException, NoDataFoundException, IOException, CustomException {
        log.info("updateResetPasswordToken API initiated...");

        Boolean ifVerified = null;
        try{
            ifVerified = userService.verifyTokenAndResetPassword(resetPasswordDTO);
        } catch (Exception e) {
            ResponseUtility.exceptionResponse(e);
        }
        if (Boolean.TRUE.equals(ifVerified)){
            return ResponseUtility.successResponseForPut(null,"Password has been reset.");
        }
        return ResponseUtility.successResponseForPut(null,"Password reset token provided is not correct.");
    }

}

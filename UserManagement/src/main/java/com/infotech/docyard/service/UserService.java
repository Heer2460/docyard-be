package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.EmailInstance;
import com.infotech.docyard.dl.entity.ForgotPasswordLink;
import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.dl.repository.*;
import com.infotech.docyard.dto.*;
import com.infotech.docyard.enums.EmailStatusEnum;
import com.infotech.docyard.enums.EmailTypeEnum;
import com.infotech.docyard.exceptions.CustomException;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.util.AppConstants;
import com.infotech.docyard.util.AppUtility;
import com.infotech.docyard.util.NotificationUtility;
import com.infotech.docyard.util.ResponseUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private AdvSearchRepository advSearchRepository;
    @Autowired
    private ForgotPasswordLinkRepository forgotPasswordLinkRepository;
    @Autowired
    private EmailInstanceRepository emailInstanceRepository;

    @Value("${fe.base.link}")
    private String baseFELink;
    @Autowired
    private NotificationService notificationService;

    @Value("${fe.reset.pass.base.link}")
    private String resetPassBaseFELink;

    public List<User> searchUser(String username, String name, String status) {
        log.info("searchUser method called..");

        return advSearchRepository.searchUser(username, name, status);
    }

    public List<User> getAllUsers() {
        log.info("getAllUsers method called..");
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.info("getUserById method called..");

        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return user.get();
        }
        return null;
    }

    @Transactional
    public User saveUser(UserDTO userDTO, MultipartFile profileImg) throws Exception {
        log.info("saveUser method called..");

        Boolean userExistsWithUsername = userRepository.existsByUsername(userDTO.getUsername());
        Boolean userExistsWithEmail = userRepository.existsByEmail(userDTO.getEmail());
        if (userExistsWithEmail && userExistsWithUsername) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.with.same.username.and.email"));
        } else if (userExistsWithEmail) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.with.same.email"));
        } else if (userExistsWithUsername) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.with.same.username"));
        } else {
            User user = null;
            if (!AppUtility.isEmpty(profileImg)) {
                userDTO.setProfilePhotoReceived(profileImg);
            }
            userDTO.setPassword(new BCryptPasswordEncoder().encode(userDTO.getPassword()));
//            SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(user.getEmail());
//            msg.setText(
//                    "Dear " + user.getName()
//                            + ", thank you for signing up. You have been registered with the following credentials, "
//                            + user.getEmail()
//                            + user.getUsername()
//                            + ", link to the system is as follows: "
//                            + "link: www.abc.com"
//            );
//            try{
//                this.mailSender.send(msg);
//            }
//            catch(MailException e) {
//                ResponseUtility.exceptionResponse(e);
//            }
            return userRepository.save(userDTO.convertToEntity());
        }
    }

    public User updateUser(UserDTO userDTO, MultipartFile profileImg) throws Exception {
        log.info("updateUser method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        if (AppUtility.isEmpty(dbUser)) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
        if (dbUser.get().getUsername().equals(userDTO.getUsername())) {
            if (!(dbUser.get().getEmail().equals(userDTO.getEmail()))) {
                Boolean userExistsWithEmail = userRepository.existsByEmail(userDTO.getEmail());
                if (userExistsWithEmail) {
                    throw new DataValidationException(AppUtility.getResourceMessage("user.with.same.email"));
                }
            }
            if (!AppUtility.isEmpty(profileImg)) {
                userDTO.setProfilePhotoReceived(profileImg);
            }
            return userRepository.save(userDTO.convertToEntityForUpdate());
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("user.can.not.change.username"));
        }
    }

    public User updateProfilePicture(UserDTO userDTO, MultipartFile profileImg) throws Exception {
        log.info("updateProfilePicture method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        userDTO.convertToDTO(dbUser.get(), false);
        if (AppUtility.isEmpty(dbUser)) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
        if (!AppUtility.isEmpty(profileImg)) {
            userDTO.setProfilePhotoReceived(profileImg);
        }
        return userRepository.save(userDTO.convertToEntityForUpdate());
    }

    public User updateUserStatus(UserDTO userDTO) throws Exception {
        log.info("updateUserStatus method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        if (AppUtility.isEmpty(dbUser)) {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
        String status = userDTO.getStatus();
        userDTO.convertToDTO(dbUser.get(), true);
        userDTO.setStatus(status);
        userDTO.setUpdatedOn(ZonedDateTime.now());
        return userRepository.save(userDTO.convertToEntityForUpdate());
    }

    public void deleteUser(Long id) {
        log.info("deleteUser method called..");

        userRepository.deleteById(id);
    }

    @Transactional(rollbackFor = {Throwable.class})
    public User changePassword(ChangePasswordDTO changePasswordDTO) throws DataValidationException, NoDataFoundException {
        log.info("changePassword method called..");

        Optional<User> user = userRepository.findById(changePasswordDTO.getUserId());
        if (user.isPresent()) {
            if (BCrypt.checkpw(changePasswordDTO.getCurrentPassword(), user.get().getPassword())) {
                if (!BCrypt.checkpw(changePasswordDTO.getNewPassword(), user.get().getPassword())) {
                    User u = user.get();
                    u.setPassword(new BCryptPasswordEncoder().encode(changePasswordDTO.getNewPassword()));
                    u.setForcePasswordChange(false);
                    u.setPasswordExpired(false);
                    u.setLastPassUpdatedOn(ZonedDateTime.now());
                    userRepository.save(u);
                } else {
                    throw new DataValidationException(AppUtility.getResourceMessage("user.same.password"));
                }
            } else {
                throw new DataValidationException(AppUtility.getResourceMessage("user.wrong.current.password"));
            }
        } else {
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        return user.get();
    }

    @Transactional(rollbackFor = {Throwable.class})
    public User resetPassword(ResetPasswordDTO resetPasswordDTO) throws DataValidationException, NoDataFoundException {
        log.info("resetPassword method called..");

        Optional<User> user = userRepository.findById(resetPasswordDTO.getUserId());
        if (user.isPresent()) {
            User u = user.get();
            u.setForcePasswordChange(false);
            u.setPasswordExpired(false);
            u.setPassword(new BCryptPasswordEncoder().encode(resetPasswordDTO.getNewPassword()));
            u.setStatus("Active");
            u.setLastPassUpdatedOn(ZonedDateTime.now());

            userRepository.save(u);
        } else {
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        return user.get();
    }


//    public void updateResetPasswordToken(ForgetPasswordDTO forgetPasswordDTO) throws IOException, CustomException {
//        log.info("updateResetPasswordToken method called..");
//
//        Optional<User> dbUser = userRepository.findByEmail(forgetPasswordDTO.getUserDTO().getEmail());
//        if (AppUtility.isEmpty(dbUser)){
//            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
//        }
//        String token = UUID.randomUUID().toString();
//        forgetPasswordDTO.getUserDTO().convertToDTO(dbUser.get(), true);
//        forgetPasswordDTO.getUserDTO().setPasswordResetToken(token);
//        User user = userRepository.save(forgetPasswordDTO.getUserDTO().convertToEntityForUpdate());
//        SimpleMailMessage msg = new SimpleMailMessage();
//            msg.setTo(user.getEmail());
//            msg.setText(
//                    "Dear " + user.getName()
//                            + ", Your password reset token is, "
//                            + forgetPasswordDTO.getUserDTO().getPasswordResetToken()
//                            + ", link to reset your password is as follows: "
//                            + forgetPasswordDTO.getPasswordResetLink()
//                            + "."
//            );
//            try{
//                this.mailSender.send(msg);
//            }
//            catch(MailException e) {
//                ResponseUtility.exceptionResponse(e);
//            }
//    }

    @Transactional(rollbackFor = {Throwable.class})
    public HttpStatus forgotPassword(String email, PasswordResetLinkDTO passwordResetLinkDTO) {
        log.info("forgotPassword method called..");

        HttpStatus status = HttpStatus.NOT_FOUND;
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (AppUtility.isEmpty(userOptional)){
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        User user = userOptional.get();
        if (!AppUtility.isEmpty(user)) {
            String token = UUID.randomUUID().toString();
            String content = NotificationUtility.buildForgotPasswordEmailContent(user, passwordResetLinkDTO.getPasswordResetLink(), token);
            if (!AppUtility.isEmpty(content)) {
                EmailInstance emailInstance = new EmailInstance();
                emailInstance.setToEmail(email);
                emailInstance.setType(EmailTypeEnum.FORGOT_PASSWORD.getValue());
                emailInstance.setSubject(AppConstants.EmailSubjectConstants.FORGOT_PASSWORD);
                emailInstance.setContent(content);
                emailInstance.setStatus(EmailStatusEnum.NOT_SENT.getValue());
                emailInstance.setCreatedOn(ZonedDateTime.now());
                emailInstance.setUpdatedOn(ZonedDateTime.now());
                emailInstance.setCreatedBy(1L);
                emailInstance.setUpdatedBy(1L);

                ForgotPasswordLink fpl = new ForgotPasswordLink(token);
                forgotPasswordLinkRepository.save(fpl);
                emailInstanceRepository.save(emailInstance);
                notificationService.sendEmail(emailInstance);
                status = HttpStatus.OK;
            }
        }
        return status;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public HttpStatus checkTokenExpiry(String token) {
        log.info("checkTokenExpiry method called..");

        ForgotPasswordLink fpl = forgotPasswordLinkRepository.findByToken(token);
        HttpStatus status;
        if (!AppUtility.isEmpty(fpl)) {
            if (fpl.getExpired()) {
                status = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
            } else {
                if (fpl.getExpiredOn().isBefore(ZonedDateTime.now())) {
                    status = HttpStatus.NON_AUTHORITATIVE_INFORMATION;
                } else {
                    status = HttpStatus.OK;
                }
            }
        } else {
            status = HttpStatus.NOT_FOUND;
        }
        return status;
    }

    public Boolean verifyPasswordResetToken(UserDTO userDTO) throws IOException {
        log.info("forgetPassword method called..");

        User user = null;
        Optional<User> dbUser = userRepository.findByEmail(userDTO.getEmail());
        if (AppUtility.isEmpty(dbUser.get())){
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        if (dbUser.get().getPasswordResetToken().equals(userDTO.getPasswordResetToken())){
            userDTO.setPasswordExpired(true);
            userDTO.convertToDTO(dbUser.get(), true);
            user = userDTO.convertToEntityForUpdate();
            userRepository.save(user);
            return true;
        }
        return false;
    }
}
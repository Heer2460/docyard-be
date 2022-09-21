package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.dl.repository.AdvSearchRepository;
import com.infotech.docyard.dl.repository.DepartmentRepository;
import com.infotech.docyard.dl.repository.GroupRepository;
import com.infotech.docyard.dl.repository.UserRepository;
import com.infotech.docyard.dto.ChangePasswordDTO;
import com.infotech.docyard.dto.ResetPasswordDTO;
import com.infotech.docyard.dto.UserDTO;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

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

    private MailSender mailSender;

    private SimpleMailMessage mailMessage;

    public void setMailSender(MailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void setMailMessage(SimpleMailMessage mailMessage) {
        this.mailMessage = new SimpleMailMessage(mailMessage);
    }

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
            userDTO.setPassword(dbUser.get().getPassword());
            userDTO.setLastPassUpdatedOn(ZonedDateTime.now());
            return userRepository.save(userDTO.convertToEntityForUpdate());
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("user.can.not.change.username"));
        }
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

}
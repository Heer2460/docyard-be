package com.infotech.docyard.service;

import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.dl.repository.AdvSearchRepository;
import com.infotech.docyard.dl.repository.UserRepository;
import com.infotech.docyard.dto.ChangePasswordDTO;
import com.infotech.docyard.dto.ResetPasswordDTO;
import com.infotech.docyard.dto.UserDTO;
import com.infotech.docyard.exceptions.DataValidationException;
import com.infotech.docyard.exceptions.NoDataFoundException;
import com.infotech.docyard.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdvSearchRepository advSearchRepository;

    public List<User> searchUser(String username,String name, String status) {
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
    public User saveAndUpdateUser(UserDTO userDTO) {
        log.info("saveAndUpdateUser method called..");

        return userRepository.save(userDTO.convertToEntity());
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

    public User searchUserByUserName(String username) {
        log.info("searchUserByUserName method called..");

        return userRepository.findByUsername(username);
    }
}
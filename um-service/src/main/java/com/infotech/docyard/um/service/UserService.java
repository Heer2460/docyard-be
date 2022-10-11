package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.*;
import com.infotech.docyard.um.dl.repository.*;
import com.infotech.docyard.um.dto.*;
import com.infotech.docyard.um.enums.EmailStatusEnum;
import com.infotech.docyard.um.enums.EmailTypeEnum;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.exceptions.NoDataFoundException;
import com.infotech.docyard.um.util.AppConstants;
import com.infotech.docyard.um.util.AppUtility;
import com.infotech.docyard.um.util.NotificationUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Log4j2
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
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
    @Autowired
    private GroupRoleRepository groupRoleRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private ModuleRepository moduleRepository;
    @Autowired
    private ModuleActionRepository moduleActionRepository;

    @Value("${fe.reset.pass.base.link}")
    private String resetPassBaseFELink;

    public List<User> searchUser(String username, String name, Long groupId, Long departmentId, String status) {
        log.info("searchUser method called..");

        return advSearchRepository.searchUser(username, name, groupId, departmentId, status);
    }

    public List<User> getAllUsers() {
        log.info("getAllUsers method called..");
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        log.info("getUserById method called..");

        Optional<User> user = userRepository.findById(id);
        return user.orElse(null);
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
            return userRepository.save(userDTO.convertToEntity());
        }
    }

    public User updateUser(UserDTO userDTO, MultipartFile profileImg) throws Exception {
        log.info("updateUser method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        if (dbUser.isPresent()) {
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

                return userRepository.save(userDTO.convertToEntityForUpdate());
            } else {
                throw new DataValidationException(AppUtility.getResourceMessage("user.can.not.change.username"));
            }
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
    }

    public User updateProfilePicture(UserDTO userDTO, MultipartFile profileImg) throws Exception {
        log.info("updateProfilePicture method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        if (dbUser.isPresent()) {
            userDTO.convertToDTO(dbUser.get(), false);
            if (!AppUtility.isEmpty(profileImg)) {
                userDTO.setProfilePhotoReceived(profileImg);
            }
            return userRepository.save(userDTO.convertToEntityForUpdate());
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
    }

    public User updateUserStatus(UserDTO userDTO) throws IOException {
        log.info("updateUserStatus method called..");

        Optional<User> dbUser = userRepository.findById(userDTO.getId());
        if (dbUser.isPresent()) {
            String status = userDTO.getStatus();
            userDTO.convertToDTO(dbUser.get(), true);
            userDTO.setStatus(status);
            userDTO.setUpdatedOn(ZonedDateTime.now());
            return userRepository.save(userDTO.convertToEntityForUpdate());
        } else {
            throw new DataValidationException(AppUtility.getResourceMessage("user.not.found"));
        }
    }

    public void deleteUser(Long id) {
        log.info("deleteUser method called..");

        Optional<User> user = userRepository.findById(id);

        if(user.isPresent()){
            if(user.get().getStatus().equalsIgnoreCase(AppConstants.Status.ACTIVE)){
                throw new DataValidationException(AppUtility.getResourceMessage("record.cannot.be.deleted.dependency"));
            }else{
                userRepository.deleteById(id);
            }
        }
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

    @Transactional(rollbackFor = {Throwable.class})
    public HttpStatus forgotPassword(String email) {
        log.info("forgotPassword method called..");

        HttpStatus status = HttpStatus.NOT_FOUND;
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (AppUtility.isEmpty(userOptional)) {
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        User user = null;
        if (userOptional.isPresent()) {
            user = userOptional.get();
        }
        if (!AppUtility.isEmpty(user)) {
            String token = UUID.randomUUID().toString();
            user.setPasswordResetToken(token);
            user.setPasswordExpired(true);
            String content = NotificationUtility.buildForgotPasswordEmailContent(user, resetPassBaseFELink, token);
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
            userRepository.save(user);
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

    public Boolean verifyTokenAndResetPassword(ResetPasswordDTO resetPasswordDTO) {
        log.info("forgetPassword method called..");

        User user = null;
        Optional<User> dbUser = userRepository.findById(resetPasswordDTO.getUserId());
        if (dbUser.isPresent()) {
            user = dbUser.get();
            if (user.getPasswordResetToken().equals(resetPasswordDTO.getToken())) {
                user.setForcePasswordChange(false);
                user.setPasswordExpired(false);
                user.setPassword(new BCryptPasswordEncoder().encode(resetPasswordDTO.getNewPassword()));
                user.setStatus("Active");
                user.setLastPassUpdatedOn(ZonedDateTime.now());
                userRepository.save(user);
                return true;
            }
        } else {
            throw new NoDataFoundException(AppUtility.getResourceMessage("user.not.found"));
        }
        return false;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public UserDTO userSignIn(String username) {
        log.info("userSignIn method called..");
        UserDTO userDTO = null;
        User user = userRepository.findByUsername(username);
        if (!AppUtility.isEmpty(user)) {
            userDTO = new UserDTO();
            userDTO.convertToDTO(user, false);
            if (user.getStatus().equalsIgnoreCase(AppConstants.Status.SUSPEND) || user.getGroup().getStatus().equalsIgnoreCase(AppConstants.Status.SUSPEND)) {
                throw new DataValidationException("User is suspended please contact administration. ");
            }
            List<GroupRole> groupRoleList = groupRoleRepository.findAllByGroup_id(user.getGroup().getId());
            Set<Long> roleIds = groupRoleList.stream().map(GroupRole::getRole).map(Role::getId).collect(Collectors.toSet());
            List<RolePermission> rolePermissionList = rolePermissionRepository.findAllRole_idIn(roleIds);
            Set<Long> moduleActionIds = rolePermissionList.stream().map(RolePermission::getModuleAction).map(ModuleAction::getId).collect(Collectors.toSet());
            List<ModuleAction> moduleActionList = moduleActionRepository.findAllById(moduleActionIds);
            Set<Long> moduleIds = moduleActionList.stream().map(ModuleAction::getModule).map(Module::getId).collect(Collectors.toSet());
            List<Module> moduleList = moduleRepository.findAllById(moduleIds);
            List<ModuleDTO> moduleDTOList = getMenuList(moduleList);

            for (ModuleDTO moduleDTO : moduleDTOList) {
                for (ModuleDTO children : moduleDTO.getChildren()) {
                    List<ModuleActionDTO> moduleActionDTOList = new ArrayList<>();
                    for (ModuleAction moduleAction : moduleActionList) {
                        if (moduleAction.getModule().getId().equals(children.getModuleId())) {
                            ModuleActionDTO moduleActionDTO = new ModuleActionDTO();
                            moduleActionDTO.convertToDTO(moduleAction, true);
                            moduleActionDTOList.add(moduleActionDTO);
                        }
                    }
                    children.setModuleActionDTOList(moduleActionDTOList);
                }
            }
            userDTO.setModuleDTOList(moduleDTOList);
            userDTO.setModuleActionList(moduleActionList);
        } else {
            throw new NoDataFoundException("User not found.");
        }
        return userDTO;
    }

    @Transactional(rollbackFor = {Throwable.class})
    public void getLoggedOutUser(Principal principal, String authHeader) {
        log.info("getLoggedOutUser method called..");
        User user = userRepository.findByUsername(principal.getName());
        if (!AppUtility.isEmpty(user)) {
            userRepository.save(user);
        }
        //String tokenValue = authHeader.replace("Bearer", "").trim();
        //OAuth2AccessToken accessToken = tokenStore.readAccessToken(tokenValue);
        //tokenStore.removeAccessToken(accessToken);
    }

    private List<ModuleDTO> getMenuList(List<Module> moduleList) {
        List<Long> specificModuleIds = new ArrayList<>();
        Map<Long, ModuleDTO> map = new HashMap<>();
        for (Module m : moduleList) {
            if (m.getSlug().equalsIgnoreCase("config")
                    || m.getSlug().equalsIgnoreCase("report")) {
                specificModuleIds.add(m.getId());
            }
            if (AppUtility.isEmpty(m.getParentId())) {
                if (!map.containsKey(m.getParentId())) {
                    map.put(m.getId(), new ModuleDTO().convertToNewDTO(m, false));
                }
            } else {
                ModuleDTO parent = map.get(m.getParentId());
                if (AppUtility.isEmpty(parent)) {
                    parent = new ModuleDTO().convertToNewDTO(moduleRepository.findById(m.getParentId()).get(), true);
                    map.put(m.getParentId(), parent);
                }
                if (AppUtility.isEmpty(parent.getChildren())) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(new ModuleDTO().convertToNewDTO(m, false));
            }
        }
        for (Long moduleId : specificModuleIds) {
            ModuleDTO refModuleDTO = map.get(moduleId);
            if (!AppUtility.isEmpty(refModuleDTO)) {
                refModuleDTO.setChildren(new ArrayList<>());
                List<Module> refChildModuleList = moduleRepository.findAllByParentId(moduleId);
                for (Module m : refChildModuleList) {
                    ModuleDTO firstParent = new ModuleDTO().convertToNewDTO(m, false);
                    refModuleDTO.getChildren().add(firstParent);
                    List<Module> refChildModList = moduleRepository.findAllByParentId(m.getId());
                    for (Module mod : refChildModList) {
                        ModuleDTO child = new ModuleDTO();
                        child.convertToDTO(mod, false);
                        if (AppUtility.isEmpty(child.getChildren())) {
                            child.setChildren(new ArrayList<>());
                        }
                        child.getChildren().add(new ModuleDTO().convertToNewDTO(mod, false));
                        if (AppUtility.isEmpty(firstParent.getChildren())) {
                            firstParent.setChildren(new ArrayList<>());
                        }
                        firstParent.getChildren().add(child);
                    }
                }
            }
            List<ModuleDTO> sortedList = refModuleDTO.getChildren().stream().sorted(Comparator.comparing(ModuleDTO::getSeq)).collect(Collectors.toList());
            refModuleDTO.setChildren(sortedList);
        }
        return new ArrayList<>(map.values());
    }

}
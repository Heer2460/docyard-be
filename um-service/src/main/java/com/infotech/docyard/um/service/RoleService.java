package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.Role;
import com.infotech.docyard.um.dl.repository.AdvSearchRepository;
import com.infotech.docyard.um.dl.repository.GroupRoleRepository;
import com.infotech.docyard.um.dl.repository.RolePermissionRepository;
import com.infotech.docyard.um.dl.repository.RoleRepository;
import com.infotech.docyard.um.dto.RoleDTO;
import com.infotech.docyard.um.exceptions.DBConstraintViolationException;
import com.infotech.docyard.um.exceptions.DataValidationException;
import com.infotech.docyard.um.util.AppConstants;
import com.infotech.docyard.um.util.AppUtility;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private AdvSearchRepository advSearchRepository;
    @Autowired
    private RolePermissionRepository rolePermissionRepository;
    @Autowired
    private GroupRoleRepository groupRoleRepository;

    public List<Role> searchRole(String code, String name, String status) {
        log.info("searchRole method called..");

        return advSearchRepository.searchRole(code, name, status);
    }

    public List<Role> getAllRole() {
        log.info("getAllRole method called..");

        return roleRepository.findAll();
    }

    public Role getRoleById(Long id) {
        log.info("getRoleById method called..");

        Optional<Role> role = roleRepository.findById(id);
        if (role.isPresent()) {
            return role.get();
        }
        return null;
    }

    @Transactional
    public Role saveRole(RoleDTO roleDTO) {
        log.info("saveAndUpdateRole method called..");
        Role role = roleDTO.convertToEntity();
        if (AppUtility.isEmpty(role.getRolePermissions())) {
            role.setRolePermissions(roleDTO.RolePermission(role));
        }
        if (roleRepository.existsByCode(roleDTO.getCode())) {
            throw new DBConstraintViolationException("Code Already Exists");
        }
        return roleRepository.save(role);
    }

    @Transactional
    public Role UpdateRole(RoleDTO roleDTO) {
        log.info("saveAndUpdateRole method called..");

        Role role = roleDTO.convertToEntity();
        if (roleDTO.getStatus().equalsIgnoreCase(AppConstants.Status.SUSPEND)) {
            if (groupRoleRepository.existsByRole_Id(roleDTO.getId())) {
                throw new DataValidationException(AppUtility.getResourceMessage("record.cannot.be.suspended.dependency"));
            }
        }
        if (AppUtility.isEmpty(role.getRolePermissions())) {
            role.setRolePermissions(roleDTO.RolePermission(role));
        }
        return roleRepository.save(role);
    }

    @Transactional
    public void deleteRole(Long id) {
        log.info("deleteRole method called..");
        if (rolePermissionRepository.existsByRole_Id(id)) {
            throw new DataValidationException(AppUtility.getResourceMessage("record.cannot.be.deleted.dependency"));
        } else {
            roleRepository.deleteById(id);
        }
    }
}

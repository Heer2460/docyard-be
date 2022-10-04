package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.Permission;
import com.infotech.docyard.um.dl.repository.PermissionRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    public List<Permission> getAllPermissions() {
        log.info("getAllPermissions method called..");

        return permissionRepository.findAll();
    }

    public Permission getPermissionsById(Long id) {
        log.info("getPermissionsById method called..");

        Optional<Permission> permission = permissionRepository.findById(id);
        if (permission.isPresent()) {
            return permission.get();
        }
        return null;
    }
}

package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

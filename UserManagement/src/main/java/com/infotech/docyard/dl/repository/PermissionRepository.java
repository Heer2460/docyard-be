package com.infotech.docyard.dl.repository;

import com.infotech.docyard.dl.entity.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, Long> {
}

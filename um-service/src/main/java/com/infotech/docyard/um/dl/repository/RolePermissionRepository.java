package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface RolePermissionRepository extends JpaRepository<RolePermission, Long> {

    @Query("SELECT rp from RolePermission rp where rp.role.id in :roleIds")
    List<RolePermission> findAllRole_idIn(@Param("roleIds") Set<Long> roleIds);
}

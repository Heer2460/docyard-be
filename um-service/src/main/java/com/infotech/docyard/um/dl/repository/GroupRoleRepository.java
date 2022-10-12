package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.GroupRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GroupRoleRepository extends JpaRepository<GroupRole, Long> {
    List<GroupRole> findAllByGroup_id(Long groupId);

    boolean existsByGroup_IdAndRole_Status(Long groupId, String roleStatus);

}

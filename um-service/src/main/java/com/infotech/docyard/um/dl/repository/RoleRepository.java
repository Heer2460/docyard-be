package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {

    boolean existsByCode(String code);
}

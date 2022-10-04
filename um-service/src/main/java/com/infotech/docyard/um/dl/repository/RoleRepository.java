package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
//    List<Role> searchDepartmentByName(String name);
//    List<Role> searchDepartmentByCode(String code);
//
//    List<Role> searchDepartmentByCodeAndName(String code, String name);
}

package com.infotech.docyard.dl.repository;

import com.infotech.docyard.dl.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoleRepository extends JpaRepository<Role, Long> {
//    List<Role> searchDepartmentByName(String name);
//    List<Role> searchDepartmentByCode(String code);
//
//    List<Role> searchDepartmentByCodeAndName(String code, String name);
}

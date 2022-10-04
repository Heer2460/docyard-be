package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> searchDepartmentByName(String name);
    List<Department> searchDepartmentByCode(String code);

    List<Department> searchDepartmentByCodeAndName(String code, String name);
}

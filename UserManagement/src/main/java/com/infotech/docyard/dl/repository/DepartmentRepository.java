package com.infotech.docyard.dl.repository;

import com.infotech.docyard.dl.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    List<Department> searchDepartmentByDescription(String description);
    List<Department> searchDepartmentByCode(String code);

    List<Department> searchDepartmentByCodeAndDescription(String code, String description);
}

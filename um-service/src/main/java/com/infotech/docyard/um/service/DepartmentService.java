package com.infotech.docyard.um.service;

import com.infotech.docyard.um.dl.entity.Department;
import com.infotech.docyard.um.dl.repository.AdvSearchRepository;
import com.infotech.docyard.um.dl.repository.DepartmentRepository;
import com.infotech.docyard.um.dto.DepartmentDTO;
import com.infotech.docyard.um.exceptions.DBConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Log4j2
@Service
@Transactional
public class DepartmentService {

    @Autowired
    private DepartmentRepository departmentRepository;
    @Autowired
    private AdvSearchRepository advSearchRepository;

    public List<Department> searchDepartmentByCodeAndDescription(String code, String name, String status) {
        log.info("searchDepartmentByName method called..");

        return advSearchRepository.searchDepartment(code, name, status);
    }

    public List<Department> getAllDepartments() {
        log.info("getAllDepartments method called..");

        return departmentRepository.findAll();
    }

    public Department getDepartmentById(Long id) {
        log.info("getDepartmentById method called..");

        Optional<Department> department = departmentRepository.findById(id);
        if (department.isPresent()) {
            return department.get();
        }
        return null;
    }

    @Transactional
    public Department saveAndUpdateDepartment(DepartmentDTO departmentDTO) {
        log.info("saveAndUpdateDepartment method called..");

        if (departmentRepository.existsByCode(departmentDTO.getCode())) {
            throw new DBConstraintViolationException("Code Already Exists");
        }
        return departmentRepository.save(departmentDTO.convertToEntity());
    }

    @Transactional
    public void deleteDepartment(Long id) {
        log.info("deleteDepartment method called..");

        departmentRepository.deleteById(id);
    }
}

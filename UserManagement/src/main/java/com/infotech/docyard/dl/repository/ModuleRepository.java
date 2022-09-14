package com.infotech.docyard.dl.repository;

import com.infotech.docyard.dl.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ModuleRepository extends JpaRepository<Module, Long> {

    @Query("SELECT DISTINCT m from Module m ORDER BY m.parentId ASC, m.seq ASC")
    List<Module> findAllModules();

    List<Module> findAllByParentId(Long parentId);
}

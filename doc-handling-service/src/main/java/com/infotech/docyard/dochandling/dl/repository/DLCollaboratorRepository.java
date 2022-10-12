package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DLCollaboratorRepository extends JpaRepository<DLCollaborator, Long> {
}

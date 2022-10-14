package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface DLShareCollaboratorRepository extends JpaRepository<DLShareCollaborator, Long> {

    @Modifying
    void deleteByDlShareId(Long dlShareId);
}

package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLShareCollaborator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DLShareCollaboratorRepository extends JpaRepository<DLShareCollaborator, Long> {

    List<DLShareCollaborator> findAllByDlCollaboratorId(Long colId);

    @Modifying
    void deleteByDlShareId(Long dlShareId);

    void deleteByDlShareIdAndDlCollaboratorId(Long dlShareId, Long dlCollId);

    List<DLShareCollaborator> findAllByDlShareId(Long dlShareId);

    DLShareCollaborator findByDlShareIdAndDlCollaboratorId(Long dlShareId, Long dlCollId);

}

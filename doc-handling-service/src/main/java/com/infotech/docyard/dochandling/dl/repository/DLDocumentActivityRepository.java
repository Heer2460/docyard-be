package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DLDocumentActivityRepository extends JpaRepository<DLDocumentActivity, Long> {
    void deleteAllByDocId(Long id);

    Boolean existsByDocId(Long id);

}

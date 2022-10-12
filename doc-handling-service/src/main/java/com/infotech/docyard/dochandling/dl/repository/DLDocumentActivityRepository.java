package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface DLDocumentActivityRepository extends JpaRepository<DLDocumentActivity, Long> {
    @Modifying
    void deleteAllByDocId(Long id);

    Boolean existsByDocId(Long id);

    List<DLDocumentActivity> findAllByUserId(Long id);

}

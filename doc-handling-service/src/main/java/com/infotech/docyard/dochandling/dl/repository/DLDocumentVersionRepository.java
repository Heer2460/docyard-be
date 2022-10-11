package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DLDocumentVersionRepository extends JpaRepository<DLDocumentVersion, Long> {
    void deleteAllByDlDocument_Id(Long id);

    Boolean existsByDlDocument_Id(Long id);
}

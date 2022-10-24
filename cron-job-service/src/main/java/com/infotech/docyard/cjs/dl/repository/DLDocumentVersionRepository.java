package com.infotech.docyard.cjs.dl.repository;

import com.infotech.docyard.cjs.dl.entity.DLDocumentVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface DLDocumentVersionRepository extends JpaRepository<DLDocumentVersion, Long> {
    @Modifying
    void deleteAllByDlDocument_Id(Long id);

    Boolean existsByDlDocument_Id(Long id);
}

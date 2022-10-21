package com.infotech.docyard.js.dl.repository;


import com.infotech.docyard.js.dl.entity.DLDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DLDocumentRepository extends JpaRepository<DLDocument, Long> {


    List<DLDocument> findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();


}

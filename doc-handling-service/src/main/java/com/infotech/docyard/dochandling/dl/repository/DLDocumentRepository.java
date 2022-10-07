package com.infotech.docyard.dochandling.dl.repository;


import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface DLDocumentRepository extends JpaRepository<DLDocument, Long> {

    DLDocument findByIdAndArchivedFalseAndFolderTrue(Long folderId);

    List<DLDocument> findByParentIdAndArchivedOrderByUpdatedOnAsc(Long folderId, Boolean archived);

    List<DLDocument> findByParentIdIsNullAndArchivedOrderByUpdatedOnAsc(Boolean archived);

    List<DLDocument> findTop8ByCreatedByAndArchivedFalseAndFolderFalseAndCreatedOnBetweenOrderByUpdatedOnAsc(long creatorId, ZonedDateTime fromDate, ZonedDateTime toDate);

    List<DLDocument> findByParentIdAndArchivedFalse(Long parentId);

    DLDocument findByName(String name);

    Boolean existsByNameAndFolderTrue(String name);

    Boolean existsByName(String name);

}

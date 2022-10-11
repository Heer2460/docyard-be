package com.infotech.docyard.dochandling.dl.repository;


import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface DLDocumentRepository extends JpaRepository<DLDocument, Long> {

    DLDocument findByIdAndArchivedFalseAndFolderTrue(Long folderId);

    DLDocument findByIdAndArchivedFalseAndFolderFalse(Long documentId);

    List<DLDocument> findByParentIdAndArchivedOrderByUpdatedOnDesc(Long folderId, Boolean archived);

    List<DLDocument> findByCreatedByAndParentIdAndArchivedOrderByUpdatedOnDesc(Long ownerId, Long folderId, Boolean archived);

    List<DLDocument> findByCreatedByAndParentIdAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(Long ownerId, Long folderId, Boolean archived);

    List<DLDocument> findByParentIdAndFavouriteOrderByUpdatedOnDesc(Long folderId, Boolean favourite);


    List<DLDocument> findByParentIdIsNullAndArchivedOrderByUpdatedOnDesc(Boolean archived);

    List<DLDocument> findByParentIdIsNullAndCreatedByAndArchivedOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findAllByCreatedByAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findAllByParentIdIsNullAndFavouriteOrderByUpdatedOnDesc(Boolean favourite);


    List<DLDocument> findTop8ByCreatedByAndArchivedFalseAndFolderFalseAndCreatedOnBetweenOrderByUpdatedOnDesc(long creatorId, ZonedDateTime fromDate, ZonedDateTime toDate);

    List<DLDocument> findByParentIdAndArchivedFalse(Long parentId);

    DLDocument findByName(String name);

    Boolean existsByNameAndFolderTrue(String name);

    Boolean existsByName(String name);

    List<DLDocument> findAllByArchivedTrueAndCreatedByOrderByUpdatedOnDesc(Long ownerId);

    int countAllByArchivedFalseAndFolderFalseAndParentId(Long parentId);

    List<DLDocument> findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();


}

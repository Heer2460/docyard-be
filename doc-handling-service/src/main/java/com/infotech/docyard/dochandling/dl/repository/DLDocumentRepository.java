package com.infotech.docyard.dochandling.dl.repository;


import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface DLDocumentRepository extends JpaRepository<DLDocument, Long> {

    DLDocument findByIdAndArchivedFalseAndFolderTrue(Long folderId);

    DLDocument findByIdAndArchivedFalseAndFolderTrueAndSharedTrue(Long folderId);

    DLDocument findByIdAndArchivedFalseAndFolderFalse(Long documentId);

    DLDocument findByVersionGUIdAndArchivedFalseAndFolderFalse(String guid);

    DLDocument findByVersionGUIdAndArchivedFalseAndFolderFalseAndSharedTrue(String guid);

    List<DLDocument> findByParentIdAndArchivedOrderByUpdatedOnDesc(Long folderId, Boolean archived);

    List<DLDocument> findByParentIdAndArchivedAndFolderFalseOrderByUpdatedOnDesc(Long folderId, Boolean archived);

    List<DLDocument> findByParentIdIsNullAndArchivedAndFolderFalseOrderByUpdatedOnDesc(Boolean archived);

    List<DLDocument> findByCreatedByAndParentIdAndArchivedOrderByUpdatedOnDesc(Long ownerId, Long folderId, Boolean archived);

    List<DLDocument> findByParentIdAndArchivedFalseOrderByUpdatedOnDesc(Long folderId);

    List<DLDocument> findAllByCreatedByAndParentIdAndArchivedAndFolderFalseOrderByUpdatedOnDesc(Long ownerId, Long folderId, Boolean archived);

    List<DLDocument> findAllByCreatedByAndArchivedAndFolderFalseOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findByCreatedByAndParentIdAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(Long ownerId, Long folderId, Boolean archived);

    List<DLDocument> findByParentIdAndFavouriteOrderByUpdatedOnDesc(Long folderId, Boolean favourite);


    List<DLDocument> findByParentIdIsNullAndArchivedOrderByUpdatedOnDesc(Boolean archived);

    List<DLDocument> findByParentIdIsNullAndCreatedByAndArchivedOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findAllByAndCreatedByAndArchivedAndFolderFalseOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findAllByCreatedByAndArchivedAndFavouriteTrueOrderByUpdatedOnDesc(Long ownerId, Boolean archived);

    List<DLDocument> findAllByParentIdIsNullAndFavouriteOrderByUpdatedOnDesc(Boolean favourite);


    List<DLDocument> findTop8ByCreatedByAndArchivedFalseAndFolderFalseAndCreatedOnBetweenOrderByUpdatedOnDesc(long creatorId, ZonedDateTime fromDate, ZonedDateTime toDate);

    List<DLDocument> findByParentIdAndArchivedFalse(Long parentId);

    List<DLDocument> findAllByArchivedTrue();

    List<DLDocument> findAllByCreatedByAndArchivedFalse(Long userId);

    List<DLDocument> findAllByCreatedByAndSharedTrueAndArchivedFalseOrderByUpdatedOnDesc(Long userId);

    List<DLDocument> findByCreatedByAndParentIdAndArchivedFalseOrderByUpdatedOnDesc(Long userId, Long folderId);


    DLDocument findByName(String name);

    Boolean existsByNameAndFolderTrue(String name);

    Boolean existsByName(String name);

    DLDocument findByIdAndArchivedTrue(Long id);

    DLDocument findByIdAndArchivedFalse(Long id);

    List<DLDocument> findAllByArchivedTrueAndCreatedByOrderByUpdatedOnDesc(Long ownerId);

    int countAllByArchivedFalseAndFolderFalseAndParentId(Long parentId);

    List<DLDocument> findAllByFolderFalseAndArchivedFalseAndOcrDoneFalseAndOcrSupportedTrue();

    @Query("SELECT DISTINCT d FROM DLDocument d LEFT JOIN d.documentComments c " +
            "WHERE d.archived = false " +
            "AND d.createdBy = :userId " +
            "AND (d.name LIKE %:searchKey% " +
            "OR d.title LIKE %:searchKey% " +
            "OR d.versionGUId LIKE %:searchKey% " +
            "OR d.content LIKE %:searchKey% " +
            "OR c.message LIKE %:searchKey%) " +
            "order by d.updatedOn desc")
    List<DLDocument> findDLDocumentBySearchKey(@Param("searchKey") String searchKey, @Param("userId") Long userId);

    @Query("SELECT DISTINCT d FROM DLDocument d WHERE d.parentId =:parentId")
    List<DLDocument> findAllByParentId(@Param("parentId") Long parentId);

    @Query("SELECT DISTINCT d FROM DLDocument d WHERE d.parentId =:parentId AND d.archived =:archived")
    List<DLDocument> findAllByParentIdAndArchivedStatus(@Param("parentId") Long parentId, @Param("archived") boolean archived);

    @Query(value = "SELECT * FROM dhs_documents  WHERE NAME = :name ORDER BY id DESC LIMIT 1", nativeQuery = true)
    DLDocument findLatestDocumentByName(String name);
}

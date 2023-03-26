package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DLDocumentTagRepository extends JpaRepository<DLDocumentTag, Long> {

    List<DLDocumentTag> findAllByDlDocument_IdOrderByUpdatedOnDesc(Long documentId);
}

package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DLDocumentCommentRepository extends JpaRepository<DLDocumentComment, Long> {

    List<DLDocumentComment> findAllByDlDocument_Id(Long documentId);

    void deleteAllByDlDocument_Id(Long documentId);

    boolean existsByDlDocument_Id(Long documentId);

}

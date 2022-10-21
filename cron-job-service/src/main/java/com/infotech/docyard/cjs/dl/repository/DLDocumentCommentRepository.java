package com.infotech.docyard.cjs.dl.repository;

import com.infotech.docyard.cjs.dl.entity.DLDocumentComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;

public interface DLDocumentCommentRepository extends JpaRepository<DLDocumentComment, Long> {

    List<DLDocumentComment> findAllByDlDocument_IdOrderByUpdatedOnDesc(Long documentId);

    @Modifying
    void deleteAllByDlDocument_Id(Long documentId);

    boolean existsByDlDocument_Id(Long documentId);

}

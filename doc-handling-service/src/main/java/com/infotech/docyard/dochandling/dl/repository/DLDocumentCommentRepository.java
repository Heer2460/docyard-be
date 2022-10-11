package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocumentComment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DLDocumentCommentRepository extends JpaRepository<DLDocumentComment, Long> {

    public List<DLDocumentComment> findAllByDlDocument_Id(Long documentId);
}

package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DLShareRepository extends JpaRepository<DLShare, Long> {
}

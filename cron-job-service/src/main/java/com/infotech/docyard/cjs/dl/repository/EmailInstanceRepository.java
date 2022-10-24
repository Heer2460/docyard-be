package com.infotech.docyard.cjs.dl.repository;


import com.infotech.docyard.cjs.dl.entity.EmailInstance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmailInstanceRepository extends JpaRepository<EmailInstance, Long> {

    List<EmailInstance> findByStatusContainingIgnoreCase(String Status);

}

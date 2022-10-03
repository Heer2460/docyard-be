package com.infotech.docyard.dl.repository;

import com.infotech.docyard.dl.entity.ConfigSMTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfigSMTPRepository extends JpaRepository<ConfigSMTP, Long> {
    ConfigSMTP findFirstByOrderByCreatedOn();
}

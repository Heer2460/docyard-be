package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.ConfigSMTP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ConfigSMTPRepository extends JpaRepository<ConfigSMTP, Long> {
    ConfigSMTP findFirstByOrderByCreatedOn();
}

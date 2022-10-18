package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.ForgotPasswordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.ZonedDateTime;
import java.util.List;

@Repository
public interface ForgotPasswordLinkRepository extends JpaRepository<ForgotPasswordLink, Long> {

    ForgotPasswordLink findByToken(String token);

    List<ForgotPasswordLink> findAllByTokenIsNotNull();

}

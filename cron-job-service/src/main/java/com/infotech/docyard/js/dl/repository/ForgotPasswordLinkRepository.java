package com.infotech.docyard.js.dl.repository;

import com.infotech.docyard.js.dl.entity.ForgotPasswordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForgotPasswordLinkRepository extends JpaRepository<ForgotPasswordLink, Long> {

    ForgotPasswordLink findByToken(String token);

    List<ForgotPasswordLink> findAllByTokenIsNotNull();

}

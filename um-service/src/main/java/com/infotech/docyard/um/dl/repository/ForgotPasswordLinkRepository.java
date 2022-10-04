package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.ForgotPasswordLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ForgotPasswordLinkRepository extends JpaRepository<ForgotPasswordLink, Long> {

    ForgotPasswordLink findByToken(String token);


}

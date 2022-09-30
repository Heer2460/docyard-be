package com.infotech.dms.autentication.service.entity.repository;


import com.infotech.dms.autentication.service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

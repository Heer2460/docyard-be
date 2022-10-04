package com.infotech.docyard.um.auth.entity.repository;


import com.infotech.docyard.um.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}

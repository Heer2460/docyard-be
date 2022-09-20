package com.infotech.docyard.dl.repository;


import com.infotech.docyard.dl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserRepository extends JpaRepository<User, Long> {
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);
}

package com.infotech.docyard.dl.repository;


import com.infotech.docyard.dl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}

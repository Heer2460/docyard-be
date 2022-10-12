package com.infotech.docyard.um.dl.repository;


import com.infotech.docyard.um.dl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByEmail(String email);

    boolean existsByGroup_IdAndStatus(Long groupId,String status);

}

package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    Boolean existsByEmail(String email);
    UserProfile findByEmail(String email);
}

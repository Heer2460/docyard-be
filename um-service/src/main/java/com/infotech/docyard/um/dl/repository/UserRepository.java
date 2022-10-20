package com.infotech.docyard.um.dl.repository;


import com.infotech.docyard.um.dl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    User findByEmail(String email);

    boolean existsByGroup_IdAndStatus(Long groupId, String status);

    boolean existsByDepartmentIdsAndStatus(String departmentIds, String status);

    List<User>findAllByStatus(String status);

//    @Query("select user from User user where user.departmentIds like %:departmentIds% and user.status like %:status%")
//    List<User> findUserByDepartmentIdsAnAndStatus(@Param("departmentIds") List<String> departmentIds, @Param("status") String status);

}

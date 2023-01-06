package com.infotech.docyard.um.dl.repository;

import com.infotech.docyard.um.dl.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    User findByUserName(String userName);
    Boolean existsByUserName(String userName);
    List<User> findAllByStatus(String status);
    boolean existsByGroup_IdAndStatus(Long groupId, String status);
    boolean existsByDepartmentIdsAndStatus(String departmentIds, String status);

    @Query("select user from User user where user.userProfile.email = :email")
    User findUserByEmail(@Param("email") String email);

//    @Query("select user from User user where user.departmentIds like %:departmentIds% and user.status like %:status%")
//    List<User> findUserByDepartmentIdsAnAndStatus(@Param("departmentIds") List<String> departmentIds, @Param("status") String status);

}

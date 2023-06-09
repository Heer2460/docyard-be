package com.infotech.docyard.um.dl.repository;


import com.infotech.docyard.um.dl.entity.*;
import com.infotech.docyard.um.util.AppUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvSearchRepository {

    @Autowired
    EntityManager em;

    public List<Department> searchDepartment(String code, String name, String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);

        Root<Department> dptRoot = cq.from(Department.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(code)) {
            predicates.add(cb.like(dptRoot.get("code"), "%" + code + "%"));
        }
        if (!AppUtility.isEmpty(name)) {
            predicates.add(cb.like(dptRoot.get("name"), "%" + name + "%"));
        }
        if (!AppUtility.isEmpty(status)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + status + "%"));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.asc(dptRoot.get("code")));

        return em.createQuery(cq).getResultList();
    }

    public List<User> searchUser(String username, String name, Long groupId, Long departmentId, String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);
        Root<User> userRoot = cq.from(User.class);
        Join<User, UserProfile> joinUserProfile = userRoot.join("userProfile");

        Join<Object, Object> group = userRoot.join("group", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(username)) {
            predicates.add(cb.like(userRoot.get("userName"), "%" + username + "%"));
        }
        if (!AppUtility.isEmpty(name)) {
            predicates.add(cb.like(joinUserProfile.get("name"), "%" + name + "%"));
        }
        if (!AppUtility.isEmpty(groupId)) {
            predicates.add(cb.equal(group.get("id"), groupId));
        }

        // need to implement
        if (!AppUtility.isEmpty(departmentId)) {
            predicates.add(cb.like(userRoot.get("departmentIds"), "%" + departmentId.toString() + "%"));
        }
        if (!AppUtility.isEmpty(status)) {
            predicates.add(cb.equal(userRoot.get("status"), status));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.desc(userRoot.get("updatedOn")));

        return em.createQuery(cq).getResultList();
    }

    public List<Role> searchRole(String code, String name, String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Role> cq = cb.createQuery(Role.class);

        Root<Role> dptRoot = cq.from(Role.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(code)) {
            predicates.add(cb.like(dptRoot.get("code"), "%" + code + "%"));
        }
        if (!AppUtility.isEmpty(name)) {
            predicates.add(cb.like(dptRoot.get("name"), "%" + name + "%"));
        }
        if (!AppUtility.isEmpty(status)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + status + "%"));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.asc(dptRoot.get("code")));

        return em.createQuery(cq).getResultList();
    }

    public List<Group> searchGroup(String code, String name, String status, List<Long> role) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Group> cq = cb.createQuery(Group.class);

        Root<Group> group = cq.from(Group.class);
        Join<Object, Object> groupRoles = group.join("groupRoles", JoinType.LEFT);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(code)) {
            predicates.add(cb.like(group.get("code"), "%" + code + "%"));
        }
        if (!AppUtility.isEmpty(name)) {
            predicates.add(cb.like(group.get("name"), "%" + name + "%"));
        }
        if (!AppUtility.isEmpty(status)) {
            predicates.add(cb.like(group.get("status"), "%" + status + "%"));
        }
        if (!AppUtility.isEmpty(role)) {
            predicates.add(groupRoles.get("role").in(role));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.asc(group.get("code")));

        return em.createQuery(cq).getResultList();
    }
}

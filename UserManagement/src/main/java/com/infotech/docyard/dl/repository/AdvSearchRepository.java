package com.infotech.docyard.dl.repository;


import com.infotech.docyard.dl.entity.Department;
import com.infotech.docyard.dl.entity.User;
import com.infotech.docyard.util.AppUtility;
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

    public List<Department> searchDepartment(String code, String status) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Department> cq = cb.createQuery(Department.class);

        Root<Department> dptRoot = cq.from(Department.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(code)) {
            predicates.add(cb.like(dptRoot.get("code"), "%" + code + "%"));
        }
        if (!AppUtility.isEmpty(status)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + status + "%"));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.asc(dptRoot.get("code")));

        return em.createQuery(cq).getResultList();
    }

    public List<User> searchUser(String username, String email, String name, String phoneNumber) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> cq = cb.createQuery(User.class);

        Root<User> dptRoot = cq.from(User.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(username)) {
            predicates.add(cb.like(dptRoot.get("code"), "%" + username + "%"));
        }
        if (!AppUtility.isEmpty(email)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + email + "%"));
        }
        if (!AppUtility.isEmpty(name)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + name + "%"));
        }
        if (!AppUtility.isEmpty(phoneNumber)) {
            predicates.add(cb.like(dptRoot.get("status"), "%" + phoneNumber + "%"));
        }
        cq.where(predicates.toArray(new Predicate[0]))
                .distinct(true);
        cq.orderBy(cb.asc(dptRoot.get("code")));

        return em.createQuery(cq).getResultList();
    }
}

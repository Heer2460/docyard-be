package com.infotech.docyard.dochandling.dl.repository;

import com.infotech.docyard.dochandling.dl.entity.DLDocument;
import com.infotech.docyard.dochandling.dl.entity.DLDocumentTag;
import com.infotech.docyard.um.util.AppUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AdvTagSearchRepository {

    @Autowired
    EntityManager em;

    public List<DLDocumentTag> searchTags(String message) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DLDocumentTag> dlTag = cb.createQuery(DLDocumentTag.class);

        Root<DLDocumentTag> dptRoot = dlTag.from(DLDocumentTag.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(message)) {
            predicates.add(cb.like(dptRoot.get("message"), "%" + message + "%"));
        }
        dlTag.where(predicates.toArray(new Predicate[0]))
                .distinct(true);

        return em.createQuery(dlTag).getResultList();
    }

    public List<DLDocument> searchFavorite(String message) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DLDocument> dlTag = cb.createQuery(DLDocument.class);

        Root<DLDocument> dptRoot = dlTag.from(DLDocument.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(message)) {
            predicates.add(cb.like(dptRoot.get("message"), "%" + message + "%"));
        }
        dlTag.where(predicates.toArray(new Predicate[0]))
                .distinct(true);

        return em.createQuery(dlTag).getResultList();
    }

    public List<DLDocument> searchShared(String message) {
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<DLDocument> dlTag = cb.createQuery(DLDocument.class);

        Root<DLDocument> dptRoot = dlTag.from(DLDocument.class);

        List<Predicate> predicates = new ArrayList<>();
        if (!AppUtility.isEmpty(message)) {
            predicates.add(cb.like(dptRoot.get("message"), "%" + message + "%"));
        }
        dlTag.where(predicates.toArray(new Predicate[0]))
                .distinct(true);

        return em.createQuery(dlTag).getResultList();
    }
}

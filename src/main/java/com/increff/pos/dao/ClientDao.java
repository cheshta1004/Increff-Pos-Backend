package com.increff.pos.dao;

import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.criteria.*;
import java.util.List;
import javax.persistence.TypedQuery;

@Repository
@Transactional
public class ClientDao extends AbstractDao<ClientPojo> {

    public ClientDao() {
        super(ClientPojo.class);
    }

    public List<ClientPojo> selectAll(int page, int size) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ClientPojo> cq = cb.createQuery(ClientPojo.class);
        Root<ClientPojo> root = cq.from(ClientPojo.class);
        cq.select(root);
        return em().createQuery(cq)
            .setFirstResult(page * size)
            .setMaxResults(size)
            .getResultList();
    }

    public List<ClientPojo> selectByPartialName(String partialName, int page, int size) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ClientPojo> cq = cb.createQuery(ClientPojo.class);
        Root<ClientPojo> root = cq.from(ClientPojo.class);
        Predicate namePredicate = cb.like(cb.lower(root.get("clientName")), "%" + partialName.toLowerCase() + "%");
        cq.where(namePredicate);
        TypedQuery<ClientPojo> query = em().createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long getTotalCount() {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ClientPojo> root = cq.from(ClientPojo.class);
        cq.select(cb.count(root));
        return em().createQuery(cq).getSingleResult();
    }

    public long getTotalCountByPartialName(String partialName) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ClientPojo> root = cq.from(ClientPojo.class);
        Predicate namePredicate = cb.like(cb.lower(root.get("clientName")), "%" + partialName.toLowerCase() + "%");
        cq.where(namePredicate);
        cq.select(cb.count(root));
        return em().createQuery(cq).getSingleResult();
    }
}
package com.increff.pos.dao;

import org.springframework.stereotype.Repository;
import com.increff.pos.pojo.UserPojo;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

@Repository
public class UserDao extends AbstractDao<UserPojo> {
    public UserDao() {
        super(UserPojo.class);
    }

    public UserPojo getByEmail(String email) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<UserPojo> cq = cb.createQuery(UserPojo.class);
        Root<UserPojo> root = cq.from(UserPojo.class);
        Predicate emailPredicate = cb.equal(root.get("email"), email);
        cq.select(root).where(emailPredicate);
        TypedQuery<UserPojo> query = em().createQuery(cq);
        return query.getResultList().stream().findFirst().orElse(null);
    }

    public boolean emailExists(String email) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<UserPojo> root = cq.from(UserPojo.class);
        Predicate emailPredicate = cb.equal(root.get("email"), email);
        cq.select(cb.count(root)).where(emailPredicate);
        TypedQuery<Long> query = em().createQuery(cq);
        Long count = query.getSingleResult();
        return count > 0;
    }
}

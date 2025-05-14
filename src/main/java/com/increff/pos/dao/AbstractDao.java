package com.increff.pos.dao;

import com.increff.pos.pojo.AbstractVersionedPojo;
import org.springframework.transaction.annotation.Transactional;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.ParameterExpression;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

@Transactional
public abstract class AbstractDao<T extends AbstractVersionedPojo> {
	@PersistenceContext
	protected EntityManager em;
	private final Class<T> pojoClass;
	protected AbstractDao(Class<T> pojoClass) {
		this.pojoClass = pojoClass;
	}
	protected <T> T getSingle(TypedQuery<T> query) {
		List<T> list = query.getResultList();
		if (list.isEmpty()) {
			return null;
		}
		return list.get(0);
	}
	
	protected <T> TypedQuery<T> getQuery(String jpql, Class<T> clazz) {
		return em.createQuery(jpql, clazz);
	}
	
	protected EntityManager em() {
		return em;
	}

	protected CriteriaBuilder getCriteriaBuilder() {
		return em.getCriteriaBuilder();
	}

	public <T> void insert(T t)  {
		em.persist(t);
	}

	public T select(String member, Object value) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(pojoClass);
		ParameterExpression<Object> p = cb.parameter(Object.class);
		Root<T> from = cq.from(pojoClass);
		cq.select(from).where(cb.equal(from.get(member), p));
		TypedQuery<T> query = em.createQuery(cq);
		query.setParameter(p, value);
		List<T> results = query.getResultList();
		if (results.isEmpty()) {
			return null;
		}
		if (results.size() > 1) {
			throw new NonUniqueResultException("Multiple results found for " + member + " = " + value);
		}
		return results.get(0);
	}

	public T select(Serializable id) {
		return em.find(pojoClass, id);
	}

	public void persist(T entity) {
		em.persist(entity);
	}
	
	public List<T> selectAll() {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(pojoClass);
		Root<T> root = cq.from(pojoClass);
		cq.select(root);
		return em.createQuery(cq).getResultList();
	}

	public List<T> selectAll(int page, int size) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(pojoClass);
		Root<T> root = cq.from(pojoClass);
		cq.select(root);
		TypedQuery<T> query = em.createQuery(cq);
		query.setFirstResult(page * size);
		query.setMaxResults(size);
		return query.getResultList();
	}

	public void delete(Integer id) {
		T pojo = em.find(pojoClass, id);
		if (!Objects.isNull(pojo)) {
			em.remove(pojo);
		}
	}

	public static <T> T selectSingleOrNull(TypedQuery<T> query) {
		try {
			return query.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

	public void update(T pojo) {
		em.merge(pojo);
	}
}
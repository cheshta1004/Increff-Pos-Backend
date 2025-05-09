package com.increff.pos.dao;

import com.increff.pos.pojo.ProductPojo;
import com.increff.pos.pojo.ClientPojo;
import org.springframework.stereotype.Repository;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Repository
@Transactional
public class ProductDao extends AbstractDao<ProductPojo> {

    public ProductDao() {
        super(ProductPojo.class);
    }

    public List<ProductPojo> getAllProductsByClientId(Integer clientId) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);
        cq.select(root).where(cb.equal(root.get("clientId"), clientId));
        return em().createQuery(cq).getResultList();
    }

    public List<ProductPojo> filterProducts(String productName, Integer clientId) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);

        List<Predicate> predicates = new ArrayList<>();

        if (!Objects.isNull(productName) && !productName.trim().isEmpty()) {
            predicates.add(cb.like(cb.lower(root.get("name")), "%" + productName.toLowerCase() + "%"));
        }
        if (!Objects.isNull(clientId)) {
            predicates.add(cb.equal(root.get("clientId"), clientId));
        }
        cq.select(root).where(cb.and(predicates.toArray(new Predicate[0])));
        return em().createQuery(cq).getResultList();
    }

    public List<ProductPojo> selectByClientName(String clientName, int page, int size) {

        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Integer> clientQuery = cb.createQuery(Integer.class);
        Root<ClientPojo> clientRoot = clientQuery.from(ClientPojo.class);
        clientQuery.select(clientRoot.get("id"))
                  .where(cb.like(cb.lower(clientRoot.get("clientName")), 
                               "%" + clientName.toLowerCase().trim() + "%"));
        List<Integer> clientIds = em().createQuery(clientQuery).getResultList();

        if (clientIds.isEmpty()) {
            return new ArrayList<>();
        }
        CriteriaQuery<ProductPojo> productQuery = cb.createQuery(ProductPojo.class);
        Root<ProductPojo> productRoot = productQuery.from(ProductPojo.class);
        productQuery.select(productRoot)
                   .where(productRoot.get("clientId").in(clientIds));

        TypedQuery<ProductPojo> query = em().createQuery(productQuery);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public List<ProductPojo> selectByPartialBarcode(String barcode, int page, int size) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);
        Predicate barcodePredicate = cb.like(cb.lower(root.get("barcode")), "%" + barcode.toLowerCase().trim() + "%");
        cq.select(root).where(barcodePredicate);
        TypedQuery<ProductPojo> query = em().createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public Long getTotalCount() {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);
        cq.select(cb.count(root));
        return em().createQuery(cq).getSingleResult();
    }

    public Long getTotalCountByClientName(String clientName) {
        // First get the client ID from the client name
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Integer> clientQuery = cb.createQuery(Integer.class);
        Root<ClientPojo> clientRoot = clientQuery.from(ClientPojo.class);
        clientQuery.select(clientRoot.get("id"))
                  .where(cb.like(cb.lower(clientRoot.get("clientName")), 
                               "%" + clientName.toLowerCase().trim() + "%"));
        List<Integer> clientIds = em().createQuery(clientQuery).getResultList();

        if (clientIds.isEmpty()) {
            return 0L;
        }
        CriteriaQuery<Long> productQuery = cb.createQuery(Long.class);
        Root<ProductPojo> productRoot = productQuery.from(ProductPojo.class);
        productQuery.select(cb.count(productRoot))
                   .where(productRoot.get("clientId").in(clientIds));
        return em().createQuery(productQuery).getSingleResult();
    }

    public Long getTotalCountByPartialBarcode(String barcode) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);
        Predicate barcodePredicate = cb.like(cb.lower(root.get("barcode")), "%" + barcode.toLowerCase().trim() + "%");
        cq.select(cb.count(root)).where(barcodePredicate);
        return em().createQuery(cq).getSingleResult();
    }

    public List<ProductPojo> searchProducts(String searchTerm, int page, int size) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<ProductPojo> cq = cb.createQuery(ProductPojo.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);

        Predicate searchPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase().trim() + "%"),
            cb.like(cb.lower(root.get("barcode")), "%" + searchTerm.toLowerCase().trim() + "%")
        );
        
        cq.select(root).where(searchPredicate);
        TypedQuery<ProductPojo> query = em().createQuery(cq);
        query.setFirstResult(page * size);
        query.setMaxResults(size);
        return query.getResultList();
    }

    public long getTotalSearchResults(String searchTerm) {
        CriteriaBuilder cb = em().getCriteriaBuilder();
        CriteriaQuery<Long> cq = cb.createQuery(Long.class);
        Root<ProductPojo> root = cq.from(ProductPojo.class);

        Predicate searchPredicate = cb.or(
            cb.like(cb.lower(root.get("name")), "%" + searchTerm.toLowerCase().trim() + "%"),
            cb.like(cb.lower(root.get("barcode")), "%" + searchTerm.toLowerCase().trim() + "%")
        );
        
        cq.select(cb.count(root)).where(searchPredicate);
        return em().createQuery(cq).getSingleResult();
    }

}

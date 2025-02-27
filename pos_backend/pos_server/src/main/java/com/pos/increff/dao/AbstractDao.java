package com.pos.increff.dao;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.Query;
import java.util.List;

public abstract class AbstractDao {

    @PersistenceContext
    private EntityManager em;

    protected EntityManager em() {
        return em;
    }

    protected <T> T getSingle(TypedQuery<T> query) {
        return query.getResultList().stream().findFirst().orElse(null);
    }

    protected <T> TypedQuery<T> getQuery(String jpql, Class<T> clazz) {
        return em.createQuery(jpql, clazz);
    }

    protected <T> T selectQuery(int id, Class<T> clazz) {
        return em().find(clazz, id);
    }

    protected void insertQuery(Object entity) {
        em().persist(entity);
    }

    protected void batchInsertQuery(List<?> entities) {
        int batchSize = 50;  // Adjust batch size based on your needs
        for (int i = 0; i < entities.size(); i++) {
            em().persist(entities.get(i));
            if (i % batchSize == 0 && i > 0) {
                // Flush and clear the persistence context periodically
                em().flush();
                em().clear();
            }
        }
        // Final flush to ensure all entities are persisted
        em().flush();
    }

    protected Query updateQuery(String queryString) {
        return em().createQuery(queryString);
    }

    protected void deleteQuery(Object entity) {
        em().remove(entity);
    }

    protected void mergeQuery(Object entity) {
        em().merge(entity);
    }

    protected <T> List<T> getPaginatedResults(String jpql, Class<T> clazz, int page, int pageSize) {
        TypedQuery<T> query = getQuery(jpql, clazz);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize + 1);
        return query.getResultList();
    }

    protected <T> List<T> getPaginatedResults(String jpql, Class<T> clazz, int page, int pageSize, String paramName, Object paramValue) {
        System.out.println("jpql: " + jpql);
        System.out.println("paramName: " + paramName);
        System.out.println("paramValue: " + paramValue);
        TypedQuery<T> query = getQuery(jpql, clazz);
        query.setParameter(paramName, paramValue);
        query.setFirstResult((page - 1) * pageSize);
        query.setMaxResults(pageSize + 1);
        return query.getResultList();
    }

    protected Long getCount(String countJpql) {
        TypedQuery<Long> query = em().createQuery(countJpql, Long.class);
        return query.getSingleResult();
    }

}


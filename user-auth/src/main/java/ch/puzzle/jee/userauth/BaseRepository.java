package ch.puzzle.jee.userauth;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.util.*;

public abstract class BaseRepository<T> {

    @PersistenceContext
    protected EntityManager entityManager;

    private final Class<T> entityType;

    protected BaseRepository() {
        entityType = Objects.requireNonNull(ReflectionUtil.<T>getActualTypeArguments(getClass(), 0));
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    protected EntityManager getEntityManager() {
        return entityManager;
    }

    public void persist(T entity) {
        entityManager.persist(entity);
    }

    public T merge(T entity) {
        return entityManager.merge(entity);
    }

    public void remove(T entity) {
        entityManager.remove(entity);
    }

    protected EntityGraph<T> getGraph() {
        return getEntityManager().createEntityGraph(entityType);
    }

    protected T findWithGraph(EntityGraph<T> graph, long id) {
        Map<String, Object> hints = new HashMap<>();
        hints.put("javax.persistence.loadgraph", graph);

        return getEntityManager().find(entityType, id, hints);
    }

    /**
     * Deletes the entity with the provided id.
     *
     * @param id id of the entity to delete.
     * @return true if the entity to delete was found - otherwise false.
     */
    public boolean remove(long id) {
        T t = find(id);
        if (t == null) {
            return false;
        }
        entityManager.remove(t);
        return true;
    }

    public T find(long id) {
        return entityManager.find(entityType, id);
    }

    /**
     * Returns a type-safe result list which contains all entities of the provided type.
     *
     * @return the type-safe result list.
     */
    public List<T> findAll() {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(entityType);
        CriteriaQuery<T> all = query.select(query.from(entityType));
        return resultList(entityManager.createQuery(all));
    }

    /**
     * Returns a type-safe result list of the given query.
     *
     * @param query the query
     * @return the result list
     */
    protected List<T> resultList(TypedQuery<T> query) {
        return query.getResultList();
    }

    /**
     * Returns a type-safe single result of the given query or null.
     *
     * @param query
     * @return the result or null
     * @throws NonUniqueResultException if more than one result
     */
    protected T singleResult(TypedQuery<T> query) {
        List<T> resultList = resultList(query);

        if (resultList.isEmpty()) {
            return null;
        }

        if (resultList.size() > 1) {
            // maybe the result is a join, so make it distinct.
            Set<T> distinctResult = new HashSet<>(resultList);
            if (distinctResult.size() > 1) {
                throw new NonUniqueResultException("Result for query '" + query + "' must contain exactly one item");
            }
        }

        return resultList.get(0);
    }

    protected TypedQuery<T> createNamedQuery(String queryName) {
        return entityManager.createNamedQuery(queryName, entityType);
    }
}

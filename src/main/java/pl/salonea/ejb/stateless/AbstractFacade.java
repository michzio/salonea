package pl.salonea.ejb.stateless;

import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Created by michzio on 11/07/2015.
 *
 * Abstract Facade for Session Bean
 *
 */
@TransactionManagement(TransactionManagementType.CONTAINER)
@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class AbstractFacade<T> {

    private Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public abstract EntityManager getEntityManager();

    public @NotNull T create(@NotNull T entity) {
        getEntityManager().persist(entity);
        return entity;
    }

    public @NotNull T update(@NotNull T entity) {

        return getEntityManager().merge(entity);
    }

    public void refresh(@NotNull T entity) {

       getEntityManager().refresh(entity);
    }

    public void remove(@NotNull T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        return findAll(null, null);
    }

    public List<T> findAll(Integer start, Integer limit) {
        return findRange(start, limit);
    }

    public List<T> findRange(Integer start, Integer limit) {
        CriteriaQuery<T> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        Root<T> e = criteriaQuery.from(entityClass);
        criteriaQuery.select(e);
        Query query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setMaxResults(limit);
            query.setFirstResult(start);
        }
        return query.getResultList();
    }

    public List<T> findRange(int[] range) {
        return findRange(range[0], range[1] - range[0]);
    }

    public Long count() {
        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<T> e = criteriaQuery.from(entityClass);
        criteriaQuery.select(criteriaBuilder.count(e));
        // criteriaQuery.where(/* some predicate */);
        return getEntityManager().createQuery(criteriaQuery).getSingleResult();
    }
}

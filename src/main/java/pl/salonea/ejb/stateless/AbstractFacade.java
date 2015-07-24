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
        CriteriaQuery<T> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        Root<T> e = criteriaQuery.from(entityClass);
        criteriaQuery.select(e);
        return getEntityManager().createQuery(criteriaQuery).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery<T> criteriaQuery = getEntityManager().getCriteriaBuilder().createQuery(entityClass);
        Root<T> e = criteriaQuery.from(entityClass);
        criteriaQuery.select(e);
        Query query = getEntityManager().createQuery(criteriaQuery);
        query.setMaxResults(range[1] - range[0]);
        query.setFirstResult(range[0]);
        return query.getResultList();
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

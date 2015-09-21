package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.IndustryFacadeInterface;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Industry_;
import pl.salonea.entities.Provider;

import javax.ejb.*;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
@Stateless
@LocalBean
@TransactionManagement(TransactionManagementType.CONTAINER) // default
@TransactionAttribute(TransactionAttributeType.REQUIRED) // default
public class IndustryFacade extends AbstractFacade<Industry> implements IndustryFacadeInterface.Local, IndustryFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public IndustryFacade() {
        super(Industry.class);
    }

    @Override
    public Industry findForName(String name) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_FOR_NAME, Industry.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public List<Industry> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Industry> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_BY_NAME, Industry.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Industry> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Industry> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<Industry> query = getEntityManager().createNamedQuery(Industry.FIND_BY_PROVIDER, Industry.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Industry> findByMultipleCriteria(List<Provider> providers, String name, String description) {
        return findByMultipleCriteria(providers, name, description, null, null);
    }

    @Override
    public List<Industry> findByMultipleCriteria(List<Provider> providers, String name, String description, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Industry> criteriaQuery = criteriaBuilder.createQuery(Industry.class);
        // FROM
        Root<Industry> industry = criteriaQuery.from(Industry.class);
        // SELECT
        criteriaQuery.select(industry).distinct(true);

        // INNER JOIN-s
        Join<Industry, Provider> provider = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(providers != null && providers.size() > 0) {

            if(provider == null) provider = industry.join(Industry_.providers);

            predicates.add( provider.in(providers) );
        }

        if(name != null) {
            predicates.add( criteriaBuilder.like(industry.get(Industry_.name), "%" + name + "%") );
        }

        if(description != null) {
            predicates.add( criteriaBuilder.like(industry.get(Industry_.description), "%" + description + "%") );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Industry> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }
}

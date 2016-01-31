package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.PaymentMethodFacadeInterface;
import pl.salonea.entities.PaymentMethod;
import pl.salonea.entities.PaymentMethod_;
import pl.salonea.entities.Provider;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 24/07/2015.
 */
@Stateless
@LocalBean
public class PaymentMethodFacade extends AbstractFacade<PaymentMethod> implements PaymentMethodFacadeInterface.Local, PaymentMethodFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public PaymentMethodFacade() {
        super(PaymentMethod.class);
    }


    @Override
    public PaymentMethod update(PaymentMethod paymentMethod, Boolean retainTransientFields) {

        if (retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            PaymentMethod currentPaymentMethod = findByIdEagerly(paymentMethod.getId());
            if (currentPaymentMethod != null) {
                paymentMethod.setAcceptingProviders(currentPaymentMethod.getAcceptingProviders());
            }
        }
        return update(paymentMethod);
    }

    @Override
    public List<PaymentMethod> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<PaymentMethod> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_ALL_EAGERLY, PaymentMethod.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public PaymentMethod findByIdEagerly(Integer paymentMethodId) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_ID_EAGERLY, PaymentMethod.class);
        query.setParameter("paymentMethodId", paymentMethodId);
        try {
            return query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public PaymentMethod findForName(String name) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_FOR_NAME, PaymentMethod.class);
        query.setParameter("name", name);
        return query.getSingleResult();
    }

    @Override
    public List<PaymentMethod> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<PaymentMethod> findByName(String name, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_NAME, PaymentMethod.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<PaymentMethod> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_DESCRIPTION, PaymentMethod.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<PaymentMethod> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_KEYWORD, PaymentMethod.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findInAdvance(Boolean inAdvance) {
        return findInAdvance(inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findInAdvance(Boolean inAdvance, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_IN_ADVANCE, PaymentMethod.class);
        query.setParameter("in_advance", inAdvance);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance) {
        return findByNameAndInAdvance(name, inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findByNameAndInAdvance(String name, Boolean inAdvance, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_NAME_AND_IN_ADVANCE, PaymentMethod.class);
        query.setParameter("name", "%" + name + "%");
        query.setParameter("in_advance", inAdvance);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<PaymentMethod> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_PROVIDER, PaymentMethod.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<PaymentMethod> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<PaymentMethod> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<PaymentMethod> query = getEntityManager().createNamedQuery(PaymentMethod.FIND_BY_PROVIDER_EAGERLY, PaymentMethod.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(PaymentMethod.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public List<PaymentMethod> findByMultipleCriteria(List<Provider> providers, String name, String description, Boolean inAdvance) {
        return findByMultipleCriteria(providers, name, description, inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findByMultipleCriteria(List<Provider> providers, String name, String description, Boolean inAdvance, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, name, description, inAdvance, false, start, limit);
    }

    @Override
    public List<PaymentMethod> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, Boolean inAdvance) {
        return findByMultipleCriteriaEagerly(providers, name, description, inAdvance, null, null);
    }

    @Override
    public List<PaymentMethod> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, Boolean inAdvance, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, name, description, inAdvance, true, start, limit);
    }

    private List<PaymentMethod> findByMultipleCriteria(List<Provider> providers, String name, String description, Boolean inAdvance, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<PaymentMethod> criteriaQuery = criteriaBuilder.createQuery(PaymentMethod.class);
        // FROM
        Root<PaymentMethod> paymentMethod = criteriaQuery.from(PaymentMethod.class);
        // SELECT
        criteriaQuery.select(paymentMethod).distinct(true);

        // INNER JOIN-s
        Join<PaymentMethod, Provider> provider = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(providers != null && providers.size() > 0) {

            if(provider == null) provider = paymentMethod.join(PaymentMethod_.acceptingProviders);

            predicates.add(provider.in(providers));

            if(eagerly) {
                // then fetch associated collection of entities
                paymentMethod.fetch("acceptingProviders", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            paymentMethod.fetch("acceptingProviders", JoinType.LEFT);
        }

        if(name != null) {
            predicates.add( criteriaBuilder.like(paymentMethod.get(PaymentMethod_.name), "%" + name + "%") );
        }

        if(description != null) {
            predicates.add( criteriaBuilder.like(paymentMethod.get(PaymentMethod_.description), "%" + description + "%") );
        }

        if(inAdvance != null) {
            predicates.add( criteriaBuilder.equal(paymentMethod.get(PaymentMethod_.inAdvance), inAdvance) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<PaymentMethod> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }

}

package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
@Stateless
@LocalBean
public class ProviderFacade extends AbstractFacade<Provider> implements ProviderFacadeInterface.Local, ProviderFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProviderFacade() { super(Provider.class); }

    @Override
    public List<Provider> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Provider> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_ALL_EAGERLY, Provider.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Provider findByIdEagerly(Long providerId) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_ID_EAGERLY, Provider.class);
        query.setParameter("providerId", providerId);
        try {
            return query.getSingleResult();
        } catch(NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Provider> findByCorporation(Corporation corporation) {
        return findByCorporation(corporation, null, null);
    }

    @Override
    public List<Provider> findByCorporation(Corporation corporation, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_CORPORATION, Provider.class);
        query.setParameter("corporation", corporation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findByType(ProviderType providerType) {
        return findByType(providerType, null, null);
    }

    @Override
    public List<Provider> findByType(ProviderType providerType, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_TYPE, Provider.class);
        query.setParameter("provider_type", providerType);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findByIndustry(Industry industry) {
        return findByIndustry(industry, null, null);
    }

    @Override
    public List<Provider> findByIndustry(Industry industry, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_INDUSTRY, Provider.class);
        query.setParameter("industry", industry);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findByPaymentMethod(PaymentMethod paymentMethod) {
        return findByPaymentMethod(paymentMethod, null, null);
    }

    @Override
    public List<Provider> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_PAYMENT_METHOD, Provider.class);
        query.setParameter("payment_method", paymentMethod);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findBySuppliedService(Service service) {
        return findBySuppliedService(service, null, null);
    }

    @Override
    public List<Provider> findBySuppliedService(Service service, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_BY_SUPPLIED_SERVICE, Provider.class);
        query.setParameter("service",service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findRated() {
        return findRated(null, null);
    }

    @Override
    public List<Provider> findRated(Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_RATED, Provider.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findUnrated() {
        return findUnrated(null, null);
    }

    @Override
    public List<Provider> findUnrated(Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_UNRATED, Provider.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findOnAvgRatedAbove(Double minAvgRating) {
        return findOnAvgRatedAbove(minAvgRating, null, null);
    }

    @Override
    public List<Provider> findOnAvgRatedAbove(Double minAvgRating, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_ON_AVG_RATED_ABOVE, Provider.class);
        query.setParameter("avg_rating", minAvgRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findOnAvgRatedBelow(Double maxAvgRating) {
        return findOnAvgRatedBelow(maxAvgRating, null, null);
    }

    @Override
    public List<Provider> findOnAvgRatedBelow(Double maxAvgRating, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_ON_AVG_RATED_BELOW, Provider.class);
        query.setParameter("avg_rating", maxAvgRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Provider> findRatedByClient(Client client) {
        return findRatedByClient(client, null, null);
    }

    @Override
    public List<Provider> findRatedByClient(Client client, Integer start, Integer limit) {

        TypedQuery<Provider> query = getEntityManager().createNamedQuery(Provider.FIND_RATED_BY_CLIENT, Provider.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}

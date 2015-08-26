package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderRatingFacadeInterface;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ProviderRating;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
@Stateless
@LocalBean
public class ProviderRatingFacade extends AbstractFacade<ProviderRating> implements ProviderRatingFacadeInterface.Local, ProviderRatingFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProviderRatingFacade() {
        super(ProviderRating.class);
    }

    @Override
    public List<ProviderRating> findByClient(Client client) {
        return findByClient(client, null, null);
    }

    @Override
    public List<ProviderRating> findByClient(Client client, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_BY_CLIENT, ProviderRating.class);
        query.setParameter("client", client);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ProviderRating> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_BY_PROVIDER, ProviderRating.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findForProviderByRating(Provider provider, Short rating) {
        return findForProviderByRating(provider, rating, null, null);
    }

    @Override
    public List<ProviderRating> findForProviderByRating(Provider provider, Short rating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FOR_PROVIDER_BY_RATING, ProviderRating.class);
        query.setParameter("provider", provider);
        query.setParameter("rating", rating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findForProviderAboveRating(Provider provider, Short minRating) {
        return findForProviderAboveRating(provider, minRating, null, null);
    }

    @Override
    public List<ProviderRating> findForProviderAboveRating(Provider provider, Short minRating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FOR_PROVIDER_ABOVE_RATING, ProviderRating.class);
        query.setParameter("provider", provider);
        query.setParameter("min_rating", minRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findForProviderBelowRating(Provider provider, Short maxRating) {
        return findForProviderBelowRating(provider, maxRating, null, null);
    }

    @Override
    public List<ProviderRating> findForProviderBelowRating(Provider provider, Short maxRating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FOR_PROVIDER_BELOW_RATING, ProviderRating.class);
        query.setParameter("provider", provider);
        query.setParameter("max_rating", maxRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findFromClientByRating(Client client, Short rating) {
        return findFromClientByRating(client, rating, null, null);
    }

    @Override
    public List<ProviderRating> findFromClientByRating(Client client, Short rating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FROM_CLIENT_BY_RATING, ProviderRating.class);
        query.setParameter("client", client);
        query.setParameter("rating", rating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findFromClientAboveRating(Client client, Short minRating) {
        return findFromClientAboveRating(client, minRating, null, null);
    }

    @Override
    public List<ProviderRating> findFromClientAboveRating(Client client, Short minRating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FROM_CLIENT_ABOVE_RATING, ProviderRating.class);
        query.setParameter("client", client);
        query.setParameter("min_rating", minRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderRating> findFromClientBelowRating(Client client, Short maxRating) {
        return findFromClientBelowRating(client, maxRating, null, null);
    }

    @Override
    public List<ProviderRating> findFromClientBelowRating(Client client, Short maxRating, Integer start, Integer limit) {

        TypedQuery<ProviderRating> query = getEntityManager().createNamedQuery(ProviderRating.FIND_FROM_CLIENT_BELOW_RATING, ProviderRating.class);
        query.setParameter("client", client);
        query.setParameter("max_rating", maxRating);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Double findProviderAvgRating(Provider provider) {

        TypedQuery<Double> query = getEntityManager().createNamedQuery(ProviderRating.FIND_PROVIDER_AVG_RATING, Double.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countProviderRatings(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderRating.COUNT_PROVIDER_RATINGS, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countClientRatings(Client client) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderRating.COUNT_CLIENT_RATINGS, Long.class);
        query.setParameter("client", client);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByClient(Client client) {

        Query query = getEntityManager().createNamedQuery(ProviderRating.DELETE_BY_CLIENT);
        query.setParameter("client", client);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByProvider(Provider provider) {

        Query query = getEntityManager().createNamedQuery(ProviderRating.DELETE_BY_PROVIDER);
        query.setParameter("provider", provider);
        return query.executeUpdate();
    }

}
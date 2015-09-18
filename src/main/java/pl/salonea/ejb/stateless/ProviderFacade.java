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
import javax.persistence.criteria.*;
import java.util.ArrayList;
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

    @Override
    public List<Provider> findByMultipleCriteria(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods, List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description) {
        return findByMultipleCriteria(corporations, types, industries, paymentMethods, services, rated, minAvgRating, maxAvgRating, clients, providerName, description, null, null);
    }

    @Override
    public List<Provider> findByMultipleCriteria(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods, List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description, Integer start, Integer limit) {
        return findByMultipleCriteria(corporations, types, industries, paymentMethods, services, rated, minAvgRating, maxAvgRating, clients, providerName, description, false, start, limit);
    }

    @Override
    public List<Provider> findByMultipleCriteriaEagerly(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods, List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description) {
        return findByMultipleCriteriaEagerly(corporations, types, industries, paymentMethods, services, rated, minAvgRating, maxAvgRating, clients, providerName, description, null, null);
    }

    @Override
    public List<Provider> findByMultipleCriteriaEagerly(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods, List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description, Integer start, Integer limit) {
        return findByMultipleCriteria(corporations, types, industries, paymentMethods, services, rated, minAvgRating, maxAvgRating, clients, providerName, description, true, start, limit);
    }

    private List<Provider> findByMultipleCriteria(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods,
                                                  List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> ratingClients, String providerName, String description, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Provider> criteriaQuery = criteriaBuilder.createQuery(Provider.class);
        // FROM
        Root<Provider> provider = criteriaQuery.from(Provider.class);
        // SELECT
        criteriaQuery.select(provider).distinct(true);

        // INNER JOIN-s
        Join<Provider, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;
        Join<Provider, ProviderRating> providerRating = null;
        Join<ProviderRating, Client> client = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        // HAS BEEN GROUPED BY
        Boolean groupedBy = false;
        // HAVING PREDICATES
        List<Predicate> havingPredicates = new ArrayList<>();

        if(corporations != null && corporations.size() > 0) {

            predicates.add( provider.get(Provider_.corporation).in(corporations) );
        }

        if(types != null && types.size() > 0) {

            predicates.add( provider.get(Provider_.type).in(types) );
        }

        if(industries != null && industries.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(Industry industry : industries) {
                orPredicates.add( criteriaBuilder.isMember(industry, provider.get(Provider_.industries)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

            if(eagerly) {
                // then fetch associated collection of entities
                provider.fetch("industries", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            provider.fetch("industries", JoinType.LEFT);
        }

        if(paymentMethods != null && paymentMethods.size() > 0) {

            List<Predicate> orPredicates = new ArrayList<>();

            for(PaymentMethod paymentMethod : paymentMethods) {
                orPredicates.add( criteriaBuilder.isMember(paymentMethod, provider.get(Provider_.acceptedPaymentMethods)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );

            if(eagerly) {
                // then fetch associated collection of entities
                provider.fetch("acceptedPaymentMethods", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            provider.fetch("acceptedPaymentMethods", JoinType.LEFT);
        }

        if(services != null && services.size() > 0) {

            if(providerService == null) providerService = provider.join(Provider_.suppliedServiceOffers);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );

            if(eagerly) {
                // then fetch associated collection of entities
                provider.fetch("suppliedServiceOffers", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            provider.fetch("suppliedServiceOffers", JoinType.LEFT);
        }

        if(rated != null) {
            Expression<Integer> ratingsCount = criteriaBuilder.size(provider.get(Provider_.receivedRatings));
            if (rated) {
                predicates.add( criteriaBuilder.greaterThan(ratingsCount, 0) );
            } else {
                predicates.add(criteriaBuilder.equal(ratingsCount, 0));
            }
        }

        Fetch<Provider, ProviderRating> fetchRatings = null;

        if(minAvgRating != null) {

            if(providerRating == null) providerRating = provider.join(Provider_.receivedRatings); 

            if(!groupedBy) {
                criteriaQuery.groupBy(provider);
                groupedBy = true;
            }

            final Expression<Double> avgRating = criteriaBuilder.avg(providerRating.get(ProviderRating_.clientRating));
            havingPredicates.add( criteriaBuilder.greaterThanOrEqualTo(avgRating, minAvgRating) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = provider.fetch("receivedRatings", JoinType.INNER);
            }
        }

        if(maxAvgRating != null) {

            if(providerRating == null) providerRating = provider.join(Provider_.receivedRatings);

            if(!groupedBy) {
                criteriaQuery.groupBy(provider);
                groupedBy = true;
            }

            final Expression<Double> avgRating = criteriaBuilder.avg(providerRating.get(ProviderRating_.clientRating));
            havingPredicates.add( criteriaBuilder.lessThanOrEqualTo(avgRating, maxAvgRating) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = provider.fetch("receivedRatings", JoinType.INNER);
            }
        }

        if(ratingClients != null && ratingClients.size() > 0) {

            if(providerRating == null) providerRating = provider.join(Provider_.receivedRatings);
            if(client == null) client = providerRating.join(ProviderRating_.client);

            predicates.add( client.in(ratingClients) );

            if(eagerly && fetchRatings == null) {
                // then fetch associated collection of entities
                fetchRatings = provider.fetch("receivedRatings", JoinType.INNER);
            }
        }

        if(eagerly && fetchRatings == null) {
            // then left fetch associated collection of entities
            fetchRatings = provider.fetch("receivedRatings", JoinType.LEFT);
        }

        if(providerName != null) {
            predicates.add( criteriaBuilder.like(provider.get(Provider_.providerName), "%" + providerName + "%") );
        }

        if(description != null) {
            predicates.add(criteriaBuilder.like(provider.get(Provider_.description), "%" + description + "%"));
        }

        if(eagerly)
            // then fetch associated collection of entities
            provider.fetch("servicePoints", JoinType.LEFT);

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));
        if(groupedBy) {
            // HAVING predicate1 AND predicate2 AND ... AND predicateN
            criteriaQuery.having(havingPredicates.toArray(new Predicate[] {}));
        }

        TypedQuery<Provider> query = getEntityManager().createQuery(criteriaQuery);

        return query.getResultList();
    }
}

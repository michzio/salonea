package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.CorporationFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.Address_;
import pl.salonea.entities.Corporation;
import pl.salonea.entities.Corporation_;
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
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 22/07/2015.
 */
@Stateless
@LocalBean
public class CorporationFacade extends AbstractFacade<Corporation> implements CorporationFacadeInterface.Local, CorporationFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public CorporationFacade() {
        super(Corporation.class);
    }

    @Override
    public Corporation update(Corporation corporation, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Corporation currentCorporation = findByIdEagerly(corporation.getCorporationId());
            if(currentCorporation != null) {
                corporation.setProviders(currentCorporation.getProviders());
            }
        }
        return update(corporation);
    }

    @Override
    public List<Corporation> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Corporation> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_ALL_EAGERLY, Corporation.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Corporation findByIdEagerly(Long corporationId) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_ID_EAGERLY, Corporation.class);
        query.setParameter("corporationId", corporationId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode) {
        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_ADDRESS, Corporation.class);
        if(city == null) city = "";
        query.setParameter("city", "%" + city + "%");
        if(state == null) state = "";
        query.setParameter("state", "%" + state + "%");
        if(country == null) country = "";
        query.setParameter("country", "%" + country + "%");
        if(street == null) street = "";
        query.setParameter("street", "%" + street + "%");
        if(zipCode == null) zipCode = "";
        query.setParameter("zip_code", "%" + zipCode + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Corporation> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_NAME, Corporation.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Corporation> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_DESCRIPTION, Corporation.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findByHistory(String history) {
        return findByHistory(history, null, null);
    }

    @Override
    public List<Corporation> findByHistory(String history, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_HISTORY, Corporation.class);
        query.setParameter("history", "%" + history + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<Corporation> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_KEYWORD, Corporation.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findOpenAfter(Date date) {
        return findOpenAfter(date, null, null);
    }

    @Override
    public List<Corporation> findOpenAfter(Date date, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_OPEN_AFTER, Corporation.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findOpenBefore(Date date) {
        return findOpenBefore(date, null, null);
    }

    @Override
    public List<Corporation> findOpenBefore(Date date, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_OPEN_BEFORE, Corporation.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findOpenBetween(Date startDate, Date endDate) {
        return findOpenBetween(startDate, endDate, null, null);
    }

    @Override
    public List<Corporation> findOpenBetween(Date startDate, Date endDate, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_OPEN_BETWEEN, Corporation.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Corporation findForProvider(Provider provider) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_FOR_PROVIDER, Corporation.class);
        query.setParameter("provider", provider);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public Corporation findForProviderEagerly(Provider provider) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_FOR_PROVIDER_EAGERLY, Corporation.class);
        query.setParameter("provider", provider);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Corporation> findByMultipleCriteria(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address) {
        return findByMultipleCriteria(providers, name, description, history, startOpeningDate, endOpeningDate, address,  null, null);
    }

    @Override
    public List<Corporation> findByMultipleCriteria(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, name, description, history, startOpeningDate, endOpeningDate, address, false, start, limit);
    }

    @Override
    public List<Corporation> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address) {
        return findByMultipleCriteriaEagerly(providers, name, description, history, startOpeningDate, endOpeningDate, address, null, null);
    }

    @Override
    public List<Corporation> findByMultipleCriteriaEagerly(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, name, description, history, startOpeningDate, endOpeningDate, address, true, start, limit);
    }

    private List<Corporation> findByMultipleCriteria(List<Provider> providers, String name, String description, String history, Date startOpeningDate, Date endOpeningDate, Address address, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Corporation> criteriaQuery = criteriaBuilder.createQuery(Corporation.class);
        // FROM
        Root<Corporation> corporation = criteriaQuery.from(Corporation.class);
        // SELECT
        criteriaQuery.select(corporation).distinct(true);

        // INNER JOIN-s
        Join<Corporation, Provider> provider = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(providers != null && providers.size() > 0) {

            if(provider == null) provider = corporation.join(Corporation_.providers);

            predicates.add( provider.in(providers) );

            if(eagerly) {
                // then fetch associated collection of entities
                corporation.fetch("providers", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            corporation.fetch("providers", JoinType.LEFT);
        }

        if(name != null) {
            predicates.add( criteriaBuilder.like(corporation.get(Corporation_.corporationName), "%" + name + "%") );
        }

        if(description != null) {
            predicates.add( criteriaBuilder.like(corporation.get(Corporation_.description), "%" + description + "%") );
        }

        if(history != null) {
            predicates.add( criteriaBuilder.like(corporation.get(Corporation_.history), "%" + history + "%") );
        }

        if(startOpeningDate != null) {
            predicates.add( criteriaBuilder.greaterThanOrEqualTo(corporation.get(Corporation_.openingDate), startOpeningDate) );
        }

        if(endOpeningDate != null) {
            predicates.add( criteriaBuilder.lessThanOrEqualTo(corporation.get(Corporation_.openingDate), endOpeningDate) );
        }

        if(address != null) {

            Path<Address> addressPath = corporation.get(Corporation_.address);

            if(address.getCity() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.city), "%" + address.getCity() + "%") );

            if(address.getState() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.state), "%" + address.getState() + "%") );

            if(address.getCountry() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.country), "%" + address.getCountry() + "%") );

            if(address.getStreet() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.street), "%" + address.getStreet() + "%") );

            if(address.getZipCode() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.zipCode), "%" + address.getZipCode() + "%") );

            if(address.getFlatNumber() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.flatNumber), "%" + address.getFlatNumber() + "%") );

            if(address.getHouseNumber() != null)
                predicates.add( criteriaBuilder.like(addressPath.get(Address_.houseNumber), "%" + address.getHouseNumber() + "%") );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Corporation> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }
}
package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderServiceFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by michzio on 25/07/2015.
 */
@Stateless
@LocalBean
public class ProviderServiceFacade extends AbstractFacade<ProviderService> implements ProviderServiceFacadeInterface.Local, ProviderServiceFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProviderServiceFacade() {
        super(ProviderService.class);
    }


    @Override
    public List<ProviderService> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ProviderService> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER, ProviderService.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<ProviderService> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE, ProviderService.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory) {
        return findByServiceCategory(serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory) {
        return findByProviderAndServiceCategory(provider, serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ProviderService> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_DESCRIPTION, ProviderService.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description) {
        return findByProviderAndDescription(provider, description, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description) {
        return findByServiceAndDescription(service, description, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndDiscountedPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNTED_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount) {
        return findByServiceAndDiscount(service, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNT, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount) {
        return findByProviderAndDiscount(provider, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DISCOUNT, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation);
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_WORK_STATION, ProviderService.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_EMPLOYEE, ProviderService.class);
        query.setParameter("employee",employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee) {
        return findByProviderAndEmployee(provider, employee, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_EMPLOYEE, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer updateDiscountForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
    }

    @Override
    public Integer updateDiscountForProviderAndEmployee(Provider provider, Employee employee, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForOnlyWorkStation(WorkStation workStation) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_ONLY_WORK_STATION);
        query.setParameter("work_station", workStation);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProviderAndOnlyEmployee(Provider provider, Employee employee) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER_AND_ONLY_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProvider(Provider provider) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER);
        query.setParameter("provider", provider);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForService(Service service) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_SERVICE);
        query.setParameter("service", service);
        return query.executeUpdate();
    }

    @Override
    public List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                        String description, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                        Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees) {


        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProviderService> criteriaQuery = criteriaBuilder.createQuery(ProviderService.class);
        // FROM
        Root<ProviderService> providerService = criteriaQuery.from(ProviderService.class);
        // SELECT
        criteriaQuery.select(providerService);

        // INNER JOIN-s
        Join<ProviderService, Service> service = null;
        Join<ProviderService, Provider> provider = null;

        // WHERE'S PREDICATE
        List<Predicate> predicates = new ArrayList<>();

        if(providers != null && providers.size() > 0) {

            // inner joining
            if(provider == null) provider = providerService.join(ProviderService_.provider);
            predicates.add(provider.in(providers));
        }

        if(services != null && services.size() > 0) {

            // inner joining
            if(service == null) service = providerService.join(ProviderService_.service);
            predicates.add(service.in(services));

        }

        if(serviceCategories != null && serviceCategories.size() > 0) {

            // inner joining
            if(service == null) service = providerService.join(ProviderService_.service);
            predicates.add(service.get(Service_.serviceCategory).in(serviceCategories));
        }

        if(description != null) {

            // TODO maybe split description param into several keywords by blank spaces
            //      and make following predicate: %key1% AND %key2% AND ... AND %keyN%
            predicates.add(criteriaBuilder.like(providerService.get(ProviderService_.description), "%" + description + "%"));
        }

        if(minPrice != null) {
            if(includeDiscounts) {

                Expression<Number> discount = criteriaBuilder.quot(criteriaBuilder.diff(100.0, criteriaBuilder.toDouble(providerService.get(ProviderService_.discount))), 100.0);
                Expression<Number> discountedPrice = criteriaBuilder.prod(providerService.get(ProviderService_.price), discount);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(criteriaBuilder.toDouble(discountedPrice), minPrice));

            } else {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(
                        providerService.get(ProviderService_.price), minPrice) );
            }
        }

        if(maxPrice != null) {
            if(includeDiscounts) {

                Expression<Number> discount = criteriaBuilder.quot(criteriaBuilder.diff(100.0, criteriaBuilder.toDouble(providerService.get(ProviderService_.discount))), 100.0);
                Expression<Number> discountedPrice = criteriaBuilder.prod(providerService.get(ProviderService_.price), discount);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(criteriaBuilder.toDouble(discountedPrice), maxPrice));

            } else {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(
                        providerService.get(ProviderService_.price), maxPrice) );
            }
        }

        if(minDiscount != null) {

            predicates.add(criteriaBuilder.greaterThanOrEqualTo( providerService.get(ProviderService_.discount), minDiscount));
        }

        if(maxDiscount != null) {

            predicates.add(criteriaBuilder.lessThanOrEqualTo( providerService.get(ProviderService_.discount), maxDiscount));
        }

        if(workStations != null) {

           List<Predicate> orPredicates = new ArrayList<>();
           for(WorkStation workStation : workStations) {
                orPredicates.add( criteriaBuilder.isMember(workStation, providerService.get(ProviderService_.workStations)) );
           }

          predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        }

        if(employees != null) {

            List<Predicate> orPredicates = new ArrayList<>();
            for(Employee employee : employees) {
                orPredicates.add( criteriaBuilder.isMember(employee, providerService.get(ProviderService_.supplyingEmployees)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<ProviderService> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    /**
     * Criteria API multi-criteria predicates in WHERE clause
     * PATTERN 1
     *
     * List<Predicate> predicates = new ArrayList<Predicate>();
     * for (Key key : keys) {
     *      predicates.add(criteriaBuilder.equal(root.get(key), value));
     * }
     * c.where(criteriaBuilder.and(predicates.toArray(new Predicate[] {})));
     *
     * PATTTERN 2
     *
     * Predicate predicate = criteriaBuilder.conjunction();
     * for (Key key : keys) {
     *      Predicate newPredicate = criteriaBuilder.equal(root.get(key), value);
     *      predicate = criteriaBuilder.and(predicate, newPredicate);
     * }
     * c.where(predicate);
     */
}
package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderServiceFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;
import pl.salonea.entities.idclass.WorkStationId;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
import javax.persistence.criteria.*;
import java.util.ArrayList;
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
    public ProviderService createForProviderAndService(Long providerId, Integer serviceId, ProviderService providerService) {

        Provider foundProvider = getEntityManager().find(Provider.class, providerId);
        Service foundService = getEntityManager().find(Service.class, serviceId);
        providerService.setProvider(foundProvider);
        providerService.setService(foundService);
        getEntityManager().persist(providerService);
        return providerService;
    }

    @Override
    public ProviderService update(ProviderServiceId providerServiceId, ProviderService providerService) {

        return update(providerServiceId, providerService, true);
    }

    @Override
    public ProviderService update(ProviderServiceId providerServiceId, ProviderService providerService, Boolean retainTransientFields) {

        Provider foundProvider = getEntityManager().find(Provider.class, providerServiceId.getProvider());
        Service foundService = getEntityManager().find(Service.class, providerServiceId.getService());
        providerService.setProvider(foundProvider);
        providerService.setService(foundService);

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            ProviderService currentProviderService = findByIdEagerly(providerServiceId);
            if(currentProviderService != null) {
                providerService.setSupplyingEmployees(currentProviderService.getSupplyingEmployees());
                providerService.setWorkStations(currentProviderService.getWorkStations());
            }
        }
        return update(providerService);
    }

    @Override
    public List<ProviderService> find(List<Object> providerServiceIds) {

        if(providerServiceIds == null || providerServiceIds.size() == 0)
            throw new IllegalArgumentException("The providerServiceIds argument must be not empty list.");

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProviderService> criteriaQuery = criteriaBuilder.createQuery(ProviderService.class);
        // FROM
        Root<ProviderService> providerService = criteriaQuery.from(ProviderService.class);
        // SELECT
        criteriaQuery.select(providerService);

        // INNER JOIN
        Join<ProviderService, Provider> provider = providerService.join(ProviderService_.provider);
        Join<ProviderService, Service> service = providerService.join(ProviderService_.service);

        // WHERE PREDICATES on composite primary key
        List<Predicate> orPredicates = new ArrayList<>();

        for(Object object : providerServiceIds) {

            if( !(object instanceof ProviderServiceId) )
                throw new IllegalArgumentException("The providerServiceIds argument should be list of ProviderServiceId typed objects.");

            ProviderServiceId providerServiceId = (ProviderServiceId) object;

            Predicate[] andPredicates = new Predicate[2];
            andPredicates[0] = criteriaBuilder.equal( provider.get(Provider_.userId), providerServiceId.getProvider() );
            andPredicates[1] = criteriaBuilder.equal( service.get(Service_.serviceId), providerServiceId.getService() );

            orPredicates.add( criteriaBuilder.and(andPredicates) );
         }

        // WHERE compositePK1 OR compositePK2 OR ... OR compositePKN
        criteriaQuery.where( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        TypedQuery<ProviderService> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<ProviderService> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_ALL_EAGERLY, ProviderService.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public ProviderService findByIdEagerly(ProviderServiceId providerServiceId) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_ID_EAGERLY, ProviderService.class);
        query.setParameter("userId", providerServiceId.getProvider());
        query.setParameter("serviceId", providerServiceId.getService());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<ProviderService> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ProviderService> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER, ProviderService.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<ProviderService> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_EAGERLY, ProviderService.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<ProviderService> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE, ProviderService.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<ProviderService> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_EAGERLY, ProviderService.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory) {
        return findByServiceCategory(serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceCategoryEagerly(ServiceCategory serviceCategory) {
        return findByServiceCategoryEagerly(serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByServiceCategoryEagerly(ServiceCategory serviceCategory, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_CATEGORY_EAGERLY, ProviderService.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory) {
        return findByProviderAndServiceCategory(provider, serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ProviderService> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_DESCRIPTION, ProviderService.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description) {
        return findByProviderAndDescription(provider, description, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description) {
        return findByServiceAndDescription(service, description, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndDiscountedPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNTED_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount) {
        return findByServiceAndDiscount(service, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNT, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount) {
        return findByProviderAndDiscount(provider, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DISCOUNT, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation);
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_WORK_STATION, ProviderService.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByWorkStationEagerly(WorkStation workStation) {
        return findByWorkStationEagerly(workStation, null, null);
    }

    @Override
    public List<ProviderService> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_WORK_STATION_EAGERLY, ProviderService.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_EMPLOYEE, ProviderService.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<ProviderService> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_EMPLOYEE_EAGERLY, ProviderService.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee) {
        return findByProviderAndEmployee(provider, employee, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer limit) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_EMPLOYEE, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer updateDiscountForProvider(Provider provider, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER);
        query.setParameter("provider", provider);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
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
    public Integer updateDiscountForProviderAndServiceCategoryAndEmployee(Provider provider, ServiceCategory serviceCategory, Employee employee, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("employee", employee);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(ProviderServiceId providerServiceId) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_BY_ID);
        query.setParameter("userId", providerServiceId.getProvider());
        query.setParameter("serviceId", providerServiceId.getService());
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
    public Integer deleteForProviderAndServiceCategoryAndOnlyEmployee(Provider provider, ServiceCategory serviceCategory, Employee employee) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY_AND_ONLY_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("employee", employee);
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
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderService.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderService.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByServiceCategory(ServiceCategory serviceCategory) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderService.COUNT_BY_SERVICE_CATEGORY, Long.class);
        query.setParameter("service_category", serviceCategory);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderService.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ProviderService.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }


    @Override
    public List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                        List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                        Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees) {

        return findByMultipleCriteria(providers, services, serviceCategories, descriptions, minPrice, maxPrice, includeDiscounts,
                minDiscount, maxDiscount, workStations, employees, null, null);
    }

    @Override
    public List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                        List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                        Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees,
                                                        Integer start, Integer limit) {

        return findByMultipleCriteria(providers, services, serviceCategories, descriptions, minPrice, maxPrice, includeDiscounts,
                minDiscount, maxDiscount, workStations, employees, false, start, limit);
    }

    @Override
    public List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                               List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                               Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees) {

        return findByMultipleCriteriaEagerly(providers, services, serviceCategories, descriptions, minPrice, maxPrice, includeDiscounts,
                minDiscount, maxDiscount, workStations, employees, null, null);
    }

    @Override
    public List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                               List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                               Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees,
                                                               Integer start, Integer limit) {

        return findByMultipleCriteria(providers, services, serviceCategories, descriptions, minPrice, maxPrice, includeDiscounts,
                minDiscount, maxDiscount, workStations, employees, true, start, limit);
    }

    private List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                        List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                        Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees,
                                                        Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ProviderService> criteriaQuery = criteriaBuilder.createQuery(ProviderService.class);
        // FROM
        Root<ProviderService> providerService = criteriaQuery.from(ProviderService.class);
        // SELECT
        criteriaQuery.select(providerService).distinct(true);

        // INNER JOIN-s
        Join<ProviderService, Service> service = null;
        Join<ProviderService, Provider> provider = null;

        // WHERE PREDICATES
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

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                orDescriptionPredicates.add( criteriaBuilder.like(providerService.get(ProviderService_.description), "%" + description + "%") );
            }

            predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
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

            predicates.add(criteriaBuilder.greaterThanOrEqualTo(providerService.get(ProviderService_.discount), minDiscount));
        }

        if(maxDiscount != null) {

            predicates.add(criteriaBuilder.lessThanOrEqualTo(providerService.get(ProviderService_.discount), maxDiscount));
        }

        if(workStations != null) {

            List<Predicate> orPredicates = new ArrayList<>();
            for(WorkStation workStation : workStations) {

                // it's needed in order to not have detached entity issue
                workStation = getEntityManager().find(WorkStation.class, new WorkStationId( workStation.getServicePoint().getProvider().getUserId(),
                                                                                            workStation.getServicePoint().getServicePointNumber(),
                                                                                            workStation.getWorkStationNumber()) );

                orPredicates.add( criteriaBuilder.isMember(workStation, providerService.get(ProviderService_.workStations)) );
            }

            predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})));

            if(eagerly) {
                // then fetch associated collection of entities
                providerService.fetch("workStations", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            providerService.fetch("workStations", JoinType.LEFT);
        }

        if(employees != null) {

            List<Predicate> orPredicates = new ArrayList<>();
            for(Employee employee : employees) {
                orPredicates.add( criteriaBuilder.isMember(employee, providerService.get(ProviderService_.supplyingEmployees)) );
            }

            predicates.add(criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})));

            if(eagerly) {
                // then fetch associated collection of entities
                providerService.fetch("supplyingEmployees", JoinType.INNER);
            }
        } else if(eagerly) {
            // then left fetch associated collection of entities
            providerService.fetch("supplyingEmployees", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<ProviderService> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
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
package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServiceFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 28/07/2015.
 */
@Stateless
@LocalBean
public class ServiceFacade extends AbstractFacade<Service> implements ServiceFacadeInterface.Local, ServiceFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ServiceFacade() {
        super(Service.class);
    }

    @Override
    public List<Service> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Service> findByName(String name, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_NAME, Service.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Service> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_DESCRIPTION, Service.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> searchByKeyword(String keyword) {
        return searchByKeyword(keyword, null, null);
    }

    @Override
    public List<Service> searchByKeyword(String keyword, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.SEARCH_BY_KEYWORD, Service.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategory(ServiceCategory serviceCategory) {
        return findByCategory(serviceCategory, null, null);
    }

    @Override
    public List<Service> findByCategory(ServiceCategory serviceCategory, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY, Service.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword) {
        return findByCategoryAndKeyword(serviceCategory, keyword, null, null);
    }

    @Override
    public List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY_AND_KEYWORD, Service.class);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Service> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_PROVIDER, Service.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Service> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_EMPLOYEE, Service.class);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Service> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_WORK_STATION, Service.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Service> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_SERVICE_POINT, Service.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByName(String name) {

        Query query = getEntityManager().createNamedQuery(Service.DELETE_BY_NAME);
        query.setParameter("name", name);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteByCategory(ServiceCategory serviceCategory) {

        Query query = getEntityManager().createNamedQuery(Service.DELETE_BY_CATEGORY);
        query.setParameter("service_category", serviceCategory);
        return query.executeUpdate();
    }

    @Override
    public List<Service> findByMultipleCriteria(String name, String description, String keyword, List<ServiceCategory> serviceCategories,
                         List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Service> criteriaQuery = criteriaBuilder.createQuery(Service.class);
        // FROM
        Root<Service> service = criteriaQuery.from(Service.class);
        // SELECT
        criteriaQuery.select(service);

        // INNER JOIN-s
        Join<Service, ProviderService> providerService = null;
        Join<ProviderService, Provider> provider = null;
        Join<ProviderService, WorkStation> workStationJoin = null;
        Join<WorkStation, ServicePoint> servicePoint = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(name != null) {
            predicates.add(criteriaBuilder.like(service.get(Service_.serviceName), "%" + name + "%"));
        }

        if(description != null) {

            // TODO maybe split description param into several keywords by blank spaces
            //      and make following predicate: %key1% AND %key2% AND ... AND %keyN%
            predicates.add(criteriaBuilder.like(service.get(Service_.description), "%" + description + "%"));
        }

        // keyword is searched either in name or description
        if(keyword != null) {
            Predicate namePredicate = criteriaBuilder.like(service.get(Service_.serviceName), "%" + keyword + "%");
            Predicate descriptionPredicate = criteriaBuilder.like(service.get(Service_.description), "%" + keyword + "%");

            predicates.add(criteriaBuilder.or(namePredicate, descriptionPredicate));
        }

        if(serviceCategories != null && serviceCategories.size() > 0) {

            predicates.add(service.get(Service_.serviceCategory).in(serviceCategories));
        }

        if(providers != null && providers.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(provider == null) provider = providerService.join(ProviderService_.provider);
            predicates.add(provider.in(providers));
        }

        if(employees != null && employees.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);

            List<Predicate> orPredicates = new ArrayList<>();

            for(Employee employee : employees) {
                orPredicates.add( criteriaBuilder.isMember(employee, providerService.get(ProviderService_.supplyingEmployees)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        }

        if(workStations != null && workStations.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);

            List<Predicate> orPredicates = new ArrayList<>();

            for(WorkStation workStation : workStations) {
                orPredicates.add( criteriaBuilder.isMember(workStation, providerService.get(ProviderService_.workStations)) );
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );
        }

        if(servicePoints != null && servicePoints.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(workStationJoin == null) workStationJoin = providerService.join(ProviderService_.workStations);
            if(servicePoint == null) servicePoint = workStationJoin.join(WorkStation_.servicePoint);

            predicates.add(servicePoint.in(servicePoints));

        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Service> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }
}
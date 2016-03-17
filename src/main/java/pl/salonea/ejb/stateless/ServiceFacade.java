package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServiceFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
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
    public Service update(Service service, Boolean retainTransientFields) {

        if(retainTransientFields) {
            // keep current collection attributes of resource (and other marked @XmlTransient)
            Service currentService = findByIdEagerly(service.getServiceId());
            if(currentService != null) {
                service.setProvidedServiceOffers(currentService.getProvidedServiceOffers());
            }
        }
        return update(service);
    }

    @Override
    public List<Service> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<Service> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_ALL_EAGERLY, Service.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Service findByIdEagerly(Integer serviceId) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_ID_EAGERLY, Service.class);
        query.setParameter("serviceId", serviceId);
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
    }

    @Override
    public List<Service> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Service> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_NAME, Service.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<Service> findByDescription(String description, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_DESCRIPTION, Service.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByKeyword(String keyword) {
        return findByKeyword(keyword, null, null);
    }

    @Override
    public List<Service> findByKeyword(String keyword, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_KEYWORD, Service.class);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategory(ServiceCategory serviceCategory) {
        return findByCategory(serviceCategory, null, null);
    }

    @Override
    public List<Service> findByCategory(ServiceCategory serviceCategory, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY, Service.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategoryEagerly(ServiceCategory serviceCategory) {
        return findByCategoryEagerly(serviceCategory, null, null);
    }

    @Override
    public List<Service> findByCategoryEagerly(ServiceCategory serviceCategory, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY_EAGERLY, Service.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategoryAndName(ServiceCategory serviceCategory, String name) {
        return findByCategoryAndName(serviceCategory, name, null, null);
    }

    @Override
    public List<Service> findByCategoryAndName(ServiceCategory serviceCategory, String name, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY_AND_NAME, Service.class);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategoryAndDescription(ServiceCategory serviceCategory, String description) {
        return findByCategoryAndDescription(serviceCategory, description, null, null);
    }

    @Override
    public List<Service> findByCategoryAndDescription(ServiceCategory serviceCategory, String description, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY_AND_DESCRIPTION, Service.class);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("description", "%" + description + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword) {
        return findByCategoryAndKeyword(serviceCategory, keyword, null, null);
    }

    @Override
    public List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_CATEGORY_AND_KEYWORD, Service.class);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("keyword", "%" + keyword + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<Service> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_PROVIDER, Service.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<Service> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_PROVIDER_EAGERLY, Service.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Service> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_EMPLOYEE, Service.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<Service> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_EMPLOYEE_EAGERLY, Service.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Service> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_WORK_STATION, Service.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByWorkStationEagerly(WorkStation workStation) {
        return findByWorkStationEagerly(workStation, null, null);
    }

    @Override
    public List<Service> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_WORK_STATION_EAGERLY, Service.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Service> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_SERVICE_POINT, Service.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByServicePointEagerly(ServicePoint servicePoint) {
        return findByServicePointEagerly(servicePoint, null, null);
    }

    @Override
    public List<Service> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_SERVICE_POINT_EAGERLY, Service.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByEmployeeTerm(EmployeeTerm employeeTerm) {
        return findByEmployeeTerm(employeeTerm, null, null);
    }

    @Override
    public List<Service> findByEmployeeTerm(EmployeeTerm employeeTerm, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_EMPLOYEE_TERM, Service.class);
        query.setParameter("employee_term", employeeTerm);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByEmployeeTermEagerly(EmployeeTerm employeeTerm) {
        return findByEmployeeTermEagerly(employeeTerm, null, null);
    }

    @Override
    public List<Service> findByEmployeeTermEagerly(EmployeeTerm employeeTerm, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_EMPLOYEE_TERM_EAGERLY, Service.class);
        query.setParameter("employee_term", employeeTerm);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<Service> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_TERM, Service.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Service> findByTermEagerly(Term term) {
        return findByTermEagerly(term, null, null);
    }

    @Override
    public List<Service> findByTermEagerly(Term term, Integer start, Integer limit) {

        TypedQuery<Service> query = getEntityManager().createNamedQuery(Service.FIND_BY_TERM_EAGERLY, Service.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByCategory(ServiceCategory serviceCategory) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_CATEGORY, Long.class);
        query.setParameter("service_category", serviceCategory);
        return query.getSingleResult();
    }

    @Override
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployeeTerm(EmployeeTerm employeeTerm) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_EMPLOYEE_TERM, Long.class);
        query.setParameter("employee_term", employeeTerm);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(Service.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
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
    public List<Service> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms) {
        return findByMultipleCriteria(names, descriptions, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, null, null);
    }

    @Override
    public List<Service> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms,
                                                Integer start, Integer limit) {
        return findByMultipleCriteria(false, names, false, descriptions, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, false, start, limit);
    }

    @Override
    public List<Service> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms) {
        return findByMultipleCriteria(keywords, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, null, null);
    }

    @Override
    public List<Service> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms,
                                                Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, false, start, limit);
    }

    @Override
    public List<Service> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                       List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                       List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms) {
        return findByMultipleCriteriaEagerly(names, descriptions, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, null, null);
    }

    @Override
    public List<Service> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                       List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                       List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms, Integer start, Integer limit) {
        return findByMultipleCriteria(false, names, false, descriptions, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, true, start, limit);
    }

    @Override
    public List<Service> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                       List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                       List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms) {
        return findByMultipleCriteriaEagerly(keywords, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, null, null);
    }

    @Override
    public List<Service> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                       List<Provider> providers, List<Employee> employees, List<WorkStation> workStations,
                                                       List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms, Integer start, Integer limit) {
        return findByMultipleCriteria(true, keywords, true, keywords, serviceCategories, providers, employees, workStations, servicePoints, employeeTerms, terms, true, start, limit);
    }

    private List<Service> findByMultipleCriteria(Boolean orWithNames, List<String> names,
                                                 Boolean orWithDescriptions, List<String> descriptions,
                                                 List<ServiceCategory> serviceCategories, List<Provider> providers,
                                                 List<Employee> employees, List<WorkStation> workStations,
                                                 List<ServicePoint> servicePoints, List<EmployeeTerm> employeeTerms, List<Term> terms,
                                                 Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Service> criteriaQuery = criteriaBuilder.createQuery(Service.class);
        // FROM
        Root<Service> service = criteriaQuery.from(Service.class);
        // SELECT
        criteriaQuery.select(service).distinct(true);

        // INNER JOIN-s
        Join<Service, ProviderService> providerService = null;
        Join<ProviderService, Provider> provider = null;
        Join<ProviderService, WorkStation> workStation = null;
        Join<WorkStation, ServicePoint> servicePoint = null;

        Join<ProviderService, Employee> employee = null;
        Join<Employee, EmployeeTerm> employeeTerm = null;
        Join<WorkStation, EmployeeTerm> workStationEmployeeTerm = null;

        Join<EmployeeTerm, Term> term = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();
        List<Predicate> orPredicates = new ArrayList<>();

        if(names != null && names.size() > 0) {

            List<Predicate> orNamePredicates = new ArrayList<>();

            for(String name : names) {
                orNamePredicates.add( criteriaBuilder.like(service.get(Service_.serviceName), "%" + name + "%") );
            }

            if(orWithNames) {
                orPredicates.add( criteriaBuilder.or(orNamePredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orNamePredicates.toArray(new Predicate[] {})) );
            }
        }

        if(descriptions != null && descriptions.size() > 0) {

            List<Predicate> orDescriptionPredicates = new ArrayList<>();

            for(String description : descriptions) {
                orDescriptionPredicates.add( criteriaBuilder.like(service.get(Service_.description), "%" + description + "%") );
            }

            if(orWithDescriptions) {
                orPredicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            } else {
                predicates.add( criteriaBuilder.or(orDescriptionPredicates.toArray(new Predicate[] {})) );
            }
        }

        if(orPredicates.size() > 0)
            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})) );


        if(serviceCategories != null && serviceCategories.size() > 0) {

            predicates.add( service.get(Service_.serviceCategory).in(serviceCategories) );
        }

        if(providers != null && providers.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(provider == null) provider = providerService.join(ProviderService_.provider);
            predicates.add(provider.in(providers));
        }

        if(employees != null && employees.size() > 0) {

            /**
             *  DEPRECATED APPROACH - WITHOUT EXPLICIT JOINING
             *
             *   if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
             *
             *   List<Predicate> orEmployeePredicates = new ArrayList<>();
             *
             *   for(Employee employee : employees) {
             *       orEmployeePredicates.add( criteriaBuilder.isMember(employee, providerService.get(ProviderService_.supplyingEmployees)) );
             *   }
             *
             *   predicates.add( criteriaBuilder.or(orEmployeePredicates.toArray(new Predicate[]{})) );
             */

            // inner joining
            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(employee == null) employee = providerService.join(ProviderService_.supplyingEmployees);

            predicates.add(employee.in(employees));
        }

        if(workStations != null && workStations.size() > 0) {

            /**
             * DEPRECATED APPROACH - WITHOUT EXPLICIT JOINING
             *
             *  if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
             *
             *   List<Predicate> orWorkStationPredicates = new ArrayList<>();
             *
             * for(WorkStation workStation : workStations) {
             *       orWorkStationPredicates.add( criteriaBuilder.isMember(workStation, providerService.get(ProviderService_.workStations)) );
             *   }
             *
             *   predicates.add( criteriaBuilder.or(orWorkStationPredicates.toArray(new Predicate[]{})) );
             */

            // inner joining
            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(workStation == null) workStation = providerService.join(ProviderService_.workStations);

            predicates.add(workStation.in(workStations));
        }

        if(servicePoints != null && servicePoints.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(workStation == null) workStation = providerService.join(ProviderService_.workStations);
            if(servicePoint == null) servicePoint = workStation.join(WorkStation_.servicePoint);

            predicates.add(servicePoint.in(servicePoints));
        }

        if(employeeTerms != null && employeeTerms.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(workStation == null) workStation = providerService.join(ProviderService_.workStations);
            if(workStationEmployeeTerm == null) workStationEmployeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);

            if(employee == null) employee = providerService.join(ProviderService_.supplyingEmployees);
            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);

            predicates.add( criteriaBuilder.and( criteriaBuilder.equal(employeeTerm, workStationEmployeeTerm),
                                                 employeeTerm.in(employeeTerms) ) );
        }

        if(terms != null && terms.size() > 0) {

            if(providerService == null) providerService = service.join(Service_.providedServiceOffers);
            if(workStation == null) workStation = providerService.join(ProviderService_.workStations);
            if(workStationEmployeeTerm == null) workStationEmployeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);

            if(employee == null) employee = providerService.join(ProviderService_.supplyingEmployees);
            if(employeeTerm == null) employeeTerm = employee.join(Employee_.termsOnWorkStation);

            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            predicates.add( criteriaBuilder.and( criteriaBuilder.equal(employeeTerm, workStationEmployeeTerm),
                                                 term.in(terms) ) );
        }

        // if searching services simultaneously by work station/service point and employee
        // check to see whether given employee works in given work station/ service point
        if(workStation != null && employee != null) {

            predicates.add(criteriaBuilder.equal(
                    workStation.join(WorkStation_.termsEmployeesWorkOn).get(EmployeeTerm_.employee), employee));
        }

        if(eagerly) {
            if(providerService != null) {
                // then fetch associated collection of entities
                service.fetch("providedServiceOffers", JoinType.INNER);
            } else {
                // then left fetch associated collection of entities
                service.fetch("providedServiceOffers", JoinType.LEFT);
            }
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<Service> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}

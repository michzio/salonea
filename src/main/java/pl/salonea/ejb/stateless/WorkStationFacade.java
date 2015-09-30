package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.WorkStationFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.WorkStationId;
import pl.salonea.enums.WorkStationType;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 09/08/2015.
 */
@Stateless
@LocalBean
public class WorkStationFacade extends AbstractFacade<WorkStation> implements WorkStationFacadeInterface.Local, WorkStationFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public WorkStationFacade() {
        super(WorkStation.class);
    }

    @Override
    public List<WorkStation> find(List<Object> workStationIds) {

        if(workStationIds == null || workStationIds.size() == 0)
            throw new IllegalArgumentException("The workStationIds argument must be not empty list.");

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WorkStation> criteriaQuery = criteriaBuilder.createQuery(WorkStation.class);
        // FROM
        Root<WorkStation> workStation = criteriaQuery.from(WorkStation.class);
        // SELECT
        criteriaQuery.select(workStation);

        // INNER JOIN
        Join<WorkStation, ServicePoint> servicePoint = workStation.join(WorkStation_.servicePoint);
        Join<ServicePoint, Provider> provider = servicePoint.join(ServicePoint_.provider);

        // WHERE PREDICATES on composite primary key
        List<Predicate> orPredicates = new ArrayList<>();

        for( Object object : workStationIds ) {

            if( !(object instanceof WorkStationId) )
                throw new IllegalArgumentException("The workStationIds argument should be list of WorkStationId typed objects.");

            WorkStationId workStationId = (WorkStationId) object;

            Predicate[] andPredicates = new Predicate[3];
            andPredicates[0] = criteriaBuilder.equal( provider.get(Provider_.userId), workStationId.getServicePoint().getProvider() );
            andPredicates[1] = criteriaBuilder.equal( servicePoint.get(ServicePoint_.servicePointNumber), workStationId.getServicePoint().getServicePointNumber() );
            andPredicates[2] = criteriaBuilder.equal( workStation.get(WorkStation_.workStationNumber), workStationId.getWorkStationNumber() );

            orPredicates.add( criteriaBuilder.and(andPredicates) );

        }

        // WHERE compositePK1 OR compositePK2 OR ... OR compositePK3
        criteriaQuery.where( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        TypedQuery<WorkStation> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByType(WorkStationType type) {
        return findByType(type, null, null);
    }

    @Override
    public List<WorkStation> findByType(WorkStationType type, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TYPE, WorkStation.class);
        query.setParameter("work_station_type", type);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type) {
        return findByServicePointAndType(servicePoint, type, null, null);
    }

    @Override
    public List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_POINT_AND_TYPE, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("work_station_type", type);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<WorkStation> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE, WorkStation.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime) {
        return findByServiceAndTerm(service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime) {
        return findByServiceAndTermStrict(service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint) {
        return findByServiceAndServicePoint(service, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServiceAndServicePointAndTerm(service, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByServiceAndServicePointAndTermStrict(service, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("service", service);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<WorkStation> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE, WorkStation.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime) {
        return findByProviderServiceAndTerm(providerService, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime) {
        return findByProviderServiceAndTermStrict(providerService, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint) {
        return findByProviderServiceAndServicePoint(providerService, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByProviderServiceAndServicePointAndTerm(providerService, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByProviderServiceAndServicePointAndTermStrict(providerService, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_PROVIDER_SERVICE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<WorkStation> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE, WorkStation.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime) {
        return findByEmployeeAndTerm(employee, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime) {
        return findByEmployeeAndTermStrict(employee, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint) {
        return findByEmployeeAndServicePoint(employee, servicePoint, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint, Integer start, Integer limit) {
        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByEmployeeAndServicePointAndTerm(employee, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByEmployeeAndServicePointAndTermStrict(employee, servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_POINT_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndService(Employee employee, Service service) {
        return findByEmployeeAndService(employee, service, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndService(Employee employee, Service service, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime) {
        return findByEmployeeAndServiceAndTerm(employee, service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime) {
        return findByEmployeeAndServiceAndTermStrict(employee, service, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_EMPLOYEE_AND_SERVICE_AND_TERM_STRICT, WorkStation.class);
        query.setParameter("employee", employee);
        query.setParameter("service", service);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTerm(Date startTime, Date endTime) {
        return findByTerm(startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTerm(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM, WorkStation.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTermStrict(Date startTime, Date endTime) {
        return findByTermStrict(startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTermStrict(Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM_STRICT, WorkStation.class);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTermAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByTermAndServicePoint(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTermAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByTermStrictAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime) {
        return findByTermStrictAndServicePoint(servicePoint, startTime, endTime, null, null);
    }

    @Override
    public List<WorkStation> findByTermStrictAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit) {

        TypedQuery<WorkStation> query = getEntityManager().createNamedQuery(WorkStation.FIND_BY_TERM_STRICT_AND_SERVICE_POINT, WorkStation.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("start_time", startTime);
        query.setParameter("end_time", endTime);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, WorkStationType type, Period period, Boolean strictTerm) {
        return findByMultipleCriteria(servicePoints, services, providerServices, employees, type, period, strictTerm,  null, null);
    }

    @Override
    public List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, WorkStationType type, Period period, Boolean strictTerm, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<WorkStation> criteriaQuery = criteriaBuilder.createQuery(WorkStation.class);
        // FROM
        Root<WorkStation> workStation = criteriaQuery.from(WorkStation.class);
        // SELECT
        criteriaQuery.select(workStation).distinct(true);

        // INNER JOIN-s
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<WorkStation, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;
        Join<WorkStation, TermEmployeeWorkOn> employeeTerm = null;
        Join<TermEmployeeWorkOn, Term> term = null;
        Join<TermEmployeeWorkOn, Employee> employee = null;
        // Join<Employee, ProviderService> employeeProviderService = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(servicePoints != null && servicePoints.size() > 0) {

            if(servicePoint == null) servicePoint = workStation.join(WorkStation_.servicePoint);

            predicates.add( servicePoint.in(servicePoints) );
        }

        if(services != null && services.size() > 0) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);

            predicates.add( providerService.in(providerServices) );
        }

        if(employees != null && employees.size() > 0) {

            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(employee == null) employee = employeeTerm.join(TermEmployeeWorkOn_.employee);

            predicates.add( employee.in(employees) );
        }

        if(type != null) {
            predicates.add( criteriaBuilder.equal(workStation.get(WorkStation_.workStationType), type) );
        }

        if(period != null) {

            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(term == null) term = employeeTerm.join(TermEmployeeWorkOn_.term);

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime()) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime()) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime()) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime()) );
            }
        }

        // take into account that employee must supply given provider services when searching by (term or employee) and services
        // if( (employees != null && services != null) || (employees != null && providerServices != null)
        //        || (services != null && period != null) || (providerServices != null && period != null)) {
        if( (employees != null || period != null) && (providerServices != null || services != null) ) {

            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(employeeTerm == null) employeeTerm = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(employee == null) employee = employeeTerm.join(TermEmployeeWorkOn_.employee);
            // if(employeeProviderService == null) employeeProviderService = employee.join(Employee_.suppliedServices);

            predicates.add( criteriaBuilder.isMember(providerService, (employee.get(Employee_.suppliedServices)) ) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<WorkStation> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByServicePoint(ServicePoint servicePoint) {

        Query query = getEntityManager().createNamedQuery(WorkStation.DELETE_BY_SERVICE_POINT);
        query.setParameter("service_point", servicePoint);
        return query.executeUpdate();
    }
}

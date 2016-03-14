package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.EmployeeTermFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.EmployeeTermId;
import pl.salonea.utils.Period;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 17/08/2015.
 */
@Stateless
@LocalBean
public class EmployeeTermFacade extends AbstractFacade<EmployeeTerm>
            implements EmployeeTermFacadeInterface.Local, EmployeeTermFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public EmployeeTermFacade() {
        super(EmployeeTerm.class);
    }

    @Override
    public EmployeeTerm createForEmployeeAndTerm(Long employeeId, Long termId, EmployeeTerm employeeTerm) {

        Employee foundEmployee = getEntityManager().find(Employee.class, employeeId);
        Term foundTerm = getEntityManager().find(Term.class, termId);
        employeeTerm.setEmployee(foundEmployee);
        employeeTerm.setTerm(foundTerm);
        getEntityManager().persist(employeeTerm);
        return employeeTerm;
    }

    @Override
    public EmployeeTerm update(EmployeeTermId employeeTermId, EmployeeTerm employeeTerm) {

        Employee foundEmployee = getEntityManager().find(Employee.class, employeeTermId.getEmployee());
        Term foundTerm = getEntityManager().find(Term.class, employeeTermId.getTerm());
        employeeTerm.setEmployee(foundEmployee);
        employeeTerm.setTerm(foundTerm);

        return update(employeeTerm);
    }

    @Override
    public List<EmployeeTerm> findByPeriod(Date startTime, Date endTime) {
        return findByPeriod(new Period(startTime, endTime));
    }

    @Override
    public List<EmployeeTerm> findByPeriod(Date startTime, Date endTime, Integer start, Integer limit) {
        return findByPeriod(new Period(startTime, endTime), start, limit);
    }

    @Override
    public List<EmployeeTerm> findByPeriod(Period period) {
        return findByPeriod(period, null, null);
    }

    @Override
    public List<EmployeeTerm> findByPeriod(Period period, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_PERIOD, EmployeeTerm.class);
        query.setParameter("start_time", period.getStartTime());
        query.setParameter("end_time", period.getEndTime());
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByPeriodStrict(Date startTime, Date endTime) {
        return findByPeriodStrict(new Period(startTime, endTime));
    }

    @Override
    public List<EmployeeTerm> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer limit) {
        return findByPeriodStrict(new Period(startTime, endTime), start, limit);
    }

    @Override
    public List<EmployeeTerm> findByPeriodStrict(Period period) {
        return findByPeriodStrict(period, null, null);
    }

    @Override
    public List<EmployeeTerm> findByPeriodStrict(Period period, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_PERIOD_STRICT, EmployeeTerm.class);
        query.setParameter("start_time", period.getStartTime());
        query.setParameter("end_time", period.getEndTime());
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findAfter(Date time) {
        return findAfter(time, null, null);
    }

    @Override
    public List<EmployeeTerm> findAfter(Date time, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_AFTER, EmployeeTerm.class);
        query.setParameter("time", time);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findAfterStrict(Date time) {
        return findAfterStrict(time, null, null);
    }

    @Override
    public List<EmployeeTerm> findAfterStrict(Date time, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_AFTER_STRICT, EmployeeTerm.class);
        query.setParameter("time", time);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findBefore(Date time) {
        return findBefore(time, null, null);
    }

    @Override
    public List<EmployeeTerm> findBefore(Date time, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BEFORE, EmployeeTerm.class);
        query.setParameter("time", time);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findBeforeStrict(Date time) {
        return findBeforeStrict(time, null, null);
    }

    @Override
    public List<EmployeeTerm> findBeforeStrict(Date time, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BEFORE_STRICT, EmployeeTerm.class);
        query.setParameter("time", time);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<EmployeeTerm> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_EMPLOYEE, EmployeeTerm.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByTerm(Term term) {
        return findByTerm(term, null, null);
    }

    @Override
    public List<EmployeeTerm> findByTerm(Term term, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_TERM, EmployeeTerm.class);
        query.setParameter("term", term);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<EmployeeTerm> findByWorkStation(WorkStation workStation, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_WORK_STATION, EmployeeTerm.class);
        query.setParameter("work_station", workStation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<EmployeeTerm> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_SERVICE, EmployeeTerm.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<EmployeeTerm> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_PROVIDER_SERVICE, EmployeeTerm.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<EmployeeTerm> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<EmployeeTerm> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit) {

        TypedQuery<EmployeeTerm> query = getEntityManager().createNamedQuery(EmployeeTerm.FIND_BY_SERVICE_POINT, EmployeeTerm.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByTerm(Term term) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_TERM, Long.class);
        query.setParameter("term", term);
        return query.getSingleResult();
    }

    @Override
    public Long countByWorkStation(WorkStation workStation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_WORK_STATION, Long.class);
        query.setParameter("work_station", workStation);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByServicePoint(ServicePoint servicePoint) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(EmployeeTerm.COUNT_BY_SERVICE_POINT, Long.class);
        query.setParameter("service_point", servicePoint);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteById(EmployeeTermId employeeTermId) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_BY_ID);
        query.setParameter("employeeId", employeeTermId.getEmployee());
        query.setParameter("termId", employeeTermId.getTerm());
        return query.executeUpdate();
    }


    @Override
    public Integer deleteForEmployees(List<Employee> employees) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_EMPLOYEES);
        query.setParameter("employees", employees);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForWorkStations(List<WorkStation> workStations) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_WORK_STATIONS);
        query.setParameter("work_stations", workStations);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForTerms(List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_TERMS);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForEmployeesAndWorkStations(List<Employee> employees, List<WorkStation> workStations) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_EMPLOYEES_AND_WORK_STATIONS);
        query.setParameter("employees", employees);
        query.setParameter("work_stations", workStations);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForEmployeesAndTerms(List<Employee> employees, List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_EMPLOYEES_AND_TERMS);
        query.setParameter("employees", employees);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForWorkStationsAndTerms(List<WorkStation> workStations, List<Term> terms) {

        Query query = getEntityManager().createNamedQuery(EmployeeTerm.DELETE_FOR_WORK_STATIONS_AND_TERMS);
        query.setParameter("work_stations", workStations);
        query.setParameter("terms", terms);
        return query.executeUpdate();
    }

    @Override
    public List<EmployeeTerm> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm) {
        return findByMultipleCriteria(servicePoints, workStations, employees, terms, services, providerServices, period, strictTerm, null, null);
    }

    @Override
    public List<EmployeeTerm> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<EmployeeTerm> criteriaQuery = criteriaBuilder.createQuery(EmployeeTerm.class);
        // FROM
        Root<EmployeeTerm> employeeTerm = criteriaQuery.from(EmployeeTerm.class);
        // SELECT
        criteriaQuery.select(employeeTerm).distinct(true);

        // INNER JOIN-s
        Join<EmployeeTerm, Term> term = null;
        Join<EmployeeTerm, Employee> employee = null;
        Join<EmployeeTerm, WorkStation> workStation = null;
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<WorkStation, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(servicePoints != null && servicePoints.size() > 0) {

            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);
            if(servicePoint == null) servicePoint = workStation.join(WorkStation_.servicePoint);

            predicates.add( servicePoint.in(servicePoints) );
        }

        if(workStations != null && workStations.size() > 0) {

            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);

            predicates.add( workStation.in(workStations) );
        }

        if(employees != null && employees.size() > 0) {

            if(employee == null) employee = employeeTerm.join(EmployeeTerm_.employee);

            predicates.add( employee.in(employees) );
        }

        if(terms != null && terms.size() > 0) {

            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            predicates.add( term.in(terms) );
        }

        if(services != null && services.size() > 0) {

            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            if(employee == null) employee = employeeTerm.join(EmployeeTerm_.employee);

            // only provider services that are executed on associated work station and by associated employee
            predicates.add( criteriaBuilder.and(providerService.in(employee.get(Employee_.suppliedServices)), service.in(services)) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            if(workStation == null) workStation = employeeTerm.join(EmployeeTerm_.workStation);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);

            if(employee == null) employee = employeeTerm.join(EmployeeTerm_.employee);

            // only provider services that are executed on associated work station and by associated employee
            predicates.add( criteriaBuilder.and(providerService.in(employee.get(Employee_.suppliedServices)), providerService.in(providerServices)) );
        }

        if(period != null) {

            if(term == null) term = employeeTerm.join(EmployeeTerm_.term);

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime() ) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime() ) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime() ) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime() ) );
            }
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] {}));

        TypedQuery<EmployeeTerm> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}

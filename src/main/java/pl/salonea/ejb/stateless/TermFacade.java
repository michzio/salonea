package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.TermFacadeInterface;
import pl.salonea.entities.*;
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
 * Created by michzio on 16/08/2015.
 */
@Stateless
@LocalBean
public class TermFacade extends AbstractFacade<Term>
            implements TermFacadeInterface.Local, TermFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public TermFacade() {
        super(Term.class);
    }

    @Override
    public List<Term> findByPeriod(Date startTime, Date endTime) {
        return findByPeriod(new Period(startTime, endTime));
    }

    @Override
    public List<Term> findByPeriod(Date startTime, Date endTime, Integer start, Integer offset) {
        return findByPeriod(new Period(startTime, endTime), start, offset);
    }

    @Override
    public List<Term> findByPeriod(Period period) {
        return findByPeriod(period, null, null);
    }

    @Override
    public List<Term> findByPeriod(Period period, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_PERIOD, Term.class);
        query.setParameter("start_time", period.getStartTime());
        query.setParameter("end_time", period.getEndTime());
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByPeriodStrict(Date startTime, Date endTime) {
        return findByPeriodStrict(new Period(startTime, endTime));
    }

    @Override
    public List<Term> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer offset) {
        return findByPeriodStrict(new Period(startTime, endTime), start, offset);
    }

    @Override
    public List<Term> findByPeriodStrict(Period period) {
        return findByPeriodStrict(period, null, null);
    }

    @Override
    public List<Term> findByPeriodStrict(Period period, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_PERIOD_STRICT, Term.class);
        query.setParameter("start_time", period.getStartTime());
        query.setParameter("end_time", period.getEndTime());
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findAfter(Date time) {
        return findAfter(time, null, null);
    }

    @Override
    public List<Term> findAfter(Date time, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_AFTER, Term.class);
        query.setParameter("time", time);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findAfterStrict(Date time) {
        return findAfterStrict(time, null, null);
    }

    @Override
    public List<Term> findAfterStrict(Date time, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_AFTER_STRICT, Term.class);
        query.setParameter("time", time);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findBefore(Date time) {
        return findBefore(time, null, null);
    }

    @Override
    public List<Term> findBefore(Date time, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BEFORE, Term.class);
        query.setParameter("time", time);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findBeforeStrict(Date time) {
        return findBeforeStrict(time, null, null);
    }

    @Override
    public List<Term> findBeforeStrict(Date time, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BEFORE_STRICT, Term.class);
        query.setParameter("time", time);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<Term> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_EMPLOYEE, Term.class);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation, null, null);
    }

    @Override
    public List<Term> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION, Term.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByService(Service service) {
        return findByService(service, null, null);
    }

    @Override
    public List<Term> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE, Term.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<Term> findByProviderService(ProviderService providerService, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_PROVIDER_SERVICE, Term.class);
        query.setParameter("provider_service", providerService);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServiceAndEmployee(Service service, Employee employee) {
        return findByServiceAndEmployee(service, employee, null, null);
    }

    @Override
    public List<Term> findByServiceAndEmployee(Service service, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("service", service);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee) {
        return findByProviderServiceAndEmployee(providerService, employee, null, null);
    }

    @Override
    public List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_PROVIDER_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("provider_service", providerService);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee) {
        return findByWorkStationAndEmployee(workStation, employee, null, null);
    }

    @Override
    public List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION_AND_EMPLOYEE, Term.class);
        query.setParameter("work_station", workStation);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStationAndService(WorkStation workStation, Service service) {
        return findByWorkStationAndService(workStation, service, null, null);
    }

    @Override
    public List<Term> findByWorkStationAndService(WorkStation workStation, Service service, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION_AND_SERVICE, Term.class);
        query.setParameter("work_station", workStation);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService) {
        return findByWorkStationAndProviderService(workStation, providerService, null, null);
    }

    @Override
    public List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE, Term.class);
        query.setParameter("work_station", workStation);
        query.setParameter("provider_service", providerService);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee) {
        return findByWorkStationAndServiceAndEmployee(workStation, service, employee, null, null);
    }

    @Override
    public List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION_AND_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("work_station", workStation);
        query.setParameter("service", service);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee) {
        return findByWorkStationAndProviderServiceAndEmployee(workStation, providerService, employee, null, null);
    }

    @Override
    public List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_WORK_STATION_AND_PROVIDER_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("work_station", workStation);
        query.setParameter("provider_service", providerService);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePoint(ServicePoint servicePoint) {
        return findByServicePoint(servicePoint, null, null);
    }

    @Override
    public List<Term> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT, Term.class);
        query.setParameter("service_point", servicePoint);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee) {
        return findByServicePointAndEmployee(servicePoint, employee, null, null);
    }

    @Override
    public List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT_AND_EMPLOYEE, Term.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service) {
        return findByServicePointAndService(servicePoint, service, null, null);
    }

    @Override
    public List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT_AND_SERVICE, Term.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService) {
        return findByServicePointAndProviderService(servicePoint, providerService, null, null);
    }

    @Override
    public List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE, Term.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("provider_service", providerService);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee) {
        return findByServicePointAndServiceAndEmployee(servicePoint, service, employee);
    }

    @Override
    public List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT_AND_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("service", service);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee) {
        return findByServicePointAndProviderServiceAndEmployee(servicePoint, providerService, employee, null, null);
    }

    @Override
    public List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee, Integer start, Integer offset) {

        TypedQuery<Term> query = getEntityManager().createNamedQuery(Term.FIND_BY_SERVICE_POINT_AND_PROVIDER_SERVICE_AND_EMPLOYEE, Term.class);
        query.setParameter("service_point", servicePoint);
        query.setParameter("provider_service", providerService);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm) {
        return findByMultipleCriteria(servicePoints, workStations, employees, services, providerServices, period, strictTerm, null, null);
    }

    @Override
    public List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer offset) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<Term> criteriaQuery = criteriaBuilder.createQuery(Term.class);
        // FROM
        Root<Term> term = criteriaQuery.from(Term.class);
        // SELECT
        criteriaQuery.select(term).distinct(true);

        // INNER JOIN-s
        Join<Term, TermEmployeeWorkOn> employeeTerm = null;
        Join<TermEmployeeWorkOn, Employee> employee = null;
        Join<TermEmployeeWorkOn, WorkStation> workStation = null;
        Join<WorkStation, ServicePoint> servicePoint = null;
        Join<WorkStation, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(servicePoints != null && servicePoints.size() > 0) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);
            if(servicePoint == null) servicePoint = workStation.join(WorkStation_.servicePoint);

            predicates.add( servicePoint.in(servicePoints) );
        }

        if(workStations != null && workStations.size() > 0) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);

            predicates.add( workStation.in(workStations) );
        }

        if(employees != null && employees.size() > 0) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(employee == null) employee = employeeTerm.join(TermEmployeeWorkOn_.employee);

            predicates.add( employee.in(employees) );
        }

        if(services != null && services.size() > 0) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.in(services) );
        }

        if(providerServices != null && providerServices.size() > 0) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);

            predicates.add( providerService.in(providerServices) );
        }

        if(period != null) {

            if(strictTerm != null && strictTerm) {
                predicates.add( criteriaBuilder.lessThanOrEqualTo( term.get(Term_.openingTime), period.getStartTime()) );
                predicates.add( criteriaBuilder.greaterThanOrEqualTo( term.get(Term_.closingTime), period.getEndTime()) );
            } else {
                predicates.add( criteriaBuilder.lessThan( term.get(Term_.openingTime), period.getEndTime()) );
                predicates.add( criteriaBuilder.greaterThan( term.get(Term_.closingTime), period.getStartTime()) );
            }
        }

        // take into account that both employee and work station must provide given provider service
        if(providerServices != null || services != null) {

            if(employeeTerm == null) employeeTerm = term.join(Term_.employeesWorkStation);
            if(workStation == null) workStation = employeeTerm.join(TermEmployeeWorkOn_.workStation);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(employee == null) employee = employeeTerm.join(TermEmployeeWorkOn_.employee);
            // if(employeeProviderService == null) employeeProviderService = employee.join(Employee_.suppliedServices);

            predicates.add( criteriaBuilder.isMember(providerService, (employee.get(Employee_.suppliedServices)) ) );
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));

        TypedQuery<Term> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteOlderThan(Date time) {

        Query query = getEntityManager().createNamedQuery(Term.DELETE_OLDER_THAN);
        query.setParameter("time", time);
        return query.executeUpdate();
    }
}
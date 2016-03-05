package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.TermEmployeeId;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 17/08/2015.
 */
public interface TermEmployeeWorkOnFacadeInterface extends AbstractFacadeInterface<TermEmployeeWorkOn> {

    // concrete interface
    TermEmployeeWorkOn createForEmployeeAndTerm(Long employeeId, Long termId, TermEmployeeWorkOn employeeTerm);
    TermEmployeeWorkOn update(TermEmployeeId termEmployeeId, TermEmployeeWorkOn employeeTerm);

    List<TermEmployeeWorkOn> findByPeriod(Date startTime, Date endTime);
    List<TermEmployeeWorkOn> findByPeriod(Date startTime, Date endTime, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findByPeriod(Period period);
    List<TermEmployeeWorkOn> findByPeriod(Period period, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findByPeriodStrict(Date startTime, Date endTime);
    List<TermEmployeeWorkOn> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findByPeriodStrict(Period period);
    List<TermEmployeeWorkOn> findByPeriodStrict(Period period, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findAfter(Date time);
    List<TermEmployeeWorkOn> findAfter(Date time, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findAfterStrict(Date time);
    List<TermEmployeeWorkOn> findAfterStrict(Date time, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findBefore(Date time);
    List<TermEmployeeWorkOn> findBefore(Date time, Integer start, Integer limit);
    List<TermEmployeeWorkOn> findBeforeStrict(Date time);
    List<TermEmployeeWorkOn> findBeforeStrict(Date time, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByEmployee(Employee employee);
    List<TermEmployeeWorkOn> findByEmployee(Employee employee, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByTerm(Term term);
    List<TermEmployeeWorkOn> findByTerm(Term term, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByWorkStation(WorkStation workStation);
    List<TermEmployeeWorkOn> findByWorkStation(WorkStation workStation, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByService(Service service);
    List<TermEmployeeWorkOn> findByService(Service service, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByProviderService(ProviderService providerService);
    List<TermEmployeeWorkOn> findByProviderService(ProviderService providerService, Integer start, Integer limit);

    List<TermEmployeeWorkOn> findByServicePoint(ServicePoint servicePoint);
    List<TermEmployeeWorkOn> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);

    // count
    Long countByEmployee(Employee employee);
    Long countByTerm(Term term);
    Long countByWorkStation(WorkStation workStation);
    Long countByService(Service service);
    Long countByProviderService(ProviderService providerService);
    Long countByServicePoint(ServicePoint servicePoint);

    // delete
    Integer deleteById(TermEmployeeId termEmployeeId);
    Integer deleteForEmployees(List<Employee> employees);
    Integer deleteForWorkStations(List<WorkStation> workStations);
    Integer deleteForTerms(List<Term> terms);

    Integer deleteForEmployeesAndWorkStations(List<Employee> employees, List<WorkStation> workStations);
    Integer deleteForEmployeesAndTerms(List<Employee> employees, List<Term> terms);
    Integer deleteForWorkStationsAndTerms(List<WorkStation> workStations, List<Term> terms);

    List<TermEmployeeWorkOn> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms,
                                                    List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm);
    List<TermEmployeeWorkOn> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms,
                                                    List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends TermEmployeeWorkOnFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TermEmployeeWorkOnFacadeInterface { }
}

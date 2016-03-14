package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.EmployeeTermId;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 17/08/2015.
 */
public interface EmployeeTermFacadeInterface extends AbstractFacadeInterface<EmployeeTerm> {

    // concrete interface
    EmployeeTerm createForEmployeeAndTerm(Long employeeId, Long termId, EmployeeTerm employeeTerm);
    EmployeeTerm update(EmployeeTermId employeeTermId, EmployeeTerm employeeTerm);

    List<EmployeeTerm> findByPeriod(Date startTime, Date endTime);
    List<EmployeeTerm> findByPeriod(Date startTime, Date endTime, Integer start, Integer limit);
    List<EmployeeTerm> findByPeriod(Period period);
    List<EmployeeTerm> findByPeriod(Period period, Integer start, Integer limit);
    List<EmployeeTerm> findByPeriodStrict(Date startTime, Date endTime);
    List<EmployeeTerm> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer limit);
    List<EmployeeTerm> findByPeriodStrict(Period period);
    List<EmployeeTerm> findByPeriodStrict(Period period, Integer start, Integer limit);

    List<EmployeeTerm> findAfter(Date time);
    List<EmployeeTerm> findAfter(Date time, Integer start, Integer limit);
    List<EmployeeTerm> findAfterStrict(Date time);
    List<EmployeeTerm> findAfterStrict(Date time, Integer start, Integer limit);
    List<EmployeeTerm> findBefore(Date time);
    List<EmployeeTerm> findBefore(Date time, Integer start, Integer limit);
    List<EmployeeTerm> findBeforeStrict(Date time);
    List<EmployeeTerm> findBeforeStrict(Date time, Integer start, Integer limit);

    List<EmployeeTerm> findByEmployee(Employee employee);
    List<EmployeeTerm> findByEmployee(Employee employee, Integer start, Integer limit);

    List<EmployeeTerm> findByTerm(Term term);
    List<EmployeeTerm> findByTerm(Term term, Integer start, Integer limit);

    List<EmployeeTerm> findByWorkStation(WorkStation workStation);
    List<EmployeeTerm> findByWorkStation(WorkStation workStation, Integer start, Integer limit);

    List<EmployeeTerm> findByService(Service service);
    List<EmployeeTerm> findByService(Service service, Integer start, Integer limit);

    List<EmployeeTerm> findByProviderService(ProviderService providerService);
    List<EmployeeTerm> findByProviderService(ProviderService providerService, Integer start, Integer limit);

    List<EmployeeTerm> findByServicePoint(ServicePoint servicePoint);
    List<EmployeeTerm> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);

    // count
    Long countByEmployee(Employee employee);
    Long countByTerm(Term term);
    Long countByWorkStation(WorkStation workStation);
    Long countByService(Service service);
    Long countByProviderService(ProviderService providerService);
    Long countByServicePoint(ServicePoint servicePoint);

    // delete
    Integer deleteById(EmployeeTermId employeeTermId);
    Integer deleteForEmployees(List<Employee> employees);
    Integer deleteForWorkStations(List<WorkStation> workStations);
    Integer deleteForTerms(List<Term> terms);

    Integer deleteForEmployeesAndWorkStations(List<Employee> employees, List<WorkStation> workStations);
    Integer deleteForEmployeesAndTerms(List<Employee> employees, List<Term> terms);
    Integer deleteForWorkStationsAndTerms(List<WorkStation> workStations, List<Term> terms);

    List<EmployeeTerm> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms,
                                                    List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm);
    List<EmployeeTerm> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, List<Term> terms,
                                                    List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends EmployeeTermFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends EmployeeTermFacadeInterface { }
}
package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 16/08/2015.
 */
public interface TermFacadeInterface extends AbstractFacadeInterface<Term> {

    // concrete interface
    Term update(Term term, Boolean retainTransientFields);

    List<Term> findAllEagerly();
    List<Term> findAllEagerly(Integer start, Integer limit);
    Term findByIdEagerly(Long termId);

    List<Term> findByPeriod(Date startTime, Date endTime);
    List<Term> findByPeriod(Date startTime, Date endTime, Integer start, Integer limit);
    List<Term> findByPeriod(Period period);
    List<Term> findByPeriod(Period period, Integer start, Integer limit);
    List<Term> findByPeriodStrict(Date startTime, Date endTime);
    List<Term> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer limit);
    List<Term> findByPeriodStrict(Period period);
    List<Term> findByPeriodStrict(Period period, Integer start, Integer limit);

    List<Term> findAfter(Date time);
    List<Term> findAfter(Date time, Integer start, Integer limit);
    List<Term> findAfterStrict(Date time);
    List<Term> findAfterStrict(Date time, Integer start, Integer limit);
    List<Term> findBefore(Date time);
    List<Term> findBefore(Date time, Integer start, Integer limit);
    List<Term> findBeforeStrict(Date time);
    List<Term> findBeforeStrict(Date time, Integer start, Integer limit);

    List<Term> findByEmployee(Employee employee);
    List<Term> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Term> findByEmployeeEagerly(Employee employee);
    List<Term> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);

    List<Term> findByWorkStation(WorkStation workStation);
    List<Term> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<Term> findByWorkStationEagerly(WorkStation workStation);
    List<Term> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit);

    List<Term> findByService(Service service);
    List<Term> findByService(Service service, Integer start, Integer limit);
    List<Term> findByServiceEagerly(Service service);
    List<Term> findByServiceEagerly(Service service, Integer start, Integer limit);

    List<Term> findByProviderService(ProviderService providerService);
    List<Term> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<Term> findByProviderServiceEagerly(ProviderService providerService);
    List<Term> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit);

    List<Term> findByServicePoint(ServicePoint servicePoint);
    List<Term> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<Term> findByServicePointEagerly(ServicePoint servicePoint);
    List<Term> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);

    // other
    List<Term> findByServiceAndEmployee(Service service, Employee employee);
    List<Term> findByServiceAndEmployee(Service service, Employee employee, Integer start, Integer limit);
    List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee);
    List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee, Integer start, Integer limit);
    List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee);
    List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee, Integer start, Integer limit);
    List<Term> findByWorkStationAndService(WorkStation workStation, Service service);
    List<Term> findByWorkStationAndService(WorkStation workStation, Service service, Integer start, Integer limit);
    List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService);
    List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService, Integer start, Integer limit);
    List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee);
    List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee, Integer start, Integer limit);
    List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee);
    List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee, Integer start, Integer limit);
    List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee);
    List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee, Integer start, Integer limit);
    List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service);
    List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service, Integer start, Integer limit);
    List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService);
    List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService, Integer start, Integer limit);
    List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee);
    List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee, Integer start, Integer limit);
    List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee);
    List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee, Integer start, Integer limit);

    List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                      List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm);
    List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                      List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer limit);
    List<Term> findByMultipleCriteriaEagerly(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                             List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm);
    List<Term> findByMultipleCriteriaEagerly(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                             List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer limit);

    // count
    Long countByEmployee(Employee employee);
    Long countByWorkStation(WorkStation workStation);
    Long countByService(Service service);
    Long countByProviderService(ProviderService providerService);
    Long countByServicePoint(ServicePoint servicePoint);

    Integer deleteOlderThan(Date time);

    @javax.ejb.Local
    interface Local extends TermFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TermFacadeInterface { }
}

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
    List<Term> findByPeriod(Date startTime, Date endTime);
    List<Term> findByPeriod(Date startTime, Date endTime, Integer start, Integer offset);
    List<Term> findByPeriod(Period period);
    List<Term> findByPeriod(Period period, Integer start, Integer offset);
    List<Term> findByPeriodStrict(Date startTime, Date endTime);
    List<Term> findByPeriodStrict(Date startTime, Date endTime, Integer start, Integer offset);
    List<Term> findByPeriodStrict(Period period);
    List<Term> findByPeriodStrict(Period period, Integer start, Integer offset);
    List<Term> findAfter(Date time);
    List<Term> findAfter(Date time, Integer start, Integer offset);
    List<Term> findAfterStrict(Date time);
    List<Term> findAfterStrict(Date time, Integer start, Integer offset);
    List<Term> findBefore(Date time);
    List<Term> findBefore(Date time, Integer start, Integer offset);
    List<Term> findBeforeStrict(Date time);
    List<Term> findBeforeStrict(Date time, Integer start, Integer offset);
    List<Term> findByEmployee(Employee employee);
    List<Term> findByEmployee(Employee employee, Integer start, Integer offset);
    List<Term> findByWorkStation(WorkStation workStation);
    List<Term> findByWorkStation(WorkStation workStation, Integer start, Integer offset);
    List<Term> findByService(Service service);
    List<Term> findByService(Service service, Integer start, Integer offset);
    List<Term> findByProviderService(ProviderService providerService);
    List<Term> findByProviderService(ProviderService providerService, Integer start, Integer offset);
    List<Term> findByServiceAndEmployee(Service service, Employee employee);
    List<Term> findByServiceAndEmployee(Service service, Employee employee, Integer start, Integer offset);
    List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee);
    List<Term> findByProviderServiceAndEmployee(ProviderService providerService, Employee employee, Integer start, Integer offset);
    List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee);
    List<Term> findByWorkStationAndEmployee(WorkStation workStation, Employee employee, Integer start, Integer offset);
    List<Term> findByWorkStationAndService(WorkStation workStation, Service service);
    List<Term> findByWorkStationAndService(WorkStation workStation, Service service, Integer start, Integer offset);
    List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService);
    List<Term> findByWorkStationAndProviderService(WorkStation workStation, ProviderService providerService, Integer start, Integer offset);
    List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee);
    List<Term> findByWorkStationAndServiceAndEmployee(WorkStation workStation, Service service, Employee employee, Integer start, Integer offset);
    List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee);
    List<Term> findByWorkStationAndProviderServiceAndEmployee(WorkStation workStation, ProviderService providerService, Employee employee, Integer start, Integer offset);
    List<Term> findByServicePoint(ServicePoint servicePoint);
    List<Term> findByServicePoint(ServicePoint servicePoint, Integer start, Integer offset);
    List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee);
    List<Term> findByServicePointAndEmployee(ServicePoint servicePoint, Employee employee, Integer start, Integer offset);
    List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service);
    List<Term> findByServicePointAndService(ServicePoint servicePoint, Service service, Integer start, Integer offset);
    List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService);
    List<Term> findByServicePointAndProviderService(ServicePoint servicePoint, ProviderService providerService, Integer start, Integer offset);
    List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee);
    List<Term> findByServicePointAndServiceAndEmployee(ServicePoint servicePoint, Service service, Employee employee, Integer start, Integer offset);
    List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee);
    List<Term> findByServicePointAndProviderServiceAndEmployee(ServicePoint servicePoint, ProviderService providerService, Employee employee, Integer start, Integer offset);
    List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                      List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm);
    List<Term> findByMultipleCriteria(List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees,
                                      List<Service> services, List<ProviderService> providerServices, Period period, Boolean strictTerm, Integer start, Integer offset);
    Integer deleteOlderThan(Date time);

    @javax.ejb.Local
    interface Local extends TermFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends TermFacadeInterface { }
}

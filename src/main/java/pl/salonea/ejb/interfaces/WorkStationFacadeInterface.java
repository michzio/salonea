package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.enums.WorkStationType;
import pl.salonea.utils.Period;

import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 09/08/2015.
 */
public interface WorkStationFacadeInterface extends AbstractFacadeInterface<WorkStation> {

    // concrete interface
    List<WorkStation> findByServicePoint(ServicePoint servicePoint);
    List<WorkStation> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<WorkStation> findByType(WorkStationType type);
    List<WorkStation> findByType(WorkStationType type, Integer start, Integer limit);
    List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type);
    List<WorkStation> findByServicePointAndType(ServicePoint servicePoint, WorkStationType type, Integer start, Integer limit);
    List<WorkStation> findByService(Service service);
    List<WorkStation> findByService(Service service, Integer start, Integer limit);
    List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime);
    List<WorkStation> findByServiceAndTerm(Service service, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime);
    List<WorkStation> findByServiceAndTermStrict(Service service, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint);
    List<WorkStation> findByServiceAndServicePoint(Service service, ServicePoint servicePoint, Integer start, Integer limit);
    List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByServiceAndServicePointAndTerm(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByServiceAndServicePointAndTermStrict(Service service, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByProviderService(ProviderService providerService);
    List<WorkStation> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime);
    List<WorkStation> findByProviderServiceAndTerm(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime);
    List<WorkStation> findByProviderServiceAndTermStrict(ProviderService providerService, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint);
    List<WorkStation> findByProviderServiceAndServicePoint(ProviderService providerService, ServicePoint servicePoint, Integer start, Integer limit);
    List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByProviderServiceAndServicePointAndTerm(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByProviderServiceAndServicePointAndTermStrict(ProviderService providerService, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployee(Employee employee);
    List<WorkStation> findByEmployee(Employee employee, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndTerm(Employee employee, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndTermStrict(Employee employee, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint);
    List<WorkStation> findByEmployeeAndServicePoint(Employee employee, ServicePoint servicePoint, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndServicePointAndTerm(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndServicePointAndTermStrict(Employee employee, ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndService(Employee employee, Service service);
    List<WorkStation> findByEmployeeAndService(Employee employee, Service service, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndServiceAndTerm(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime);
    List<WorkStation> findByEmployeeAndServiceAndTermStrict(Employee employee, Service service, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByTerm(Date startTime, Date endTime);
    List<WorkStation> findByTerm(Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByTermStrict(Date startTime, Date endTime);
    List<WorkStation> findByTermStrict(Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByTermAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByTermAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByTermStrictAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime);
    List<WorkStation> findByTermStrictAndServicePoint(ServicePoint servicePoint, Date startTime, Date endTime, Integer start, Integer limit);
    List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, WorkStationType type, Period period, Boolean strictTerm);
    List<WorkStation> findByMultipleCriteria(List<ServicePoint> servicePoints, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, WorkStationType type, Period period, Boolean strictTerm, Integer start, Integer limit);
    Integer deleteByServicePoint(ServicePoint servicePoint);

    @javax.ejb.Local
    interface Local extends WorkStationFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends WorkStationFacadeInterface { }
}

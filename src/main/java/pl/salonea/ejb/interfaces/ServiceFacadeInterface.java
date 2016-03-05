package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;

import java.util.List;

/**
 * Created by michzio on 28/07/2015.
 */
public interface ServiceFacadeInterface extends AbstractFacadeInterface<Service> {

    // concrete interface
    Service update(Service service, Boolean retainTransientFields);

    List<Service> findAllEagerly();
    List<Service> findAllEagerly(Integer start, Integer limit);
    Service findByIdEagerly(Integer serviceId);

    List<Service> findByName(String name);
    List<Service> findByName(String name, Integer start, Integer limit);
    List<Service> findByDescription(String description);
    List<Service> findByDescription(String description, Integer start, Integer limit);
    List<Service> findByKeyword(String keyword);
    List<Service> findByKeyword(String keyword, Integer start, Integer limit);

    List<Service> findByCategory(ServiceCategory serviceCategory);
    List<Service> findByCategory(ServiceCategory serviceCategory, Integer start, Integer limit);
    List<Service> findByCategoryEagerly(ServiceCategory serviceCategory);
    List<Service> findByCategoryEagerly(ServiceCategory serviceCategory, Integer start, Integer limit);
    List<Service> findByCategoryAndName(ServiceCategory serviceCategory, String name);
    List<Service> findByCategoryAndName(ServiceCategory serviceCategory, String name, Integer start, Integer limit);
    List<Service> findByCategoryAndDescription(ServiceCategory serviceCategory, String description);
    List<Service> findByCategoryAndDescription(ServiceCategory serviceCategory, String description, Integer start, Integer limit);
    List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword);
    List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword, Integer start, Integer limit);

    List<Service> findByProvider(Provider provider);
    List<Service> findByProvider(Provider provider, Integer start, Integer limit);
    List<Service> findByProviderEagerly(Provider provider);
    List<Service> findByProviderEagerly(Provider provider, Integer start, Integer limit);

    List<Service> findByEmployee(Employee employee);
    List<Service> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Service> findByEmployeeEagerly(Employee employee);
    List<Service> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);

    List<Service> findByWorkStation(WorkStation workStation);
    List<Service> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<Service> findByWorkStationEagerly(WorkStation workStation);
    List<Service> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit);

    List<Service> findByServicePoint(ServicePoint servicePoint);
    List<Service> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<Service> findByServicePointEagerly(ServicePoint servicePoint);
    List<Service> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);

    Long countByCategory(ServiceCategory serviceCategory);
    Long countByProvider(Provider provider);
    Long countByEmployee(Employee employee);
    Long countByServicePoint(ServicePoint servicePoint);
    Long countByWorkStation(WorkStation workStation);

    Integer deleteByName(String name);
    Integer deleteByCategory(ServiceCategory serviceCategory);

    List<Service> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                         List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints);
    List<Service> findByMultipleCriteria(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                         List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints, Integer start, Integer limit);
    List<Service> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> serviceCategories,
                                         List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints);
    List<Service> findByMultipleCriteria(List<String> keywords, List<ServiceCategory> serviceCategories,
                                         List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints, Integer start, Integer limit);

    List<Service> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints);
    List<Service> findByMultipleCriteriaEagerly(List<String> names, List<String> descriptions, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints, Integer start, Integer limit);
    List<Service> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints);
    List<Service> findByMultipleCriteriaEagerly(List<String> keywords, List<ServiceCategory> serviceCategories,
                                                List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints, Integer start, Integer limit);


    @javax.ejb.Local
    interface Local extends ServiceFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServiceFacadeInterface { }
}

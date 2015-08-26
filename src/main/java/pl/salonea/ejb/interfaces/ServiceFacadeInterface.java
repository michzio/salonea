package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;

import java.util.List;

/**
 * Created by michzio on 28/07/2015.
 */
public interface ServiceFacadeInterface extends AbstractFacadeInterface<Service> {

    // concrete interface
    List<Service> findByName(String name);
    List<Service> findByName(String name, Integer start, Integer limit);
    List<Service> findByDescription(String description);
    List<Service> findByDescription(String description, Integer start, Integer limit);
    List<Service> searchByKeyword(String keyword);
    List<Service> searchByKeyword(String keyword, Integer start, Integer limit);
    List<Service> findByCategory(ServiceCategory serviceCategory);
    List<Service> findByCategory(ServiceCategory serviceCategory, Integer start, Integer limit);
    List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword);
    List<Service> findByCategoryAndKeyword(ServiceCategory serviceCategory, String keyword, Integer start, Integer limit);
    List<Service> findByProvider(Provider provider);
    List<Service> findByProvider(Provider provider, Integer start, Integer limit);
    List<Service> findByEmployee(Employee employee);
    List<Service> findByEmployee(Employee employee, Integer start, Integer limit);
    List<Service> findByWorkStation(WorkStation workStation);
    List<Service> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<Service> findByServicePoint(ServicePoint servicePoint);
    List<Service> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    Integer deleteByName(String name);
    Integer deleteByCategory(ServiceCategory serviceCategory);
    List<Service> findByMultipleCriteria(String name, String description, String keyword, List<ServiceCategory> serviceCategories,
                          List<Provider> providers, List<Employee> employees, List<WorkStation> workStations, List<ServicePoint> servicePoints);


    @javax.ejb.Local
    interface Local extends ServiceFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServiceFacadeInterface { }
}

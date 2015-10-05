package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;

import java.util.List;

/**
 * Created by michzio on 25/07/2015.
 */
public interface ProviderServiceFacadeInterface extends AbstractFacadeInterface<ProviderService> {

    // concrete interface
    List<ProviderService> findAllEagerly();
    List<ProviderService> findAllEagerly(Integer start, Integer limit);
    ProviderService findByIdEagerly(ProviderServiceId providerServiceId);
    List<ProviderService> findByProvider(Provider provider);
    List<ProviderService> findByProvider(Provider provider, Integer start, Integer limit);
    List<ProviderService> findByProviderEagerly(Provider provider);
    List<ProviderService> findByProviderEagerly(Provider provider, Integer start, Integer limit);
    List<ProviderService> findByService(Service service);
    List<ProviderService> findByService(Service service, Integer start, Integer limit);
    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory);
    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer limit);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer limit);
    List<ProviderService> findByDescription(String description);
    List<ProviderService> findByDescription(String description, Integer start, Integer limit);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDescription(Service service, String description);
    List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer limit);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer limit);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer limit);
    List<ProviderService> findByWorkStation(WorkStation workStation);
    List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<ProviderService> findByEmployee(Employee employee);
    List<ProviderService> findByEmployee(Employee employee, Integer start, Integer limit);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer limit);
    List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 String description, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees);
    List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 String description, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees, Integer start, Integer limit);
    List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 String description, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees);
    List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 String description, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<WorkStation> workStations, List<Employee> employees, Integer start, Integer limit);
    Integer updateDiscountForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Short newDiscount);
    Integer updateDiscountForProviderAndEmployee(Provider provider, Employee employee, Short newDiscount);
    Integer deleteForOnlyWorkStation(WorkStation workStation);
    Integer deleteForProviderAndOnlyEmployee(Provider provider, Employee employee);
    Integer deleteForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory);
    Integer deleteForProvider(Provider provider);
    Integer deleteForService(Service service);


    @javax.ejb.Remote
    interface Remote extends ProviderServiceFacadeInterface { }

    @javax.ejb.Local
    interface Local extends ProviderServiceFacadeInterface { }
}

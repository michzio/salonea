package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ProviderServiceId;

import java.util.List;

/**
 * Created by michzio on 25/07/2015.
 */
public interface ProviderServiceFacadeInterface extends AbstractFacadeInterface<ProviderService> {

    // concrete interface
    ProviderService createForProviderAndService(Long providerId, Integer serviceId, ProviderService providerService);
    ProviderService update(ProviderServiceId providerServiceId, ProviderService providerService);
    ProviderService update(ProviderServiceId providerServiceId, ProviderService providerService, Boolean retainTransientFields);

    List<ProviderService> findAllEagerly();
    List<ProviderService> findAllEagerly(Integer start, Integer limit);
    ProviderService findByIdEagerly(ProviderServiceId providerServiceId);

    List<ProviderService> findByDescription(String description);
    List<ProviderService> findByDescription(String description, Integer start, Integer limit);

    List<ProviderService> findByProvider(Provider provider);
    List<ProviderService> findByProvider(Provider provider, Integer start, Integer limit);
    List<ProviderService> findByProviderEagerly(Provider provider);
    List<ProviderService> findByProviderEagerly(Provider provider, Integer start, Integer limit);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer limit);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer limit);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer limit);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer limit);

    List<ProviderService> findByService(Service service);
    List<ProviderService> findByService(Service service, Integer start, Integer limit);
    List<ProviderService> findByServiceEagerly(Service service);
    List<ProviderService> findByServiceEagerly(Service service, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDescription(Service service, String description);
    List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer limit);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer limit);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer limit);

    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory);
    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer limit);
    List<ProviderService> findByServiceCategoryEagerly(ServiceCategory serviceCategory);
    List<ProviderService> findByServiceCategoryEagerly(ServiceCategory serviceCategory, Integer start, Integer limit);

    List<ProviderService> findByWorkStation(WorkStation workStation);
    List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer limit);
    List<ProviderService> findByWorkStationEagerly(WorkStation workStation);
    List<ProviderService> findByWorkStationEagerly(WorkStation workStation, Integer start, Integer limit);

    List<ProviderService> findByServicePoint(ServicePoint servicePoint);
    List<ProviderService> findByServicePoint(ServicePoint servicePoint, Integer start, Integer limit);
    List<ProviderService> findByServicePointEagerly(ServicePoint servicePoint);
    List<ProviderService> findByServicePointEagerly(ServicePoint servicePoint, Integer start, Integer limit);

    List<ProviderService> findByEmployee(Employee employee);
    List<ProviderService> findByEmployee(Employee employee, Integer start, Integer limit);
    List<ProviderService> findByEmployeeEagerly(Employee employee);
    List<ProviderService> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);

    Integer updateDiscountForProvider(Provider provider, Short newDiscount);
    Integer updateDiscountForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Short newDiscount);
    Integer updateDiscountForProviderAndEmployee(Provider provider, Employee employee, Short newDiscount);
    Integer updateDiscountForProviderAndServiceCategoryAndEmployee(Provider provider, ServiceCategory serviceCategory, Employee employee, Short newDiscount);

    Integer deleteById(ProviderServiceId providerServiceId);
    Integer deleteForOnlyWorkStation(WorkStation workStation);
    Integer deleteForProviderAndOnlyEmployee(Provider provider, Employee employee);
    Integer deleteForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory);
    Integer deleteForProviderAndServiceCategoryAndOnlyEmployee(Provider provider, ServiceCategory serviceCategory, Employee employee);
    Integer deleteForProvider(Provider provider);
    Integer deleteForService(Service service);

    Long countByProvider(Provider provider);
    Long countByService(Service service);
    Long countByServiceCategory(ServiceCategory serviceCategory);
    Long countByWorkStation(WorkStation workStation);
    Long countByServicePoint(ServicePoint servicePoint);
    Long countByEmployee(Employee employee);


    List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees);
    List<ProviderService> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, Integer start, Integer limit);
    List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees);
    List<ProviderService> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ServiceCategory> serviceCategories,
                                                 List<String> descriptions, Double minPrice, Double maxPrice, Boolean includeDiscounts,
                                                 Short minDiscount, Short maxDiscount, List<ServicePoint> servicePoints, List<WorkStation> workStations, List<Employee> employees, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends ProviderServiceFacadeInterface { }

    @javax.ejb.Local
    interface Local extends ProviderServiceFacadeInterface { }
}

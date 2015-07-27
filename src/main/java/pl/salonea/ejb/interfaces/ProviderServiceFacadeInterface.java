package pl.salonea.ejb.interfaces;

import pl.salonea.entities.*;

import java.util.List;

/**
 * Created by michzio on 25/07/2015.
 */
public interface ProviderServiceFacadeInterface extends AbstractFacadeInterface<ProviderService> {

    // concrete interface
    List<ProviderService> findByProvider(Provider provider);
    List<ProviderService> findByProvider(Provider provider, Integer start, Integer offset);
    List<ProviderService> findByService(Service service);
    List<ProviderService> findByService(Service service, Integer start, Integer offset);
    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory);
    List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer offset);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory);
    List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer offset);
    List<ProviderService> findByDescription(String description);
    List<ProviderService> findByDescription(String description, Integer start, Integer offset);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description);
    List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer offset);
    List<ProviderService> findByServiceAndDescription(Service service, String description);
    List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer offset);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice);
    List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer offset);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount);
    List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer offset);
    List<ProviderService> findByWorkStation(WorkStation workStation);
    List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer offset);
    List<ProviderService> findByEmployee(Employee employee);
    List<ProviderService> findByEmployee(Employee employee, Integer start, Integer offset);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee);
    List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer offset);
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

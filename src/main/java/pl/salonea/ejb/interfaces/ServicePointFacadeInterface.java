package pl.salonea.ejb.interfaces;

import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.utils.CoordinatesCircle;
import pl.salonea.utils.CoordinatesSquare;

import java.util.List;

/**
 * Created by michzio on 02/08/2015.
 */
public interface ServicePointFacadeInterface extends AbstractFacadeInterface<ServicePoint> {

    // concrete interface
    ServicePoint createForProvider(Long providerId, ServicePoint servicePoint);
    ServicePoint update(ServicePointId servicePointId, ServicePoint servicePoint);
    List<ServicePoint> findAllEagerly();
    List<ServicePoint> findAllEagerly(Integer start, Integer limit);
    ServicePoint findByIdEagerly(ServicePointId servicePointId);

    List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84,Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84,Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by provider
    List<ServicePoint> findByProvider(Provider provider);
    List<ServicePoint> findByProvider(Provider provider, Integer start, Integer limit);
    List<ServicePoint> findByProviderEagerly(Provider provider);
    List<ServicePoint> findByProviderEagerly(Provider provider, Integer start, Integer limit);
    List<ServicePoint> findByProviderAndAddress(Provider provider, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByProviderAndAddress(Provider provider, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by service
    List<ServicePoint> findByService(Service service);
    List<ServicePoint> findByService(Service service, Integer start, Integer limit);
    List<ServicePoint> findByServiceEagerly(Service service);
    List<ServicePoint> findByServiceEagerly(Service service, Integer start, Integer limit);
    List<ServicePoint> findByServiceAndAddress(Service service, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByServiceAndAddress(Service service, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by employee
    List<ServicePoint> findByEmployee(Employee employee);
    List<ServicePoint> findByEmployee(Employee employee, Integer start, Integer limit);
    List<ServicePoint> findByEmployeeEagerly(Employee employee);
    List<ServicePoint> findByEmployeeEagerly(Employee employee, Integer start, Integer limit);
    List<ServicePoint> findByEmployeeAndAddress(Employee employee, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByEmployeeAndAddress(Employee employee, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByEmployeeAndCoordinatesSquare(Employee employee, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByEmployeeAndCoordinatesSquare(Employee employee, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByEmployeeAndCoordinatesCircle(Employee employee, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByEmployeeAndCoordinatesCircle(Employee employee, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by provider service
    List<ServicePoint> findByProviderService(ProviderService providerService);
    List<ServicePoint> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<ServicePoint> findByProviderServiceEagerly(ProviderService providerService);
    List<ServicePoint> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit);
    List<ServicePoint> findByProviderServiceAndAddress(ProviderService providerService, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByProviderServiceAndAddress(ProviderService providerService, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByProviderServiceAndCoordinatesSquare(ProviderService providerService, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByProviderServiceAndCoordinatesSquare(ProviderService providerService, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByProviderServiceAndCoordinatesCircle(ProviderService providerService, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByProviderServiceAndCoordinatesCircle(ProviderService providerService, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by corporation
    List<ServicePoint> findByCorporation(Corporation corporation);
    List<ServicePoint> findByCorporation(Corporation corporation, Integer start, Integer limit);
    List<ServicePoint> findByCorporationEagerly(Corporation corporation);
    List<ServicePoint> findByCorporationEagerly(Corporation corporation, Integer start, Integer limit);
    List<ServicePoint> findByCorporationAndAddress(Corporation corporation, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByCorporationAndAddress(Corporation corporation, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByCorporationAndCoordinatesSquare(Corporation corporation, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByCorporationAndCoordinatesSquare(Corporation corporation, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByCorporationAndCoordinatesCircle(Corporation corporation, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByCorporationAndCoordinatesCircle(Corporation corporation, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    // by industry
    List<ServicePoint> findByIndustry(Industry industry);
    List<ServicePoint> findByIndustry(Industry industry, Integer start, Integer limit);
    List<ServicePoint> findByIndustryEagerly(Industry industry);
    List<ServicePoint> findByIndustryEagerly(Industry industry, Integer start, Integer limit);
    List<ServicePoint> findByIndustryAndAddress(Industry industry, String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByIndustryAndAddress(Industry industry, String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByIndustryAndCoordinatesSquare(Industry industry, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByIndustryAndCoordinatesSquare(Industry industry, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByIndustryAndCoordinatesCircle(Industry industry, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByIndustryAndCoordinatesCircle(Industry industry, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);

    Long countByProvider(Provider provider);
    Long countByService(Service service);
    Long countByEmployee(Employee employee);
    Long countByProviderService(ProviderService providerService);
    Long countByCorporation(Corporation corporation);
    Long countByIndustry(Industry industry);
    Integer deleteByProvider(Provider provider);
    Integer deleteById(ServicePointId servicePointId);

    // Criteria API query methods interface
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle, Integer start, Integer limit);

    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle);
    List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends ServicePointFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServicePointFacadeInterface { }

}

package pl.salonea.ejb.interfaces;

import pl.salonea.embeddables.Address;
import pl.salonea.entities.*;
import pl.salonea.utils.CoordinatesCircle;
import pl.salonea.utils.CoordinatesSquare;

import java.util.List;

/**
 * Created by michzio on 02/08/2015.
 */
public interface ServicePointFacadeInterface extends AbstractFacadeInterface<ServicePoint> {

    // concrete interface
    List<ServicePoint> findByProvider(Provider provider);
    List<ServicePoint> findByProvider(Provider provider, Integer start, Integer limit);
    List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode);
    List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit);
    List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84,Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84,Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);
    List<ServicePoint> findByService(Service service);
    List<ServicePoint> findByService(Service service, Integer start, Integer limit);
    List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);
    List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84);
    List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit);
    List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius);
    List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit);
    List<ServicePoint> findByEmployee(Employee employee);
    List<ServicePoint> findByEmployee(Employee employee, Integer start, Integer limit);
    List<ServicePoint> findByProviderService(ProviderService providerService);
    List<ServicePoint> findByProviderService(ProviderService providerService, Integer start, Integer limit);
    List<ServicePoint> findByCorporation(Corporation corporation);
    List<ServicePoint> findByCorporation(Corporation corporation, Integer start, Integer limit);
    List<ServicePoint> findByIndustry(Industry industry);
    List<ServicePoint> findByIndustry(Industry industry, Integer start, Integer limit);
    Integer deleteByProvider(Provider provider);
    // Criteria API query methods interface
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare, Integer start, Integer limit);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle);
    List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle, Integer start, Integer limit);

    @javax.ejb.Local
    interface Local extends ServicePointFacadeInterface { }

    @javax.ejb.Remote
    interface Remote extends ServicePointFacadeInterface { }

}

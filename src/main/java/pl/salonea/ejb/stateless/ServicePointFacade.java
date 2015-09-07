package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServicePointFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.*;
import pl.salonea.entities.*;
import pl.salonea.utils.CoordinatesCircle;
import pl.salonea.utils.CoordinatesSquare;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 03/08/2015.
 */
@Stateless
@LocalBean
public class ServicePointFacade extends AbstractFacade<ServicePoint> implements ServicePointFacadeInterface.Local, ServicePointFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ServicePointFacade() {
        super(ServicePoint.class);
    }

    @Override
    public List<ServicePoint> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ServicePoint> findByProvider(Provider provider, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER, ServicePoint.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode) {

        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_ADDRESS, ServicePoint.class);
        if(city == null) city = "";
        query.setParameter("city", "%" + city + "%");
        if(state == null) state = "";
        query.setParameter("state", "%" + state + "%");
        if(country == null) country = "";
        query.setParameter("country", "%" + country + "%");
        if(street == null) street = "";
        query.setParameter("street", "%" + street + "%");
        if(zipCode == null) zipCode = "";
        query.setParameter("zip_code", "%" + zipCode + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {

        return findByCoordinatesSquare(minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius) {

        return findByCoordinatesCircle(longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByService(Service service) {

        return findByService(service, null, null);
    }

    @Override
    public List<ServicePoint> findByService(Service service, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE, ServicePoint.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {

        return findByProviderAndCoordinatesSquare(provider, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("provider", provider);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByProviderAndCoordinatesCircle(provider, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("provider", provider);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByServiceAndCoordinatesSquare(service, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("service", service);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByServiceAndCoordinatesCircle(service, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("service", service);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<ServicePoint> findByEmployee(Employee employee, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_EMPLOYEE, ServicePoint.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderService(ProviderService providerService) {
        return findByProviderService(providerService, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderService(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_SERVICE, ServicePoint.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCorporation(Corporation corporation) {
        return findByCorporation(corporation, null, null);
    }

    @Override
    public List<ServicePoint> findByCorporation(Corporation corporation, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_CORPORATION, ServicePoint.class);
        query.setParameter("corporation", corporation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByIndustry(Industry industry) {
        return findByIndustry(industry, null, null);
    }

    @Override
    public List<ServicePoint> findByIndustry(Industry industry, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_INDUSTRY, ServicePoint.class);
        query.setParameter("industry", industry);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public Integer deleteByProvider(Provider provider) {

        Query query = getEntityManager().createNamedQuery(ServicePoint.DELETE_BY_PROVIDER);
        query.setParameter("provider", provider);
        return query.executeUpdate();
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories) {
        return findByMultipleCriteria(providers, services, employees, corporations, industries, serviceCategories, null, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address) {
        return findByMultipleCriteria(providers, services, employees, corporations, industries, serviceCategories, address, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare) {
        return findByMultipleCriteria(providers, services, employees, corporations, industries, serviceCategories, null, coordinatesSquare, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle) {
        return findByMultipleCriteria(providers, services, employees, corporations, industries, serviceCategories, null, null, coordinatesCircle);
    }

    private List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, CoordinatesSquare coordinatesSquare, CoordinatesCircle coordinatesCircle) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServicePoint> criteriaQuery = criteriaBuilder.createQuery(ServicePoint.class);
        // FROM
        Root<ServicePoint> servicePoint = criteriaQuery.from(ServicePoint.class);
        // SELECT
        criteriaQuery.select(servicePoint);

        // INNER JOIN-s
        Join<ServicePoint, Provider> provider = null;
        Join<ServicePoint, WorkStation> workStation = null;
        Join<WorkStation, ProviderService> providerService = null;
        Join<ProviderService, Service> service = null;
        Join<WorkStation, TermEmployeeWorkOn> termEmployeeWorkOn = null;
        Join<TermEmployeeWorkOn, Employee> employee = null;

        // WHERE PREDICATES
        List<Predicate> predicates = new ArrayList<>();

        if(providers != null && providers.size() > 0) {

            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);
            predicates.add(provider.in(providers));
        }

        if(services != null && services.size() > 0) {

            if(workStation == null) workStation = servicePoint.join(ServicePoint_.workStations);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);
            predicates.add(service.in(services));
        }

        if(employees != null && employees.size() > 0) {

            if(workStation == null) workStation = servicePoint.join(ServicePoint_.workStations);
            if(termEmployeeWorkOn == null)  termEmployeeWorkOn = workStation.join(WorkStation_.termsEmployeesWorkOn);
            if(employee == null) employee = termEmployeeWorkOn.join(TermEmployeeWorkOn_.employee);

            predicates.add(employee.in(employees));
        }

        if(corporations != null && corporations.size() > 0) {

            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);
            predicates.add(provider.get(Provider_.corporation).in(corporations));
        }

        if(industries != null && industries.size() > 0) {

            if(provider == null) provider = servicePoint.join(ServicePoint_.provider);

            List<Predicate> orPredicates = new ArrayList<>();

            for(Industry industry : industries) {
                orPredicates.add( criteriaBuilder.isMember(industry, provider.get(Provider_.industries)));
            }

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[] { })));

        }

        if(serviceCategories != null && serviceCategories.size() > 0) {

            if(workStation == null) workStation = servicePoint.join(ServicePoint_.workStations);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            if(service == null) service = providerService.join(ProviderService_.service);

            predicates.add( service.get(Service_.serviceCategory).in(serviceCategories) );
        }

        if(address != null) {

            if(coordinatesSquare != null || coordinatesCircle != null) throw new IllegalArgumentException("Address, CoordinatesSquare and CoordinatesCircle arguments are mutually exclusive.");

            Join<ServicePoint, Address> servicePointAddress = servicePoint.join(ServicePoint_.address);

            if(address.getCountry() != null)
                predicates.add(criteriaBuilder.like(servicePointAddress.get(Address_.country), "%" + address.getCountry() + "%"));
            if(address.getState() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.state), "%" + address.getState() + "%"));
            if(address.getCity() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.city), "%" + address.getCity() + "%"));
            if(address.getZipCode() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.zipCode), "%" + address.getZipCode() + "%"));
            if(address.getStreet() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.street), "%" + address.getStreet() + "%"));

        }

        if(coordinatesSquare != null) {

            if(address != null || coordinatesCircle != null) throw new IllegalArgumentException("Address, CoordinatesSquare and CoordinatesCircle arguments are mutually exclusive.");

            predicates.add( criteriaBuilder.greaterThanOrEqualTo(servicePoint.get(ServicePoint_.longitudeWGS84),
                            coordinatesSquare.getMinLongitudeWGS84()) );
            predicates.add( criteriaBuilder.lessThanOrEqualTo(servicePoint.get(ServicePoint_.longitudeWGS84),
                            coordinatesSquare.getMaxLongitudeWGS84()) );

            predicates.add( criteriaBuilder.greaterThanOrEqualTo(servicePoint.get(ServicePoint_.latitudeWGS84),
                            coordinatesSquare.getMinLatitudeWGS84()) );
            predicates.add ( criteriaBuilder.lessThanOrEqualTo(servicePoint.get(ServicePoint_.latitudeWGS84),
                            coordinatesSquare.getMaxLatitudeWGS84()) );
        }

        if(coordinatesCircle != null) {

            if(address != null || coordinatesSquare != null) throw new IllegalArgumentException("Address, CoordinatesSquare and CoordinatesCircle arguments are mutually exclusive.");

            //  SQRT((sp.longitudeWGS84 - :longitude_wgs84)*(sp.longitudeWGS84 - :longitude_wgs84)
            //        + (sp.latitudeWGS84 - :latitude_wgs84)*(sp.latitudeWGS84 - :latitude_wgs84)) < :radius
            Expression<Float> longitudeDifference = criteriaBuilder.diff(servicePoint.get(ServicePoint_.longitudeWGS84), coordinatesCircle.getLongitudeWGS84());
            Expression<Float> longitudeDifferenceSquared = criteriaBuilder.prod(longitudeDifference, longitudeDifference);
            Expression<Float> latitudeDifference = criteriaBuilder.diff(servicePoint.get(ServicePoint_.latitudeWGS84), coordinatesCircle.getLatitudeWGS84());
            Expression<Float> latitudeDifferenceSquared = criteriaBuilder.prod(latitudeDifference, latitudeDifference);
            Expression<Double> calculatedRadius = criteriaBuilder.sqrt(criteriaBuilder.sum(longitudeDifferenceSquared, latitudeDifferenceSquared));

            predicates.add( criteriaBuilder.lessThanOrEqualTo( calculatedRadius, coordinatesCircle.getRadius() ));
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));
        TypedQuery<ServicePoint> query = getEntityManager().createQuery(criteriaQuery);

        return query.getResultList();
    }


}

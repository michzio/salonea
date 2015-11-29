package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServicePointFacadeInterface;
import pl.salonea.embeddables.Address;
import pl.salonea.embeddables.*;
import pl.salonea.entities.*;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.utils.CoordinatesCircle;
import pl.salonea.utils.CoordinatesSquare;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.*;
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
    public ServicePoint createForProvider(Long providerId, ServicePoint servicePoint) {

        Provider foundProvider = getEntityManager().find(Provider.class, providerId);
        servicePoint.setProvider(foundProvider);

        return create(servicePoint);
    }

    @Override
    public ServicePoint update(ServicePointId servicePointId, ServicePoint servicePoint) {

        Provider foundProvider = getEntityManager().find(Provider.class, servicePointId.getProvider());
        servicePoint.setProvider(foundProvider);
        servicePoint.setServicePointNumber(servicePointId.getServicePointNumber());
        
        return update(servicePoint);
    }

    @Override
    public List<ServicePoint> find(List<Object> servicePointIds) {

        if(servicePointIds == null || servicePointIds.size() == 0)
            throw new IllegalArgumentException("The servicePointIds argument must be not empty list.");

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServicePoint> criteriaQuery = criteriaBuilder.createQuery(ServicePoint.class);
        // FROM
        Root<ServicePoint> servicePoint = criteriaQuery.from(ServicePoint.class);
        // SELECT
        criteriaQuery.select(servicePoint);

        // INNER JOIN
        Join<ServicePoint, Provider> provider = servicePoint.join(ServicePoint_.provider);

        // WHERE PREDICATES on composite primary keys
        List<Predicate> orPredicates = new ArrayList<>();

        for( Object object : servicePointIds ) {

            if( !(object instanceof ServicePointId) )
                throw new IllegalArgumentException("The servicePointIds argument should be list of ServicePointId typed objects.");

            ServicePointId servicePointId = (ServicePointId) object;

            Predicate[] andPredicates = new Predicate[2];
            andPredicates[0] = criteriaBuilder.equal( provider.get(Provider_.userId), servicePointId.getProvider() );
            andPredicates[1] = criteriaBuilder.equal( servicePoint.get(ServicePoint_.servicePointNumber), servicePointId.getServicePointNumber() );

            orPredicates.add( criteriaBuilder.and(andPredicates) );
        }

        // WHERE compositePK1 OR compositePK2 OR ... OR compositePKN
        criteriaQuery.where( criteriaBuilder.or(orPredicates.toArray(new Predicate[] {})) );

        TypedQuery<ServicePoint> query = getEntityManager().createQuery(criteriaQuery);
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findAllEagerly() {
        return findAllEagerly(null, null);
    }

    @Override
    public List<ServicePoint> findAllEagerly(Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_ALL_EAGERLY, ServicePoint.class);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public ServicePoint findByIdEagerly(ServicePointId servicePointId) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_ID_EAGERLY, ServicePoint.class);
        query.setParameter("userId", servicePointId.getProvider());
        query.setParameter("servicePointNumber", servicePointId.getServicePointNumber());
        try {
            return query.getSingleResult();
        } catch (NoResultException | NonUniqueResultException ex) {
            return null;
        }
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
    public List<ServicePoint> findByProviderEagerly(Provider provider) {
        return findByProviderEagerly(provider, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderEagerly(Provider provider, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_EAGERLY, ServicePoint.class);
        query.setParameter("provider", provider);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderAndAddress(Provider provider, String city, String state, String country, String street, String zipCode) {
        return findByProviderAndAddress(provider, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderAndAddress(Provider provider, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_AND_ADDRESS, ServicePoint.class);
        query.setParameter("provider", provider);
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
    public List<ServicePoint> findByServiceEagerly(Service service) {
        return findByServiceEagerly(service, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceEagerly(Service service, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_EAGERLY, ServicePoint.class);
        query.setParameter("service", service);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByServiceAndAddress(Service service, String city, String state, String country, String street, String zipCode) {
        return findByServiceAndAddress(service, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceAndAddress(Service service, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_AND_ADDRESS, ServicePoint.class);
        query.setParameter("service", service);
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
    public List<ServicePoint> findByEmployeeEagerly(Employee employee) {
        return findByEmployeeEagerly(employee, null, null);
    }

    @Override
    public List<ServicePoint> findByEmployeeEagerly(Employee employee, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_EMPLOYEE_EAGERLY, ServicePoint.class);
        query.setParameter("employee", employee);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByEmployeeAndAddress(Employee employee, String city, String state, String country, String street, String zipCode) {
        return findByEmployeeAndAddress(employee, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByEmployeeAndAddress(Employee employee, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_EMPLOYEE_AND_ADDRESS, ServicePoint.class);
        query.setParameter("employee", employee);
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
    public List<ServicePoint> findByEmployeeAndCoordinatesSquare(Employee employee, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByEmployeeAndCoordinatesSquare(employee, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByEmployeeAndCoordinatesSquare(Employee employee, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_EMPLOYEE_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("employee", employee);
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
    public List<ServicePoint> findByEmployeeAndCoordinatesCircle(Employee employee, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByEmployeeAndCoordinatesCircle(employee, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByEmployeeAndCoordinatesCircle(Employee employee, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_EMPLOYEE_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("employee", employee);
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
    public List<ServicePoint> findByProviderServiceEagerly(ProviderService providerService) {
        return findByProviderServiceEagerly(providerService, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderServiceEagerly(ProviderService providerService, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_SERVICE_EAGERLY, ServicePoint.class);
        query.setParameter("provider_service", providerService);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderServiceAndAddress(ProviderService providerService, String city, String state, String country, String street, String zipCode) {
        return findByProviderServiceAndAddress(providerService, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderServiceAndAddress(ProviderService providerService, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_SERVICE_AND_ADDRESS, ServicePoint.class);
        query.setParameter("provider_service", providerService);
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
    public List<ServicePoint> findByProviderServiceAndCoordinatesSquare(ProviderService providerService, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByProviderServiceAndCoordinatesSquare(providerService, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderServiceAndCoordinatesSquare(ProviderService providerService, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_SERVICE_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("provider_service", providerService);
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
    public List<ServicePoint> findByProviderServiceAndCoordinatesCircle(ProviderService providerService, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByProviderServiceAndCoordinatesCircle(providerService, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderServiceAndCoordinatesCircle(ProviderService providerService, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_SERVICE_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("provider_service", providerService);
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
    public List<ServicePoint> findByCorporationEagerly(Corporation corporation) {
        return findByCorporationEagerly(corporation, null, null);
    }

    @Override
    public List<ServicePoint> findByCorporationEagerly(Corporation corporation, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_CORPORATION_EAGERLY, ServicePoint.class);
        query.setParameter("corporation", corporation);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCorporationAndAddress(Corporation corporation, String city, String state, String country, String street, String zipCode) {
        return findByCorporationAndAddress(corporation, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByCorporationAndAddress(Corporation corporation, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_CORPORATION_AND_ADDRESS, ServicePoint.class);
        query.setParameter("corporation", corporation);
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
    public List<ServicePoint> findByCorporationAndCoordinatesSquare(Corporation corporation, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByCorporationAndCoordinatesSquare(corporation, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByCorporationAndCoordinatesSquare(Corporation corporation, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_CORPORATION_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("corporation", corporation);
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
    public List<ServicePoint> findByCorporationAndCoordinatesCircle(Corporation corporation, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByCorporationAndCoordinatesCircle(corporation, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByCorporationAndCoordinatesCircle(Corporation corporation, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_CORPORATION_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("corporation", corporation);
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
    public List<ServicePoint> findByIndustryEagerly(Industry industry) {
        return findByIndustryEagerly(industry, null, null);
    }

    @Override
    public List<ServicePoint> findByIndustryEagerly(Industry industry, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_INDUSTRY_EAGERLY, ServicePoint.class);
        query.setParameter("industry", industry);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByIndustryAndAddress(Industry industry, String city, String state, String country, String street, String zipCode) {
        return findByIndustryAndAddress(industry, city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByIndustryAndAddress(Industry industry, String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_INDUSTRY_AND_ADDRESS, ServicePoint.class);
        query.setParameter("industry", industry);
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
    public List<ServicePoint> findByIndustryAndCoordinatesSquare(Industry industry, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByIndustryAndCoordinatesSquare(industry, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByIndustryAndCoordinatesSquare(Industry industry, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_INDUSTRY_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("industry", industry);
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
    public List<ServicePoint> findByIndustryAndCoordinatesCircle(Industry industry, Float longitudeWGS84, Float latitudeWGS84, Double radius) {
        return findByIndustryAndCoordinatesCircle(industry, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByIndustryAndCoordinatesCircle(Industry industry, Float longitudeWGS84, Float latitudeWGS84, Double radius, Integer start, Integer limit) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_INDUSTRY_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("industry", industry);
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
    public Long countByProvider(Provider provider) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_PROVIDER, Long.class);
        query.setParameter("provider", provider);
        return query.getSingleResult();
    }

    @Override
    public Long countByService(Service service) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_SERVICE, Long.class);
        query.setParameter("service", service);
        return query.getSingleResult();
    }

    @Override
    public Long countByEmployee(Employee employee) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_EMPLOYEE, Long.class);
        query.setParameter("employee", employee);
        return query.getSingleResult();
    }

    @Override
    public Long countByProviderService(ProviderService providerService) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_PROVIDER_SERVICE, Long.class);
        query.setParameter("provider_service", providerService);
        return query.getSingleResult();
    }

    @Override
    public Long countByCorporation(Corporation corporation) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_CORPORATION, Long.class);
        query.setParameter("corporation", corporation);
        return query.getSingleResult();
    }

    @Override
    public Long countByIndustry(Industry industry) {

        TypedQuery<Long> query = getEntityManager().createNamedQuery(ServicePoint.COUNT_BY_INDUSTRY, Long.class);
        query.setParameter("industry", industry);
        return query.getSingleResult();
    }

    @Override
    public Integer deleteByProvider(Provider provider) {

        Query query = getEntityManager().createNamedQuery(ServicePoint.DELETE_BY_PROVIDER);
        query.setParameter("provider", provider);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteById(ServicePointId servicePointId) {

        Query query = getEntityManager().createNamedQuery(ServicePoint.DELETE_BY_ID);
        query.setParameter("userId", servicePointId.getProvider());
        query.setParameter("servicePointNumber", servicePointId.getServicePointNumber());
        return query.executeUpdate();
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices,  List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null, null, false, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, address, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, address, null, null, false, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, coordinatesSquare, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, coordinatesSquare, null, false, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, coordinatesCircle, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null, coordinatesCircle, false, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories) {
        return findByMultipleCriteriaEagerly(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null, null, true, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address) {
        return findByMultipleCriteriaEagerly(providers, services, providerServices, employees, corporations, industries, serviceCategories, address, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, address, null, null, true, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare) {
        return findByMultipleCriteriaEagerly(providers, services, providerServices, employees, corporations, industries, serviceCategories, coordinatesSquare, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesSquare coordinatesSquare, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, coordinatesSquare, null, true, start, limit);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle) {
        return findByMultipleCriteriaEagerly(providers, services, providerServices, employees, corporations, industries, serviceCategories, coordinatesCircle, null, null);
    }

    @Override
    public List<ServicePoint> findByMultipleCriteriaEagerly(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, CoordinatesCircle coordinatesCircle, Integer start, Integer limit) {
        return findByMultipleCriteria(providers, services, providerServices, employees, corporations, industries, serviceCategories, null, null, coordinatesCircle, true, start, limit);
    }

    private List<ServicePoint> findByMultipleCriteria(List<Provider> providers, List<Service> services, List<ProviderService> providerServices, List<Employee> employees, List<Corporation> corporations, List<Industry> industries, List<ServiceCategory> serviceCategories, Address address, CoordinatesSquare coordinatesSquare, CoordinatesCircle coordinatesCircle, Boolean eagerly, Integer start, Integer limit) {

        CriteriaBuilder criteriaBuilder = getEntityManager().getCriteriaBuilder();
        CriteriaQuery<ServicePoint> criteriaQuery = criteriaBuilder.createQuery(ServicePoint.class);
        // FROM
        Root<ServicePoint> servicePoint = criteriaQuery.from(ServicePoint.class);
        // SELECT
        criteriaQuery.select(servicePoint).distinct(true);

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

        if(providerServices != null && providerServices.size() > 0) {

            if(workStation == null) workStation = servicePoint.join(ServicePoint_.workStations);
            if(providerService == null) providerService = workStation.join(WorkStation_.providedServices);
            predicates.add(providerService.in(providerServices));
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

            predicates.add( criteriaBuilder.or(orPredicates.toArray(new Predicate[]{})));

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
            if(address.getHouseNumber() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.houseNumber), "%" + address.getHouseNumber() + "%"));
            if(address.getFlatNumber() != null)
                predicates.add( criteriaBuilder.like(servicePointAddress.get(Address_.flatNumber), "%" + address.getFlatNumber() + "%"));

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

        if(eagerly) {
            // then (left) fetch associated collections of entities

            if(workStation != null) {
                servicePoint.fetch("workStations", JoinType.INNER);
            } else {
                servicePoint.fetch("workStations", JoinType.LEFT);
            }

            servicePoint.fetch("photos", JoinType.LEFT);
            servicePoint.fetch("virtualTours", JoinType.LEFT);
        }

        // WHERE predicate1 AND predicate2 AND ... AND predicateN
        criteriaQuery.where(predicates.toArray(new Predicate[] { }));
        TypedQuery<ServicePoint> query = getEntityManager().createQuery(criteriaQuery);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }

        return query.getResultList();
    }


}

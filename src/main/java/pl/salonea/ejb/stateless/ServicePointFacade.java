package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ServicePointFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.lang.reflect.Type;
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
    public List<ServicePoint> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER, ServicePoint.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode) {

        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<ServicePoint> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_ADDRESS, ServicePoint.class);
        query.setParameter("city", "%" + city + "%");
        query.setParameter("state", "%" + state + "%");
        query.setParameter("country", "%" + country + "%");
        query.setParameter("street", "%" + street + "%");
        query.setParameter("zip_code", "%" + zipCode + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {

        return findByCoordinatesSquare(minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByCoordinatesSquare(Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Float radius) {

        return findByCoordinatesCircle(longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByCoordinatesCircle(Float longitudeWGS84, Float latitudeWGS84, Float radius, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByService(Service service) {

        return findByService(service, null, null);
    }

    @Override
    public List<ServicePoint> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE, ServicePoint.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {

        return findByProviderAndCoordinatesSquare(provider, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesSquare(Provider provider, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("provider", provider);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Float radius) {
        return findByProviderAndCoordinatesCircle(provider, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByProviderAndCoordinatesCircle(Provider provider, Float longitudeWGS84, Float latitudeWGS84, Float radius, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("provider", provider);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84) {
        return findByServiceAndCoordinatesSquare(service, minLongitudeWGS84, minLatitudeWGS84, maxLongitudeWGS84, maxLatitudeWGS84, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesSquare(Service service, Float minLongitudeWGS84, Float minLatitudeWGS84, Float maxLongitudeWGS84, Float maxLatitudeWGS84, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_SQUARE, ServicePoint.class);
        query.setParameter("service", service);
        query.setParameter("min_longitude_wgs84", minLongitudeWGS84);
        query.setParameter("min_latitude_wgs84", minLatitudeWGS84);
        query.setParameter("max_longitude_wgs84", maxLongitudeWGS84);
        query.setParameter("max_latitude_wgs84", maxLatitudeWGS84);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Float radius) {
        return findByServiceAndCoordinatesCircle(service, longitudeWGS84, latitudeWGS84, radius, null, null);
    }

    @Override
    public List<ServicePoint> findByServiceAndCoordinatesCircle(Service service, Float longitudeWGS84, Float latitudeWGS84, Float radius, Integer start, Integer offset) {

        TypedQuery<ServicePoint> query = getEntityManager().createNamedQuery(ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_CIRCLE, ServicePoint.class);
        query.setParameter("service", service);
        query.setParameter("longitude_wgs84", longitudeWGS84);
        query.setParameter("latitude_wgs84", latitudeWGS84);
        query.setParameter("radius", radius);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ServicePoint> findByEmployee(Employee employee) {
        return null;
    }

    @Override
    public List<ServicePoint> findByEmployee(Employee employee, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<ServicePoint> findByProviderService(ProviderService providerService) {
        return null;
    }

    @Override
    public List<ServicePoint> findByProviderService(ProviderService providerService, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<ServicePoint> findByCorporation(Corporation corporation) {
        return null;
    }

    @Override
    public List<ServicePoint> findByCorporation(Corporation corporation, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<ServicePoint> findByIndustry(Industry industry) {
        return null;
    }

    @Override
    public List<ServicePoint> findByIndustry(Industry industry, Integer start, Integer offset) {
        return null;
    }
}

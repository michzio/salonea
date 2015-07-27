package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderServiceFacadeInterface;
import pl.salonea.entities.*;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 25/07/2015.
 */
@Stateless
@LocalBean
public class ProviderServiceFacade extends AbstractFacade<ProviderService> implements ProviderServiceFacadeInterface.Local, ProviderServiceFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProviderServiceFacade() {
        super(ProviderService.class);
    }


    @Override
    public List<ProviderService> findByProvider(Provider provider) {
        return findByProvider(provider, null, null);
    }

    @Override
    public List<ProviderService> findByProvider(Provider provider, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER, ProviderService.class);
        query.setParameter("provider", provider);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByService(Service service) {
        return findByService(service);
    }

    @Override
    public List<ProviderService> findByService(Service service, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE, ProviderService.class);
        query.setParameter("service", service);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory) {
        return findByServiceCategory(serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByServiceCategory(ServiceCategory serviceCategory, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("service_category", serviceCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory) {
        return findByProviderAndServiceCategory(provider, serviceCategory, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_SERVICE_CATEGORY, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByDescription(String description) {
        return findByDescription(description, null, null);
    }

    @Override
    public List<ProviderService> findByDescription(String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_DESCRIPTION, ProviderService.class);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description) {
        return findByProviderAndDescription(provider, description, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDescription(Provider provider, String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description) {
        return findByServiceAndDescription(service, description, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDescription(Service service, String description, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DESCRIPTION, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("description", "%" + description + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice) {
        return findByServiceAndDiscountedPrice(service, minPrice, maxPrice, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscountedPrice(Service service, Double minPrice, Double maxPrice, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNTED_PRICE, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_price", minPrice);
        query.setParameter("max_price", maxPrice);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount) {
        return findByServiceAndDiscount(service, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByServiceAndDiscount(Service service, Short minDiscount, Short maxDiscount, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_SERVICE_AND_DISCOUNT, ProviderService.class);
        query.setParameter("service", service);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount) {
        return findByProviderAndDiscount(provider, minDiscount, maxDiscount, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndDiscount(Provider provider, Short minDiscount, Short maxDiscount, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_DISCOUNT, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("min_discount", minDiscount);
        query.setParameter("max_discount", maxDiscount);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation) {
        return findByWorkStation(workStation);
    }

    @Override
    public List<ProviderService> findByWorkStation(WorkStation workStation, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_WORK_STATION, ProviderService.class);
        query.setParameter("work_station", workStation);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee) {
        return findByEmployee(employee, null, null);
    }

    @Override
    public List<ProviderService> findByEmployee(Employee employee, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_EMPLOYEE, ProviderService.class);
        query.setParameter("employee",employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee) {
        return findByProviderAndEmployee(provider, employee, null, null);
    }

    @Override
    public List<ProviderService> findByProviderAndEmployee(Provider provider, Employee employee, Integer start, Integer offset) {

        TypedQuery<ProviderService> query = getEntityManager().createNamedQuery(ProviderService.FIND_BY_PROVIDER_AND_EMPLOYEE, ProviderService.class);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Integer updateDiscountForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_SERVICE_CATEGORY);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
    }

    @Override
    public Integer updateDiscountForProviderAndEmployee(Provider provider, Employee employee, Short newDiscount) {

        Query query = getEntityManager().createNamedQuery(ProviderService.UPDATE_DISCOUNT_FOR_PROVIDER_AND_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        query.setParameter("new_discount", newDiscount);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForOnlyWorkStation(WorkStation workStation) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_ONLY_WORK_STATION);
        query.setParameter("work_station", workStation);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProviderAndOnlyEmployee(Provider provider, Employee employee) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER_AND_ONLY_EMPLOYEE);
        query.setParameter("provider", provider);
        query.setParameter("employee", employee);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProviderAndServiceCategory(Provider provider, ServiceCategory serviceCategory) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER_AND_SERVICE_CATEGORY);
        query.setParameter("provider", provider);
        query.setParameter("service_category", serviceCategory);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForProvider(Provider provider) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_PROVIDER);
        query.setParameter("provider", provider);
        return query.executeUpdate();
    }

    @Override
    public Integer deleteForService(Service service) {

        Query query = getEntityManager().createNamedQuery(ProviderService.DELETE_FOR_SERVICE);
        query.setParameter("service", service);
        return query.executeUpdate();
    }
}
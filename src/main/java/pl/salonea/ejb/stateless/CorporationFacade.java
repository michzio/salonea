package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.CorporationFacadeInterface;
import pl.salonea.entities.Corporation;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.Date;
import java.util.List;

/**
 * Created by michzio on 22/07/2015.
 */
@Stateless
@LocalBean
public class CorporationFacade extends AbstractFacade<Corporation> implements CorporationFacadeInterface.Local, CorporationFacadeInterface.Remote {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public CorporationFacade() {
        super(Corporation.class);
    }

    @Override
    public List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode) {
        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Corporation> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_ADDRESS, Corporation.class);
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
    public List<Corporation> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Corporation> findByName(String name, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_BY_NAME, Corporation.class);
        query.setParameter("name", "%" + name + "%");
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findOpenAfter(Date date) {
        return findOpenAfter(date, null, null);
    }

    @Override
    public List<Corporation> findOpenAfter(Date date, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_OPEN_AFTER, Corporation.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }

    @Override
    public List<Corporation> findOpenBefore(Date date) {
        return findOpenBefore(date, null, null);
    }

    @Override
    public List<Corporation> findOpenBefore(Date date, Integer start, Integer limit) {

        TypedQuery<Corporation> query = getEntityManager().createNamedQuery(Corporation.FIND_OPEN_BEFORE, Corporation.class);
        query.setParameter("date", date);
        if(start != null && limit != null) {
            query.setFirstResult(start);
            query.setMaxResults(limit);
        }
        return query.getResultList();
    }
}
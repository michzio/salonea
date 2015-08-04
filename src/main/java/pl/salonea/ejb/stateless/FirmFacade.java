package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.FirmFacadeInterface;
import pl.salonea.entities.Firm;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.List;

/**
 * Created by michzio on 17/07/2015.
 */
@Stateless
@LocalBean
public class FirmFacade extends AbstractFacade<Firm> implements FirmFacadeInterface.Remote, FirmFacadeInterface.Local  {

    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public FirmFacade() {
        super(Firm.class);
    }

    @Override
    public List<Firm> findByName(String name) {
        return findByName(name, null, null);
    }

    @Override
    public List<Firm> findByName(String name, Integer start, Integer offset) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_NAME, Firm.class);
        query.setParameter("firm_name", "%" + name + "%");
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }

    @Override
    public Firm findByVATIN(String vatin) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_VATIN, Firm.class);
        query.setParameter("vatin", vatin);
        return query.getSingleResult();
    }

    @Override
    public Firm findByCompanyNumber(String companyNumber) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_COMPANY_NUMBER, Firm.class);
        query.setParameter("company_number", companyNumber);
        return query.getSingleResult();
    }

    @Override
    public Boolean deleteWithVATIN(String vatin) {

        Query query = getEntityManager().createNamedQuery(Firm.DELETE_WITH_VATIN);
        query.setParameter("vatin", vatin);
        if(query.executeUpdate() == 1)
            return true;

        return false;
    }

    @Override
    public Boolean deleteWithCompanyNumber(String companyNumber) {

        Query query = getEntityManager().createNamedQuery(Firm.DELETE_WITH_COMPANY_NUMBER);
        query.setParameter("company_number", companyNumber);
        if(query.executeUpdate() == 1)
            return true;

        return false;
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode) {
        return findByAddress(city, state, country, street, zipCode, null, null);
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {

        TypedQuery<Firm> query = getEntityManager().createNamedQuery(Firm.FIND_BY_ADDRESS, Firm.class);
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
        if(start != null && offset != null) {
            query.setFirstResult(start);
            query.setMaxResults(offset);
        }
        return query.getResultList();
    }
}
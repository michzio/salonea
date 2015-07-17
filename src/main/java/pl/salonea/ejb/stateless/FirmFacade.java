package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.FirmFacadeInterface;
import pl.salonea.entities.Firm;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by michzio on 17/07/2015.
 */
@Stateless
@LocalBean
public class FirmFacade extends AbstractFacade<Firm> implements FirmFacadeInterface.Remote, FirmFacadeInterface.Local  {

    @Inject
    EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public FirmFacade() {
        super(Firm.class);
    }

    @Override
    public List<Firm> findByName(String name) {
        return null;
    }

    @Override
    public Firm findByVATIN(String vatin) {
        return null;
    }

    @Override
    public Firm findByCompanyNumber(String companyNumber) {
        return null;
    }

    @Override
    public Boolean deleteWithVATIN(String vatin) {
        return null;
    }

    @Override
    public Boolean deleteWithCompanyNumber(String companyNumber) {
        return null;
    }

    @Override
    public List<Firm> findByName(String name, Integer start, Integer offset) {
        return null;
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode) {
        return null;
    }

    @Override
    public List<Firm> findByAddress(String city, String state, String country, String street, String zipCode, Integer start, Integer offset) {
        return null;
    }
}

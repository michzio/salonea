package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderFacadeInterface;
import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
@Stateless
@LocalBean
public class ProviderFacade extends AbstractFacade<Provider> implements ProviderFacadeInterface.Locale, ProviderFacadeInterface.Remote {


    @Inject
    private EntityManager em;

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    public ProviderFacade() { super(Provider.class); }

    @Override
    public List<Provider> findByCorporation(Corporation corporation) {
        return null;
    }

    @Override
    public List<Provider> findByType(ProviderType providerType) {
        return null;
    }

    @Override
    public List<Provider> findByIndustry(Industry industry) {
        return null;
    }

    @Override
    public List<Provider> findByPaymentMethod(PaymentMethod paymentMethod) {
        return null;
    }

    @Override
    public List<Provider> findBySuppliedService(Service service) {
        return null;
    }

    @Override
    public List<Provider> findRated() {
        return null;
    }

    @Override
    public List<Provider> findUnrated() {
        return null;
    }

    @Override
    public List<Provider> findOnAvgRatedAbove(Double minAvgRating) {
        return null;
    }

    @Override
    public List<Provider> findOnAvgRatedBelow(Double maxAvgRating) {
        return null;
    }

    @Override
    public List<Provider> findRatedByClient(Client client) {
        return null;
    }
}

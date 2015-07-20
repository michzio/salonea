package pl.salonea.ejb.interfaces;


import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;

import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
public interface ProviderFacadeInterface {

    // concrete interface
    List<Provider> findByCorporation(Corporation corporation);
    List<Provider> findByType(ProviderType providerType);
    List<Provider> findByIndustry(Industry industry);
    List<Provider> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Provider> findBySuppliedService(Service service);
    List<Provider> findRated();
    List<Provider> findUnrated();
    List<Provider> findOnAvgRatedAbove(Double minAvgRating);
    List<Provider> findOnAvgRatedBelow(Double maxAvgRating);
    List<Provider> findRatedByClient(Client client);

    @javax.ejb.Remote
    interface Remote extends ProviderFacadeInterface { }

    @javax.ejb.Local
    interface Locale extends ProviderFacadeInterface { }
}

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
    List<Provider> findByCorporation(Corporation corporation, Integer start, Integer offset);
    List<Provider> findByType(ProviderType providerType);
    List<Provider> findByType(ProviderType providerType, Integer start, Integer offset);
    List<Provider> findByIndustry(Industry industry);
    List<Provider> findByIndustry(Industry industry, Integer start, Integer offset);
    List<Provider> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Provider> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer offset);
    List<Provider> findBySuppliedService(Service service);
    List<Provider> findBySuppliedService(Service service, Integer start, Integer offset);
    List<Provider> findRated();
    List<Provider> findRated(Integer start, Integer offset);
    List<Provider> findUnrated();
    List<Provider> findUnrated(Integer start, Integer offset);
    List<Provider> findOnAvgRatedAbove(Double minAvgRating);
    List<Provider> findOnAvgRatedAbove(Double minAvgRating, Integer start, Integer offset);
    List<Provider> findOnAvgRatedBelow(Double maxAvgRating);
    List<Provider> findOnAvgRatedBelow(Double maxAvgRating, Integer start, Integer offset);
    List<Provider> findRatedByClient(Client client);
    List<Provider> findRatedByClient(Client client, Integer start, Integer offset);

    @javax.ejb.Remote
    interface Remote extends ProviderFacadeInterface { }

    @javax.ejb.Local
    interface Locale extends ProviderFacadeInterface { }
}

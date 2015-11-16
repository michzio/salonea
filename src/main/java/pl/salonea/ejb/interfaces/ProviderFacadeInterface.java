package pl.salonea.ejb.interfaces;


import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;

import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
public interface ProviderFacadeInterface extends AbstractFacadeInterface<Provider> {

    // concrete interface
    Provider update(Provider provider, Boolean retainTransientFields);
    List<Provider> findAllEagerly();
    List<Provider> findAllEagerly(Integer start, Integer limit);
    Provider findByIdEagerly(Long providerId);
    List<Provider> findByCorporation(Corporation corporation);
    List<Provider> findByCorporation(Corporation corporation, Integer start, Integer limit);
    List<Provider> findByCorporationEagerly(Corporation corporation);
    List<Provider> findByCorporationEagerly(Corporation corporation, Integer start, Integer limit);
    List<Provider> findByType(ProviderType providerType);
    List<Provider> findByType(ProviderType providerType, Integer start, Integer limit);
    List<Provider> findByIndustry(Industry industry);
    List<Provider> findByIndustry(Industry industry, Integer start, Integer limit);
    List<Provider> findByIndustryEagerly(Industry industry);
    List<Provider> findByIndustryEagerly(Industry industry, Integer start, Integer limit);
    List<Provider> findByPaymentMethod(PaymentMethod paymentMethod);
    List<Provider> findByPaymentMethod(PaymentMethod paymentMethod, Integer start, Integer limit);
    List<Provider> findBySuppliedService(Service service);
    List<Provider> findBySuppliedService(Service service, Integer start, Integer limit);
    List<Provider> findRated();
    List<Provider> findRated(Integer start, Integer limit);
    List<Provider> findUnrated();
    List<Provider> findUnrated(Integer start, Integer limit);
    List<Provider> findOnAvgRatedAbove(Double minAvgRating);
    List<Provider> findOnAvgRatedAbove(Double minAvgRating, Integer start, Integer limit);
    List<Provider> findOnAvgRatedBelow(Double maxAvgRating);
    List<Provider> findOnAvgRatedBelow(Double maxAvgRating, Integer start, Integer limit);
    List<Provider> findRatedByClient(Client client);
    List<Provider> findRatedByClient(Client client, Integer start, Integer limit);
    List<Provider> findRatedByClientEagerly(Client client);
    List<Provider> findRatedByClientEagerly(Client client, Integer start, Integer limit);
    List<Provider> findByMultipleCriteria(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods,
                                          List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description);
    List<Provider> findByMultipleCriteria(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods,
                                          List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description, Integer start, Integer limit);
    List<Provider> findByMultipleCriteriaEagerly(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods,
                                                 List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description);
    List<Provider> findByMultipleCriteriaEagerly(List<Corporation> corporations, List<ProviderType> types, List<Industry> industries, List<PaymentMethod> paymentMethods,
                                                 List<Service> services, Boolean rated, Double minAvgRating, Double maxAvgRating, List<Client> clients, String providerName, String description, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends ProviderFacadeInterface { }

    @javax.ejb.Local
    interface Local extends ProviderFacadeInterface { }
}

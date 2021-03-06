package pl.salonea.ejb.interfaces;

        import pl.salonea.entities.Client;
        import pl.salonea.entities.Provider;
        import pl.salonea.entities.ProviderRating;
        import pl.salonea.entities.idclass.ProviderRatingId;

        import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
public interface ProviderRatingFacadeInterface extends AbstractFacadeInterface<ProviderRating> {

    // concrete interface
    ProviderRating createForProviderAndClient(Long providerId, Long clientId, ProviderRating providerRating);
    ProviderRating update(ProviderRatingId providerRatingId, ProviderRating providerRating);
    List<ProviderRating> findByClient(Client client);
    List<ProviderRating> findByClient(Client client, Integer start, Integer limit);
    List<ProviderRating> findByProvider(Provider provider);
    List<ProviderRating> findByProvider(Provider provider, Integer start, Integer limit);
    List<ProviderRating> findForProviderByRating(Provider provider, Short rating);
    List<ProviderRating> findForProviderByRating(Provider provider, Short rating, Integer start, Integer limit);
    List<ProviderRating> findForProviderAboveRating(Provider provider, Short minRating);
    List<ProviderRating> findForProviderAboveRating(Provider provider, Short minRating, Integer start, Integer limit);
    List<ProviderRating> findForProviderBelowRating(Provider provider, Short maxRating);
    List<ProviderRating> findForProviderBelowRating(Provider provider, Short maxRating, Integer start, Integer limit);
    List<ProviderRating> findFromClientByRating(Client client, Short rating);
    List<ProviderRating> findFromClientByRating(Client client, Short rating, Integer start, Integer limit);
    List<ProviderRating> findFromClientAboveRating(Client client, Short minRating);
    List<ProviderRating> findFromClientAboveRating(Client client, Short minRating, Integer start, Integer limit);
    List<ProviderRating> findFromClientBelowRating(Client client, Short maxRating);
    List<ProviderRating> findFromClientBelowRating(Client client, Short maxRating, Integer start, Integer limit);
    Double findProviderAvgRating(Provider provider);
    Long countProviderRatings(Provider provider);
    Long countClientRatings(Client client);
    Integer deleteByClient(Client client);
    Integer deleteByProvider(Provider provider);
    Integer deleteById(ProviderRatingId providerRatingId);
    List<ProviderRating> findByMultipleCriteria(List<Client> clients, List<Provider> providers, Short minRating, Short maxRating, Short exactRating, String clientComment, String providerDementi);
    List<ProviderRating> findByMultipleCriteria(List<Client> clients, List<Provider> providers, Short minRating, Short maxRating, Short exactRating, String clientComment, String providerDementi, Integer start, Integer limit);

    @javax.ejb.Remote
    interface Remote extends ProviderRatingFacadeInterface { }

    @javax.ejb.Local
    interface Local extends ProviderRatingFacadeInterface { }
}

package pl.salonea.ejb.interfaces;

        import pl.salonea.entities.Client;
        import pl.salonea.entities.Provider;
        import pl.salonea.entities.ProviderRating;
        import java.util.List;

/**
 * Created by michzio on 20/07/2015.
 */
public interface ProviderRatingFacadeInterface extends AbstractFacadeInterface<ProviderRating> {

    // concrete interface
    List<ProviderRating> findByClient(Client client);
    List<ProviderRating> findByClient(Client client, Integer start, Integer offset);
    List<ProviderRating> findByProvider(Provider provider);
    List<ProviderRating> findByProvider(Provider provider, Integer start, Integer offset);
    List<ProviderRating> findForProviderByRating(Provider provider, Short rating);
    List<ProviderRating> findForProviderByRating(Provider provider, Short rating, Integer start, Integer offset);
    List<ProviderRating> findFromClientByRating(Client client, Short rating);
    List<ProviderRating> findFromClientByRating(Client client, Short rating, Integer start, Integer offset);
    Double findProviderAvgRating(Provider provider);
    Integer countProviderRatings(Provider provider);
    Integer countClientRatings(Client client);


    @javax.ejb.Remote
    interface Remote extends ProviderRatingFacadeInterface { }

    @javax.ejb.Local
    interface Local extends ProviderRatingFacadeInterface { }
}

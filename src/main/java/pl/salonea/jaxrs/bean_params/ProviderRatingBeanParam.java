package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ClientFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Client;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 08/10/2015.
 */
public class ProviderRatingBeanParam extends RatingBeanParam {

    private @QueryParam("clientId") List<Long> clientIds;
    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("clientComment") String clientComment;
    private @QueryParam("providerDementi") String providerDementi;

    @Inject
    private ProviderFacade providerFacade;

    @Inject
    private ClientFacade clientFacade;

    public List<Long> getClientIds() {
        return clientIds;
    }

    public void setClientIds(List<Long> clientIds) {
        this.clientIds = clientIds;
    }

    public List<Client> getClients() throws NotFoundException {
        if(getClientIds() != null && getClientIds().size() > 0) {
            final List<Client> clients = clientFacade.find( new ArrayList<>(getClientIds()) );
            if(clients.size() != getClientIds().size()) throw new NotFoundException("Could not find clients for all provided ids.");
            return clients;
        }
        return null;
    }

    public List<Long> getProviderIds() {
        return providerIds;
    }

    public void setProviderIds(List<Long> providerIds) {
        this.providerIds = providerIds;
    }

    public List<Provider> getProviders() throws NotFoundException {
        if(getProviderIds() != null && getProviderIds().size() > 0) {
            final List<Provider> providers = providerFacade.find( new ArrayList<>(getProviderIds()) );
            if(providers.size() != getProviderIds().size()) throw new NotFoundException("Could not find providers for all provided ids.");
            return providers;
        }
        return null;
    }

    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    public String getProviderDementi() {
        return providerDementi;
    }

    public void setProviderDementi(String providerDementi) {
        this.providerDementi = providerDementi;
    }
}

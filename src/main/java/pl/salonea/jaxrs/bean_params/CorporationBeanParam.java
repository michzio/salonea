package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.NotFoundException;
import pl.salonea.jaxrs.utils.RESTDateTime;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 17/10/2015.
 */
public class CorporationBeanParam extends AddressBeanParam {

    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("name") String name;
    private @QueryParam("description") String description;
    private @QueryParam("history") String history;
    private @QueryParam("startOpeningDate") RESTDateTime startOpeningDate;
    private @QueryParam("endOpeningDate") RESTDateTime endOpeningDate;

    @Inject
    private ProviderFacade providerFacade;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public RESTDateTime getStartOpeningDate() {
        return startOpeningDate;
    }

    public void setStartOpeningDate(RESTDateTime startOpeningDate) {
        this.startOpeningDate = startOpeningDate;
    }

    public RESTDateTime getEndOpeningDate() {
        return endOpeningDate;
    }

    public void setEndOpeningDate(RESTDateTime endOpeningDate) {
        this.endOpeningDate = endOpeningDate;
    }
}

package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 21/09/2015.
 */
public class IndustryBeanParam extends PaginationBeanParam {

    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("name") String name;
    private @QueryParam("description") String description;

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
}

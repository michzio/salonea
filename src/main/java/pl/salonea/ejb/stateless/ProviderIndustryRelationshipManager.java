package pl.salonea.ejb.stateless;

import pl.salonea.ejb.interfaces.ProviderIndustryRelationshipManagerInterface;
import pl.salonea.entities.Industry;
import pl.salonea.entities.Provider;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Inject;

/**
 * Created by michzio on 15/11/2015.
 */
@Stateless
@LocalBean
public class ProviderIndustryRelationshipManager
        implements ProviderIndustryRelationshipManagerInterface.Local, ProviderIndustryRelationshipManagerInterface.Remote {

    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private IndustryFacade industryFacade;

    @Override
    public void addProviderToIndustry(Long providerId, Long industryId) throws NotFoundException {

        Provider provider = providerFacade.find(providerId);
        if(provider == null)
            throw new NotFoundException("Provider entity could not be found for id " + providerId);
        Industry industry = industryFacade.find(industryId);
        if(industry == null)
            throw new NotFoundException("Industry entity could not be found for id " + industryId);

        provider.getIndustries().add(industry);
        industry.getProviders().add(provider);
    }

    @Override
    public void removeProviderFromIndustry(Long providerId, Long industryId) throws NotFoundException {

        Provider provider = providerFacade.find(providerId);
        if(provider == null)
            throw new NotFoundException("Provider entity could not be found for id " + providerId);
        Industry industry = industryFacade.find(industryId);
        if(industry == null)
            throw new NotFoundException("Industry entity could not be found for id " + industryId);

        provider.getIndustries().remove(industry);
        industry.getProviders().remove(provider);
    }
}

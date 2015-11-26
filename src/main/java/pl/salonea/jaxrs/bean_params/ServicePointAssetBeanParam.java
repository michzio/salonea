package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.CorporationFacade;
import pl.salonea.ejb.stateless.ProviderFacade;
import pl.salonea.ejb.stateless.ServicePointFacade;
import pl.salonea.entities.Corporation;
import pl.salonea.entities.Provider;
import pl.salonea.entities.ServicePoint;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 26/11/2015.
 */
public class ServicePointAssetBeanParam extends PaginationBeanParam {

    private @QueryParam("fileName") List<String> fileNames;
    private @QueryParam("description") List<String> descriptions;
    private @QueryParam("tagName") List<String> tagNames;
    private @QueryParam("keyword") List<String> keywords;
    private @QueryParam("servicePointId") List<ServicePointId> servicePointIds; // {providerId}+{servicePointNumber} composite PK
    private @QueryParam("providerId") List<Long> providerIds;
    private @QueryParam("corporationId") List<Long> corporationIds;

    @Inject
    private ServicePointFacade servicePointFacade;
    @Inject
    private ProviderFacade providerFacade;
    @Inject
    private CorporationFacade corporationFacade;

    public List<String> getFileNames() {
        return fileNames;
    }

    public void setFileNames(List<String> fileNames) {
        this.fileNames = fileNames;
    }

    public List<String> getDescriptions() {
        return descriptions;
    }

    public void setDescriptions(List<String> descriptions) {
        this.descriptions = descriptions;
    }

    public List<String> getTagNames() {
        return tagNames;
    }

    public void setTagNames(List<String> tagNames) {
        this.tagNames = tagNames;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<ServicePointId> getServicePointIds() {
        return servicePointIds;
    }

    public void setServicePointIds(List<ServicePointId> servicePointIds) {
        this.servicePointIds = servicePointIds;
    }

    public List<ServicePoint> getServicePoints() throws NotFoundException {
        if(getServicePointIds() != null && getServicePointIds().size() > 0) {
            final List<ServicePoint> servicePoints = servicePointFacade.find( new ArrayList<>(getServicePointIds()));
            if(servicePoints.size() != getServicePointIds().size()) throw new NotFoundException("Could not find service points for all provided ids.");
            return servicePoints;
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
    public List<Long> getCorporationIds() {
        return corporationIds;
    }

    public void setCorporationIds(List<Long> corporationIds) {
        this.corporationIds = corporationIds;
    }

    public List<Corporation> getCorporations() throws NotFoundException {
        if(getCorporationIds() != null && getCorporationIds().size() > 0) {
            final List<Corporation> corporations = corporationFacade.find( new ArrayList<>(getCorporationIds()) );
            if(corporations.size() != getCorporationIds().size()) throw new NotFoundException("Could not find corporations for all provided ids.");
            return corporations;
        }
        return null;
    }
}

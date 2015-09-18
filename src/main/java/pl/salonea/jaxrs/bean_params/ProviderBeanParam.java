package pl.salonea.jaxrs.bean_params;

import pl.salonea.ejb.stateless.*;
import pl.salonea.entities.*;
import pl.salonea.enums.ProviderType;
import pl.salonea.jaxrs.exceptions.NotFoundException;

import javax.inject.Inject;
import javax.ws.rs.QueryParam;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 10/09/2015.
 */
public class ProviderBeanParam extends PaginationBeanParam {

    private @QueryParam("providerName") String providerName;
    private @QueryParam("description") String description;
    private @QueryParam("corporationId") List<Long> corporationIds;
    private @QueryParam("providerType") List<ProviderType> providerTypes;
    private @QueryParam("industryId") List<Long> industryIds;
    private @QueryParam("paymentMethodId") List<Integer> paymentMethodIds;
    private @QueryParam("serviceId") List<Integer> serviceIds;
    private @QueryParam("rated") Boolean rated;
    private @QueryParam("minAvgRating") Double minAvgRating;
    private @QueryParam("maxAvgRating") Double maxAvgRating;
    private @QueryParam("ratingClientId") List<Long> ratingClientIds;

    @Inject
    private CorporationFacade corporationFacade;

    @Inject
    private IndustryFacade industryFacade;

    @Inject
    private PaymentMethodFacade paymentMethodFacade;

    @Inject
    private ServiceFacade serviceFacade;

    @Inject
    private ClientFacade clientFacade;

    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Long> getCorporationIds() {
        return corporationIds;
    }

    public void setCorporationIds(List<Long> corporationIds) {
        this.corporationIds = corporationIds;
    }

    public List<Corporation> getCorporations() throws NotFoundException {
        if(getCorporationIds() != null && getCorporationIds().size() > 0) {
            final List<Corporation> corporations = corporationFacade.find(new ArrayList<>(getCorporationIds()));
            if(corporations.size() != getCorporationIds().size()) throw new NotFoundException("Could not find corporations for all provided ids.");
            return corporations;
        }
        return null;
    }

    public List<ProviderType> getProviderTypes() {
        return providerTypes;
    }

    public void setProviderTypes(List<ProviderType> providerTypes) {
        this.providerTypes = providerTypes;
    }

    public List<Long> getIndustryIds() {
        return industryIds;
    }

    public void setIndustryIds(List<Long> industryIds) {
        this.industryIds = industryIds;
    }

    public List<Industry> getIndustries() throws NotFoundException {
        if(getIndustryIds() != null && getIndustryIds().size() > 0) {
            final List<Industry> industries = industryFacade.find(new ArrayList<>(getIndustryIds()));
            if(industries.size() != getIndustryIds().size()) throw new NotFoundException("Could not find industries for all provided ids.");
            return industries;
        }
        return null;
    }

    public List<Integer> getPaymentMethodIds() {
        return paymentMethodIds;
    }

    public void setPaymentMethodIds(List<Integer> paymentMethodIds) {
        this.paymentMethodIds = paymentMethodIds;
    }

    public List<PaymentMethod> getPaymentMethods() throws NotFoundException {
        if(getPaymentMethodIds() != null && getPaymentMethodIds().size() > 0) {
            final List<PaymentMethod> paymentMethods = paymentMethodFacade.find(new ArrayList<>(getPaymentMethodIds()));
            if(paymentMethods.size() != getPaymentMethodIds().size()) throw new NotFoundException("Could not find payment methods for all provided ids.");
            return paymentMethods;
        }
        return null;
    }

    public List<Integer> getServiceIds() {
        return serviceIds;
    }

    public void setServiceIds(List<Integer> serviceIds) {
        this.serviceIds = serviceIds;
    }

    public List<Service> getServices() throws NotFoundException {
        if(getServiceIds() != null && getServiceIds().size() > 0) {
            final List<Service> services = serviceFacade.find(new ArrayList<>(getServiceIds()));
            if(services.size() != getServiceIds().size()) throw new NotFoundException("Could not find services for all provided ids.");
            return services;
        }
        return null;
    }

    public Boolean getRated() {
        return rated;
    }

    public void setRated(Boolean rated) {
        this.rated = rated;
    }

    public Double getMinAvgRating() {
        return minAvgRating;
    }

    public void setMinAvgRating(Double minAvgRating) {
        this.minAvgRating = minAvgRating;
    }

    public Double getMaxAvgRating() {
        return maxAvgRating;
    }

    public void setMaxAvgRating(Double maxAvgRating) {
        this.maxAvgRating = maxAvgRating;
    }

    public List<Long> getRatingClientIds() {
        return ratingClientIds;
    }

    public void setRatingClientIds(List<Long> ratingClientIds) {
        this.ratingClientIds = ratingClientIds;
    }

    public List<Client> getRatingClients() throws NotFoundException {
        if(getRatingClientIds() != null && getRatingClientIds().size() > 0) {
            final List<Client> ratingClients = clientFacade.find(new ArrayList<>(getRatingClientIds()));
            if(ratingClients.size() != getRatingClientIds().size()) throw new NotFoundException("Could not find rating clients for all provided ids.");
            return ratingClients;
        }
        return null;
    }
}

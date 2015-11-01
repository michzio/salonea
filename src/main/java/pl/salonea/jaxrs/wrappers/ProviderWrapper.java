package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 12/09/2015.
 */
@XmlRootElement(name = "provider")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ProviderWrapper {

    private Provider provider;
    private Set<Industry> industries;
    private Set<PaymentMethod> acceptedPaymentMethods;
    private Set<ServicePoint> servicePoints;
    private Set<ProviderService> suppliedServiceOffers;
    private Set<ProviderRating> receivedRatings;

    // default no-args constructor
    public ProviderWrapper() { }

    public ProviderWrapper(Provider provider) {
        this.provider = provider;
        this.industries = provider.getIndustries();
        this.acceptedPaymentMethods = provider.getAcceptedPaymentMethods();
        this.servicePoints = provider.getServicePoints();
        this.suppliedServiceOffers = provider.getSuppliedServiceOffers();
        this.receivedRatings = provider.getReceivedRatings();
    }

    public static List<ProviderWrapper> wrap(List<Provider> providers) {

        List<ProviderWrapper> wrappedProviders = new ArrayList<>();

        for(Provider provider : providers)
            wrappedProviders.add(new ProviderWrapper(provider));

        return wrappedProviders;
    }

    @XmlElement(name = "entity", nillable = true)
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @XmlElement(name = "industries", nillable = true)
    public Set<Industry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<Industry> industries) {
        this.industries = industries;
    }

    @XmlElement(name = "acceptedPaymentMethods", nillable = true)
    public Set<PaymentMethod> getAcceptedPaymentMethods() {
        return acceptedPaymentMethods;
    }

    public void setAcceptedPaymentMethods(Set<PaymentMethod> acceptedPaymentMethods) {
        this.acceptedPaymentMethods = acceptedPaymentMethods;
    }

    @XmlElement(name = "servicePoints", nillable = true)
    public Set<ServicePoint> getServicePoints() {
        return servicePoints;
    }

    public void setServicePoints(Set<ServicePoint> servicePoints) {
        this.servicePoints = servicePoints;
    }

    @XmlElement(name = "suppliedServiceOffers", nillable = true)
    public Set<ProviderService> getSuppliedServiceOffers() {
        return suppliedServiceOffers;
    }

    public void setSuppliedServiceOffers(Set<ProviderService> suppliedServiceOffers) {
        this.suppliedServiceOffers = suppliedServiceOffers;
    }

    @XmlElement(name = "receivedRatings", nillable = true)
    public Set<ProviderRating> getReceivedRatings() {
        return receivedRatings;
    }

    public void setReceivedRatings(Set<ProviderRating> receivedRatings) {
        this.receivedRatings = receivedRatings;
    }
}

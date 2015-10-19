package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.ProviderService;
import pl.salonea.entities.Service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 19/10/2015.
 */
@XmlRootElement(name = "service")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServiceWrapper {

    private Service service;
    private Set<ProviderService> providerServices;

    // default no-args constructor
    public ServiceWrapper() { }

    public ServiceWrapper(Service service) {
        this.service = service;
        this.providerServices = service.getProvidedServiceOffers();
    }

    public static List<ServiceWrapper> wrap(List<Service> services) {

        List<ServiceWrapper> wrappedServices = new ArrayList<>();

        for(Service service : services)
            wrappedServices.add(new ServiceWrapper(service));

        return wrappedServices;
    }

    @XmlElement(name = "entity", nillable = true)
    public Service getService() {
        return service;
    }

    public void setService(Service service) {
        this.service = service;
    }

    @XmlElement(name = "provider-services", nillable = true)
    public Set<ProviderService> getProviderServices() {
        return providerServices;
    }

    public void setProviderServices(Set<ProviderService> providerServices) {
        this.providerServices = providerServices;
    }
}

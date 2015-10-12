package pl.salonea.jaxrs.utils;


import pl.salonea.entities.*;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by michzio on 03/09/2015.
 */
@XmlRootElement(name = "resources")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "resources", "links"})
public class ResourceList<T> {

    private List<T> resources;

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    // default no-args constructor
    public ResourceList() {
    }

    public ResourceList(List<T> resources) {
       this.resources = resources;
    }

    // getters and setter
    @XmlElementWrapper(name = "collection")
    @XmlElements({
            @XmlElement(name = "user-account", type = UserAccount.class),
            @XmlElement(name = "natural-person", type = NaturalPerson.class),
            @XmlElement(name = "firm", type = Firm.class),
            @XmlElement(name = "provider", type = Provider.class),
            @XmlElement(name = "provider", type = ProviderWrapper.class),
            @XmlElement(name = "industry", type = Industry.class),
            @XmlElement(name = "industry", type = IndustryWrapper.class),
            @XmlElement(name = "payment-method", type = PaymentMethod.class),
            @XmlElement(name = "payment-method", type = PaymentMethodWrapper.class),
            @XmlElement(name = "service-point", type = ServicePoint.class ),
            @XmlElement(name = "service-point", type = ServicePointWrapper.class),
            @XmlElement(name = "provider-service", type = ProviderService.class),
            @XmlElement(name = "provider-service", type = ProviderServiceWrapper.class),
            @XmlElement(name = "provider-rating", type = ProviderRating.class),
    })
    public List<T> getResources() {
        return resources;
    }

    public void setResources(List<T> resources) {
        this.resources = resources;
    }

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

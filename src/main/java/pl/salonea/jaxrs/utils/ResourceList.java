package pl.salonea.jaxrs.utils;


import pl.salonea.entities.*;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.jaxrs.wrappers.*;

import javax.ws.rs.core.UriInfo;
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
            @XmlElement(name = "client", type = Client.class),
            @XmlElement(name = "client", type = ClientWrapper.class),
            @XmlElement(name = "employee", type = Employee.class),
            @XmlElement(name = "employee", type = EmployeeWrapper.class),
            @XmlElement(name = "employee-rating", type = EmployeeRating.class),
            @XmlElement(name = "credit-card", type = CreditCard.class),
            @XmlElement(name = "corporation", type = Corporation.class),
            @XmlElement(name = "corporation", type = CorporationWrapper.class),
            @XmlElement(name = "photo", type = ServicePointPhoto.class),
            @XmlElement(name = "photo", type = ServicePointPhotoWrapper.class),
            @XmlElement(name = "virtual-tour", type = VirtualTour.class),
            @XmlElement(name = "virtual-tour", type = VirtualTourWrapper.class),
            @XmlElement(name = "tag", type = Tag.class),
            @XmlElement(name = "tag", type = TagWrapper.class),
            @XmlElement(name = "employee-term", type = TermEmployeeWorkOn.class),
            @XmlElement(name = "skill", type = Skill.class),
            @XmlElement(name = "skill", type = SkillWrapper.class),
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

    public static void generateNavigationLinks(ResourceList resources, UriInfo uriInfo,Integer offset, Integer limit) {

        // navigation links through collection of resources
        if(offset != null && limit != null) {
            // self collection link
            resources.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", offset).queryParam("limit", limit).build()).rel("self").build() );
            // prev collection link
            Integer prevOffset = (offset - limit) < 0 ? 0 : offset - limit;
            Integer prevLimit = offset - prevOffset;
            if(prevLimit > 0)
                resources.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", prevOffset).queryParam("limit", prevLimit).build()).rel("prev").build() );
            else
                resources.getLinks().add( Link.fromUri("").rel("prev").build() );
            // next collection link
            resources.getLinks().add( Link.fromUri(uriInfo.getAbsolutePathBuilder().queryParam("offset", (offset+limit)).queryParam("limit", limit).build()).rel("next").build() );
        } else {
            resources.getLinks().add( Link.fromUri(uriInfo.getAbsolutePath()).rel("self").build() );
        }
    }
}

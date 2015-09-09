package pl.salonea.jaxrs.utils;


import pl.salonea.entities.NaturalPerson;
import pl.salonea.entities.UserAccount;
import pl.salonea.jaxrs.utils.hateoas.Link;

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

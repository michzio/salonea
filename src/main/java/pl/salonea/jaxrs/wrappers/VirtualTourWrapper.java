package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.Tag;
import pl.salonea.entities.VirtualTour;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 27/11/2015.
 */
@XmlRootElement(name = "virtual-tour")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class VirtualTourWrapper {

    private VirtualTour virtualTour;
    private Set<Tag> tags;

    // default no-args constructor
    public VirtualTourWrapper() { }

    public VirtualTourWrapper( VirtualTour virtualTour ) {
        this.virtualTour = virtualTour;
        this.tags = virtualTour.getTags();
    }

    public static List<VirtualTourWrapper> wrap( List<VirtualTour> virtualTours ) {

        List<VirtualTourWrapper> wrappedVirtualTours = new ArrayList<>();

        for (VirtualTour virtualTour : virtualTours)
            wrappedVirtualTours.add(new VirtualTourWrapper(virtualTour));

        return wrappedVirtualTours;
    }

    @XmlElement(name = "entity", nillable = true)
    public VirtualTour getVirtualTour() {
        return virtualTour;
    }

    public void setVirtualTour(VirtualTour virtualTour) {
        this.virtualTour = virtualTour;
    }

    @XmlElement(name = "tags", nillable = true)
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}

package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.ServicePointPhoto;
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
@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class TagWrapper {

    private Tag tag;
    private Set<ServicePointPhoto> photos;
    private Set<VirtualTour> virtualTours;

    // default no-args constructor
    public TagWrapper() { }

    public TagWrapper(Tag tag) {
        this.tag = tag;
        this.photos = tag.getTaggedPhotos();
        this.virtualTours = tag.getTaggedVirtualTours();
    }

    public static List<TagWrapper> wrap(List<Tag> tags) {

        List<TagWrapper> wrappedTags = new ArrayList<>();

        for(Tag tag : tags)
            wrappedTags.add(new TagWrapper(tag));

        return wrappedTags;
    }

    @XmlElement(name = "entity", nillable = true)
    public Tag getTag() {
        return tag;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    @XmlElement(name = "photos", nillable = true)
    public Set<ServicePointPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ServicePointPhoto> photos) {
        this.photos = photos;
    }

    @XmlElement(name = "virtual-tours", nillable = true)
    public Set<VirtualTour> getVirtualTours() {
        return virtualTours;
    }

    public void setVirtualTours(Set<VirtualTour> virtualTours) {
        this.virtualTours = virtualTours;
    }
}

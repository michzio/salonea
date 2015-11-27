package pl.salonea.jaxrs.wrappers;

import pl.salonea.entities.ServicePointPhoto;
import pl.salonea.entities.Tag;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by michzio on 26/11/2015.
 */
@XmlRootElement(name = "photo")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ServicePointPhotoWrapper {

    private ServicePointPhoto photo;
    private Set<Tag> tags;

    // default no-args constructor
    public ServicePointPhotoWrapper() { }

    public ServicePointPhotoWrapper( ServicePointPhoto photo ) {
        this.photo = photo;
        this.tags = photo.getTags();
    }

    public static List<ServicePointPhotoWrapper> wrap(List<ServicePointPhoto> photos) {

        List<ServicePointPhotoWrapper> wrappedPhotos = new ArrayList<>();

        for(ServicePointPhoto photo : photos)
            wrappedPhotos.add(new ServicePointPhotoWrapper(photo));

        return wrappedPhotos;
    }

    @XmlElement(name = "entity", nillable = true)
    public ServicePointPhoto getPhoto() {
        return photo;
    }

    public void setPhoto(ServicePointPhoto photo) {
        this.photo = photo;
    }

    @XmlElement(name = "tags", nillable = true)
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
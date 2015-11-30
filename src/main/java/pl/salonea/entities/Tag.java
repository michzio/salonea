package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "tag")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"tagId", "tagName", "links"})

@Entity
@Table(name = "tag")
@Access(AccessType.PROPERTY)
@NamedQueries({
         @NamedQuery(name = Tag.FIND_ALL_EAGERLY, query = "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.taggedPhotos LEFT JOIN FETCH t.taggedVirtualTours"),
         @NamedQuery(name = Tag.FIND_BY_ID_EAGERLY, query = "SELECT t FROM Tag t LEFT JOIN FETCH t.taggedPhotos LEFT JOIN FETCH t.taggedVirtualTours WHERE t.tagId = :tag_id"),
         @NamedQuery(name = Tag.FIND_BY_TAG_NAME, query = "SELECT t FROM Tag t WHERE t.tagName LIKE :tag_name"),
         @NamedQuery(name = Tag.FIND_BY_SERVICE_POINT_PHOTO, query = "SELECT t FROM Tag t WHERE :service_point_photo MEMBER OF t.taggedPhotos"),
         @NamedQuery(name = Tag.FIND_BY_SERVICE_POINT_PHOTO_EAGERLY, query = "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.taggedPhotos LEFT JOIN FETCH t.taggedVirtualTours WHERE :service_point_photo MEMBER OF t.taggedPhotos"),
         @NamedQuery(name = Tag.FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME, query = "SELECT t FROM Tag t WHERE :service_point_photo MEMBER OF t.taggedPhotos AND t.tagName LIKE :tag_name"),
         @NamedQuery(name = Tag.FIND_BY_VIRTUAL_TOUR, query = "SELECT t FROM Tag t WHERE :virtual_tour MEMBER OF t.taggedVirtualTours"),
         @NamedQuery(name = Tag.FIND_BY_VIRTUAL_TOUR_EAGERLY, query = "SELECT DISTINCT t FROM Tag t LEFT JOIN FETCH t.taggedPhotos LEFT JOIN FETCH t.taggedVirtualTours WHERE :virtual_tour MEMBER OF t.taggedVirtualTours"),
         @NamedQuery(name = Tag.FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME, query = "SELECT t FROM Tag t WHERE :virtual_tour MEMBER OF t.taggedVirtualTours AND t.tagName LIKE :tag_name"),
         @NamedQuery(name = Tag.COUNT_BY_SERVICE_POINT_PHOTO, query = "SELECT COUNT(t) FROM Tag t WHERE :service_point_photo MEMBER OF t.taggedPhotos"),
         @NamedQuery(name = Tag.COUNT_BY_VIRTUAL_TOUR, query = "SELECT COUNT(t) FROM Tag t WHERE :virtual_tour MEMBER OF t.taggedVirtualTours"),
})
public class Tag implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Tag.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Tag.findByIdEagerly";
    public static final String FIND_BY_TAG_NAME = "Tag.findByTagName";
    public static final String FIND_BY_SERVICE_POINT_PHOTO = "Tag.findByServicePointPhoto";
    public static final String FIND_BY_SERVICE_POINT_PHOTO_EAGERLY = "Tag.findByServicePointPhotoEagerly";
    public static final String FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME = "Tag.findByServicePointPhotoAndTagName";
    public static final String FIND_BY_VIRTUAL_TOUR = "Tag.findByVirtualTour";
    public static final String FIND_BY_VIRTUAL_TOUR_EAGERLY = "Tag.findByVirtualTourEagerly";
    public static final String FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME = "Tag.findByVirtualTourAndTagName";
    public static final String COUNT_BY_SERVICE_POINT_PHOTO = "Tag.countByServicePointPhoto";
    public static final String COUNT_BY_VIRTUAL_TOUR = "Tag.countByVirtualTour";

    private Long tagId;
    private String tagName;

    /* many-to-many relationships */

    private Set<ServicePointPhoto> taggedPhotos = new HashSet<>();
    private Set<VirtualTour> taggedVirtualTours = new HashSet<>();
    // TODO potentially other tagged things like employees, providers, news, etc.

    /* HATEOAS support for RESTful web service in JAX-RS */
    private List<Link> links = new ArrayList<>();

    /* constructors */

    public Tag() { }

    public Tag(String tagName) {
        this.tagName = tagName;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "tag_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED"*/)
    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    @NotNull
    @Size(min=2, max=45)
    @Column(name = "tag_name", nullable = false, unique = true, length = 45)
    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    /* many-to-many relationships */

    @XmlTransient
    @ManyToMany(mappedBy = "tags", cascade = { CascadeType.MERGE })
    public Set<ServicePointPhoto> getTaggedPhotos() {
        return taggedPhotos;
    }

    public void setTaggedPhotos(Set<ServicePointPhoto> taggedPhotos) {
        this.taggedPhotos = taggedPhotos;
    }

    @XmlTransient
    @ManyToMany(mappedBy = "tags", cascade = { CascadeType.MERGE })
    public Set<VirtualTour> getTaggedVirtualTours() {
        return taggedVirtualTours;
    }

    public void setTaggedVirtualTours(Set<VirtualTour> taggedVirtualTours) {
        this.taggedVirtualTours = taggedVirtualTours;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getTagName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Tag))
            return false;
        if (obj == this)
            return true;

        Tag rhs = (Tag) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getTagName(), rhs.getTagName())
                .isEquals();
    }

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}
package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tag")
@Access(AccessType.PROPERTY)
@NamedQueries({
         @NamedQuery(name = Tag.FIND_BY_TAG_NAME, query = "SELECT t FROM Tag t WHERE t.tagName LIKE :tag_name"),
         @NamedQuery(name = Tag.FIND_BY_SERVICE_POINT_PHOTO, query = "SELECT t FROM Tag t WHERE :service_point_photo MEMBER OF t.taggedPhotos"),
         @NamedQuery(name = Tag.FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME, query = "SELECT t FROM Tag t WHERE :service_point_photo MEMBER OF t.taggedPhotos AND t.tagName LIKE :tag_name"),
         @NamedQuery(name = Tag.FIND_BY_VIRTUAL_TOUR, query = "SELECT t FROM Tag t WHERE :virtual_tour MEMBER OF t.taggedVirtualTours"),
         @NamedQuery(name = Tag.FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME, query = "SELECT t FROM Tag t WHERE :virtual_tour MEMBER OF t.taggedVirtualTours AND t.tagName LIKE :tag_name")
})
public class Tag implements Serializable {

    public static final String FIND_BY_TAG_NAME = "Tag.findByTagName";
    public static final String FIND_BY_SERVICE_POINT_PHOTO = "Tag.findByServicePointPhoto";
    public static final String FIND_BY_SERVICE_POINT_PHOTO_AND_TAG_NAME = "Tag.findByServicePointPhotoAndTagName";
    public static final String FIND_BY_VIRTUAL_TOUR = "Tag.findByVirtualTour";
    public static final String FIND_BY_VIRTUAL_TOUR_AND_TAG_NAME = "Tag.findByVirtualTourAndTagName";

    private Long tagId;
    private String tagName;

    /* many-to-many relationships */

    private Set<ServicePointPhoto> taggedPhotos = new HashSet<>();
    private Set<VirtualTour> taggedVirtualTours = new HashSet<>();
    // TODO potentially other tagged things like employees, providers, news, etc.

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

    @ManyToMany(mappedBy = "tags", cascade = { CascadeType.MERGE })
    public Set<ServicePointPhoto> getTaggedPhotos() {
        return taggedPhotos;
    }

    public void setTaggedPhotos(Set<ServicePointPhoto> taggedPhotos) {
        this.taggedPhotos = taggedPhotos;
    }

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
}
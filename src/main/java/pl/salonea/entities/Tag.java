package pl.salonea.entities;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

@Entity
@Table(name = "tag")
@Access(AccessType.PROPERTY)
public class Tag {

    private Long tagId;
    private String tagName;

    /* many-to-many relationships */

    private Set<ServicePointPhoto> taggedPhotos;
    private Set<VirtualTour> taggedVirtualTours;
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
    @Column(name = "tag_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
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

    @ManyToMany(mappedBy = "tags")
    public Set<ServicePointPhoto> getTaggedPhotos() {
        return taggedPhotos;
    }

    public void setTaggedPhotos(Set<ServicePointPhoto> taggedPhotos) {
        this.taggedPhotos = taggedPhotos;
    }

    @ManyToMany(mappedBy = "tags")
    public Set<VirtualTour> getTaggedVirtualTours() {
        return taggedVirtualTours;
    }

    public void setTaggedVirtualTours(Set<VirtualTour> taggedVirtualTours) {
        this.taggedVirtualTours = taggedVirtualTours;
    }
}
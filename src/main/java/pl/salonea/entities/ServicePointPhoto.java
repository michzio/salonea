package pl.salonea.entities;


import pl.salonea.constraints.ImageName;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name = "service_point_photo")
@Access(AccessType.PROPERTY)
public class ServicePointPhoto implements Serializable {

    private Long photoId; // PK

    /* simple attributes */
    private String fileName;
    private String description;

    /* relationships */
    private ServicePoint servicePoint; // composite FK
    private Set<Tag> tags;

    /* constructors */

    public ServicePointPhoto() {}

    public ServicePointPhoto(String fileName, ServicePoint servicePoint) {
        this.fileName = fileName;
        this.servicePoint = servicePoint;
    }

    /* PK getter and setter */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name="photo_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getPhotoId() {
        return photoId;
    }

    public void setPhotoId(Long photoId) {
        this.photoId = photoId;
    }

     /*  getters and setters */

    @NotNull
    @ImageName
    @Column(name = "photo_filename", nullable = false, unique = true, length = 45)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }


    @Lob
    @Column(name = "photo_description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* obligatory relationship */

    @JoinColumns(value = {
            @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false),
            @JoinColumn(name = "service_point_no", referencedColumnName = "service_point_no", nullable = false),
    }, foreignKey = @ForeignKey(name = "fk_service_point_photo_service_point"))
    @ManyToOne(fetch = FetchType.EAGER)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    /* other relationships */

    @ManyToMany
    @JoinTable( name = "photo_tagged_with",
            joinColumns = @JoinColumn(name = "photo_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "tag_id", nullable = false)
    )
    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}

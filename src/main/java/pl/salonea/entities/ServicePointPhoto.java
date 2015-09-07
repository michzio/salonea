package pl.salonea.entities;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.ImageName;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "service_point_photo")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ServicePointPhoto.FIND_BY_FILE_NAME, query = "SELECT photo FROM ServicePointPhoto photo WHERE photo.fileName LIKE :file_name"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_DESCRIPTION, query = "SELECT photo FROM ServicePointPhoto photo WHERE photo.description LIKE :description"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_FILE_NAME_AND_DESCRIPTION, query = "SELECT photo FROM ServicePointPhoto photo WHERE photo.fileName LIKE :file_name AND photo.description LIKE :description"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_KEYWORD, query = "SELECT photo FROM ServicePointPhoto photo WHERE photo.fileName LIKE :keyword OR photo.description LIKE :keyword"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_TAG_NAME, query = "SELECT DISTINCT photo FROM ServicePointPhoto photo INNER JOIN photo.tags tag WHERE tag.tagName LIKE :tag_name"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_ANY_TAG_NAMES, query = "SELECT DISTINCT photo FROM ServicePointPhoto photo INNER JOIN photo.tags tag WHERE tag.tagName IN :tag_names"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_ALL_TAG_NAMES, query = "SELECT photo FROM ServicePointPhoto photo JOIN photo.tags tag WHERE tag.tagName IN :tag_names GROUP BY photo.photoId HAVING COUNT(photo.photoId) = :tag_count"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_KEYWORD_INCLUDING_TAGS, query = "SELECT DISTINCT photo FROM ServicePointPhoto photo INNER JOIN photo.tags tag WHERE photo.fileName LIKE :keyword OR photo.description LIKE :keyword OR tag.tagName LIKE :keyword"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_SERVICE_POINT, query = "SELECT photo FROM ServicePointPhoto photo WHERE photo.servicePoint = :service_point"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_PROVIDER, query = "SELECT photo FROM ServicePointPhoto photo INNER JOIN photo.servicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = ServicePointPhoto.FIND_BY_CORPORATION, query = "SELECT photo FROM ServicePointPhoto photo INNER JOIN photo.servicePoint sp INNER JOIN sp.provider p WHERE p.corporation = :corporation")
})
public class ServicePointPhoto implements Serializable {

    public static final String FIND_BY_FILE_NAME = "ServicePointPhoto.findByFileName";
    public static final String FIND_BY_DESCRIPTION = "ServicePointPhoto.findByDescription";
    public static final String FIND_BY_FILE_NAME_AND_DESCRIPTION = "ServicePointPhoto.findByFileNameAndDescription";
    public static final String FIND_BY_KEYWORD = "ServicePointPhoto.findByKeyword";
    public static final String FIND_BY_TAG_NAME = "ServicePointPhoto.findByTagName";
    public static final String FIND_BY_ANY_TAG_NAMES = "ServicePointPhoto.findByAnyTagNames";
    public static final String FIND_BY_ALL_TAG_NAMES = "ServicePointPhoto.findByAllTagNames";
    public static final String FIND_BY_KEYWORD_INCLUDING_TAGS = "ServicePointPhoto.findByKeywordIncludingTags";
    public static final String FIND_BY_SERVICE_POINT = "ServicePointPhoto.findByServicePoint";
    public static final String FIND_BY_PROVIDER = "ServicePointPhoto.findByProvider";
    public static final String FIND_BY_CORPORATION = "ServicePointPhoto.findByCorporation";

    private Long photoId; // PK

    /* simple attributes */
    private String fileName;
    private String description;

    /* relationships */
    private ServicePoint servicePoint; // composite FK
    private Set<Tag> tags = new HashSet<>();

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
    @Column(name="photo_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED"*/)
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

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode())
                .append(getFileName())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ServicePointPhoto))
            return false;
        if (obj == this)
            return true;

        ServicePointPhoto rhs = (ServicePointPhoto) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getFileName(), rhs.getFileName())
                .isEquals();
    }
}

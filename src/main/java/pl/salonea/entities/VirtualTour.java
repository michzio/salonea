package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.VirtualTourName;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "virtual-tour")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"tourId", "fileName", "description", "servicePoint", "links"})

@Entity
@Table(name = "virtual_tour")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = VirtualTour.FIND_ALL_EAGERLY, query = "SELECT DISTINCT vt FROM VirtualTour vt LEFT JOIN FETCH vt.tags t"),
        @NamedQuery(name = VirtualTour.FIND_BY_ID_EAGERLY, query = "SELECT vt FROM VirtualTour vt LEFT JOIN FETCH vt.tags t WHERE vt.tourId = :tour_id"),
        @NamedQuery(name = VirtualTour.FIND_BY_FILE_NAME, query = "SELECT vt FROM VirtualTour vt WHERE vt.fileName LIKE :file_name"),
        @NamedQuery(name = VirtualTour.FIND_BY_DESCRIPTION, query = "SELECT vt FROM VirtualTour vt WHERE vt.description LIKE :description"),
        @NamedQuery(name = VirtualTour.FIND_BY_FILE_NAME_AND_DESCRIPTION, query = "SELECT vt FROM VirtualTour vt WHERE vt.fileName LIKE :file_name AND vt.description LIKE :description"),
        @NamedQuery(name = VirtualTour.FIND_BY_KEYWORD, query = "SELECT vt FROM VirtualTour vt WHERE vt.fileName LIKE :keyword OR vt.description LIKE :keyword"),
        @NamedQuery(name = VirtualTour.FIND_BY_TAG_NAME, query = "SELECT DISTINCT vt FROM VirtualTour vt INNER JOIN vt.tags tag WHERE tag.tagName LIKE :tag_name"),
        @NamedQuery(name = VirtualTour.FIND_BY_ANY_TAG_NAMES, query = "SELECT DISTINCT vt FROM VirtualTour vt INNER JOIN vt.tags tag WHERE tag.tagName IN :tag_names"),
        @NamedQuery(name = VirtualTour.FIND_BY_ALL_TAG_NAMES, query = "SELECT vt FROM VirtualTour vt JOIN vt.tags tag WHERE tag.tagName IN :tag_names GROUP BY vt.tourId HAVING COUNT(vt.tourId) = :tag_count"),
        @NamedQuery(name = VirtualTour.FIND_BY_KEYWORD_INCLUDING_TAGS, query = "SELECT DISTINCT vt FROM VirtualTour vt INNER JOIN vt.tags tag WHERE vt.fileName LIKE :keyword OR vt.description LIKE :keyword OR tag.tagName LIKE :keyword"),
        @NamedQuery(name = VirtualTour.FIND_BY_SERVICE_POINT, query = "SELECT vt FROM VirtualTour vt WHERE vt.servicePoint = :service_point"),
        @NamedQuery(name = VirtualTour.FIND_BY_SERVICE_POINT_EAGERLY, query = "SELECT DISTINCT vt FROM VirtualTour vt LEFT JOIN FETCH vt.tags WHERE vt.servicePoint = :service_point"),
        @NamedQuery(name = VirtualTour.FIND_BY_PROVIDER, query = "SELECT vt FROM VirtualTour vt INNER JOIN vt.servicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = VirtualTour.FIND_BY_PROVIDER_EAGERLY, query = "SELECT DISTINCT vt FROM VirtualTour vt LEFT JOIN FETCH vt.tags INNER JOIN vt.servicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = VirtualTour.FIND_BY_CORPORATION, query = "SELECT vt FROM VirtualTour vt INNER JOIN vt.servicePoint sp INNER JOIN sp.provider p WHERE p.corporation = :corporation"),
        @NamedQuery(name = VirtualTour.FIND_BY_CORPORATION_EAGERLY, query = "SELECT DISTINCT vt FROM VirtualTour vt LEFT JOIN FETCH vt.tags INNER JOIN vt.servicePoint sp INNER JOIN sp.provider p WHERE p.corporation = :corporation"),
        @NamedQuery(name = VirtualTour.FIND_BY_TAG, query = "SELECT vt FROM VirtualTour vt WHERE :tag MEMBER OF vt.tags"),
        @NamedQuery(name = VirtualTour.FIND_BY_TAG_EAGERLY, query = "SELECT DISTINCT vt FROM VirtualTour vt INNER JOIN FETCH vt.tags t WHERE t = :tag"),
        @NamedQuery(name = VirtualTour.COUNT_BY_SERVICE_POINT, query = "SELECT COUNT(vt) FROM VirtualTour vt WHERE vt.servicePoint = :service_point"),
        @NamedQuery(name = VirtualTour.COUNT_BY_PROVIDER, query = "SELECT COUNT(vt) FROM VirtualTour vt INNER JOIN vt.servicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = VirtualTour.COUNT_BY_CORPORATION, query = "SELECT COUNT(vt) FROM VirtualTour vt INNER JOIN vt.servicePoint sp INNER JOIN sp.provider p WHERE p.corporation = :corporation"),
        @NamedQuery(name = VirtualTour.COUNT_BY_TAG, query = "SELECT COUNT(vt) FROM VirtualTour vt WHERE :tag MEMBER OF vt.tags"),
        @NamedQuery(name = VirtualTour.DELETE_BY_SERVICE_POINT, query = "DELETE FROM VirtualTour vt WHERE vt.servicePoint = :service_point"),
        @NamedQuery(name = VirtualTour.DELETE_BY_ID, query = "DELETE FROM VirtualTour vt WHERE vt.tourId = :tour_id"),
})
public class VirtualTour implements Serializable {

    public static final String FIND_ALL_EAGERLY = "VirtualTour.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "VirtualTour.findByIdEagerly";
    public static final String FIND_BY_FILE_NAME = "VirtualTour.findByFileName";
    public static final String FIND_BY_DESCRIPTION = "VirtualTour.findByDescription";
    public static final String FIND_BY_FILE_NAME_AND_DESCRIPTION = "VirtualTour.findByFileNameAndDescription";
    public static final String FIND_BY_KEYWORD = "VirtualTour.findByKeyword";
    public static final String FIND_BY_TAG_NAME = "VirtualTour.findByTagName";
    public static final String FIND_BY_ANY_TAG_NAMES = "VirtualTour.findByAnyTagNames";
    public static final String FIND_BY_ALL_TAG_NAMES = "VirtualTour.findByAllTagNames";
    public static final String FIND_BY_KEYWORD_INCLUDING_TAGS = "VirtualTour.findByKeywordIncludingTags";
    public static final String FIND_BY_SERVICE_POINT = "VirtualTour.findByServicePoint";
    public static final String FIND_BY_SERVICE_POINT_EAGERLY = "VirtualTour.findByServicePointEagerly";
    public static final String FIND_BY_PROVIDER = "VirtualTour.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "VirtualTour.findByProviderEagerly";
    public static final String FIND_BY_CORPORATION = "VirtualTour.findByCorporation";
    public static final String FIND_BY_CORPORATION_EAGERLY = "VirtualTour.findByCorporationEagerly";
    public static final String FIND_BY_TAG = "VirtualTour.findByTag";
    public static final String FIND_BY_TAG_EAGERLY = "VirtualTour.findByTagEagerly";
    public static final String COUNT_BY_SERVICE_POINT = "VirtualTour.countByServicePoint";
    public static final String COUNT_BY_PROVIDER = "VirtualTour.countByProvider";
    public static final String COUNT_BY_CORPORATION = "VirtualTour.countByCorporation";
    public static final String COUNT_BY_TAG = "VirtualTour.countByTag";
    public static final String DELETE_BY_SERVICE_POINT = "VirtualTour.deleteByServicePoint";
    public static final String DELETE_BY_ID = "VirtualTour.deleteById";

    private Long tourId; // PK

    /* simple attributes */
    private String fileName;
    private String description;

    /* relationships */
    private ServicePoint servicePoint; // composite FK
    private Set<Tag> tags = new HashSet<>();

    /* HATEOAS support for RESTful web service in JAX-RS */
    private LinkedHashSet<Link> links = new LinkedHashSet<>();

    /* constructors */

    public VirtualTour() {}

    public VirtualTour(String fileName, ServicePoint servicePoint) {
        this.fileName = fileName;
        this.servicePoint = servicePoint;
    }

    /* PK getter and setter */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "virtual_tour_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED"*/)
    public Long getTourId() {
        return tourId;
    }

    public void setTourId(Long tourId) {
        this.tourId = tourId;
    }

    /* getters and setters */

    @NotNull
    @VirtualTourName
    @Column(name = "virtual_tour_filename", nullable = false, unique = true, length = 45)
    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
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
    }, foreignKey = @ForeignKey(name = "fk_virtual_tour_service_point"))
    @ManyToOne(fetch = FetchType.EAGER)
    public ServicePoint getServicePoint() {
        return servicePoint;
    }

    public void setServicePoint(ServicePoint servicePoint) {
        this.servicePoint = servicePoint;
    }

    /* other relationships */

    @XmlTransient
    @ManyToMany
    @JoinTable(name = "tour_tagged_with",
            joinColumns = @JoinColumn(name = "virtual_tour_id", nullable = false),
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
        if (obj == null || !(obj instanceof VirtualTour))
            return false;
        if (obj == this)
            return true;

        VirtualTour rhs = (VirtualTour) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getFileName(), rhs.getFileName())
                .isEquals();
    }

    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    @Transient
    public LinkedHashSet<Link> getLinks() {
        return links;
    }

    public void setLinks(LinkedHashSet<Link> links) {
        this.links = links;
    }
}
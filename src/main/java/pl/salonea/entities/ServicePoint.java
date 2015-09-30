package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.constraints.PhoneNumber;
import pl.salonea.constraints.SkypeName;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.idclass.ServicePointId;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "service-point")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "servicePointNumber", "provider", "address", "phoneNumber", "skypeName", "latitudeWGS84", "longitudeWGS84"  })

@Entity
@IdClass(ServicePointId.class)
@Table(name = "service_point")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ServicePoint.FIND_ALL_EAGERLY, query = "SELECT sp FROM ServicePoint sp LEFT JOIN FETCH sp.workStations ws LEFT JOIN FETCH sp.photos ph LEFT JOIN FETCH sp.virtualTours vt"),
        @NamedQuery(name = ServicePoint.FIND_BY_ID_EAGERLY, query = "SELECT sp FROM ServicePoint sp LEFT JOIN FETCH sp.workStations ws LEFT JOIN FETCH sp.photos ph LEFT JOIN FETCH sp.virtualTours vt WHERE sp.provider.userId = :userId AND sp.servicePointNumber = :servicePointNumber"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER, query = "SELECT sp FROM ServicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER_EAGERLY, query = "SELECT sp FROM ServicePoint sp LEFT JOIN FETCH sp.workStations ws LEFT JOIN FETCH sp.photos ph LEFT JOIN FETCH sp.virtualTours vt WHERE sp.provider = :provider" ),
        @NamedQuery(name = ServicePoint.FIND_BY_ADDRESS, query = "SELECT sp FROM ServicePoint sp WHERE sp.address.city LIKE :city AND sp.address.state LIKE :state " +
                "AND sp.address.country LIKE :country AND sp.address.street LIKE :street AND sp.address.zipCode LIKE :zip_code"),
        @NamedQuery(name = ServicePoint.FIND_BY_COORDINATES_SQUARE, query = "SELECT sp FROM ServicePoint sp WHERE sp.longitudeWGS84 >= :min_longitude_wgs84 AND sp.longitudeWGS84 <= :max_longitude_wgs84 AND sp.latitudeWGS84 >= :min_latitude_wgs84 AND sp.latitudeWGS84 <= :max_latitude_wgs84"),
        @NamedQuery(name = ServicePoint.FIND_BY_COORDINATES_CIRCLE, query = "SELECT sp FROM ServicePoint sp WHERE SQRT((sp.longitudeWGS84 - :longitude_wgs84)*(sp.longitudeWGS84 - :longitude_wgs84) + (sp.latitudeWGS84 - :latitude_wgs84)*(sp.latitudeWGS84 - :latitude_wgs84)) <= :radius"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER_AND_ADDRESS, query = "SELECT sp FROM ServicePoint sp WHERE sp.provider = :provider AND sp.address.city LIKE :city AND sp.address.state LIKE :state " +
                "AND sp.address.country LIKE :country AND sp.address.street LIKE :street AND sp.address.zipCode LIKE :zip_code"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_SQUARE, query = "SELECT sp FROM ServicePoint sp WHERE sp.provider = :provider AND sp.longitudeWGS84 >= :min_longitude_wgs84 AND sp.longitudeWGS84 <= :max_longitude_wgs84 AND sp.latitudeWGS84 >= :min_latitude_wgs84 AND sp.latitudeWGS84 <= :max_latitude_wgs84"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER_AND_COORDINATES_CIRCLE, query = "SELECT sp FROM ServicePoint sp WHERE sp.provider = :provider AND SQRT((sp.longitudeWGS84 - :longitude_wgs84)*(sp.longitudeWGS84 - :longitude_wgs84) + (sp.latitudeWGS84 - :latitude_wgs84)*(sp.latitudeWGS84 - :latitude_wgs84)) <= :radius"),
        @NamedQuery(name = ServicePoint.FIND_BY_SERVICE_AND_ADDRESS, query = "SELECT sp FROM ServicePoint sp INNER JOIN sp.workStations ws INNER JOIN ws.providedServices ps WHERE ps.service = :service AND sp.address.city LIKE :city AND sp.address.state LIKE :state " +
                "AND sp.address.country LIKE :country AND sp.address.street LIKE :street AND sp.address.zipCode LIKE :zip_code"),
        @NamedQuery(name = ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_SQUARE, query = "SELECT sp FROM ServicePoint sp INNER JOIN sp.workStations ws INNER JOIN ws.providedServices ps WHERE ps.service = :service AND sp.longitudeWGS84 >= :min_longitude_wgs84 AND sp.longitudeWGS84 <= :max_longitude_wgs84 AND sp.latitudeWGS84 >= :min_latitude_wgs84 AND sp.latitudeWGS84 <= :max_latitude_wgs84 "),
        @NamedQuery(name = ServicePoint.FIND_BY_SERVICE_AND_COORDINATES_CIRCLE, query = "SELECT sp FROM ServicePoint sp INNER JOIN sp.workStations ws INNER JOIN ws.providedServices ps WHERE ps.service = :service AND SQRT((sp.longitudeWGS84 - :longitude_wgs84)*(sp.longitudeWGS84 - :longitude_wgs84) + (sp.latitudeWGS84 - :latitude_wgs84)*(sp.latitudeWGS84 - :latitude_wgs84)) <= :radius"),
        @NamedQuery(name = ServicePoint.FIND_BY_SERVICE, query = "SELECT DISTINCT sp FROM ServicePoint sp INNER JOIN sp.workStations ws INNER JOIN ws.providedServices ps WHERE ps.service = :service"),
        @NamedQuery(name = ServicePoint.FIND_BY_EMPLOYEE, query = "SELECT DISTINCT sp FROM ServicePoint sp INNER JOIN sp.workStations ws INNER JOIN ws.termsEmployeesWorkOn term WHERE term.employee = :employee"),
        @NamedQuery(name = ServicePoint.FIND_BY_PROVIDER_SERVICE, query = "SELECT DISTINCT sp FROM ServicePoint sp INNER JOIN sp.workStations ws WHERE :provider_service MEMBER OF ws.providedServices"),
        @NamedQuery(name = ServicePoint.FIND_BY_CORPORATION, query = "SELECT sp FROM ServicePoint sp INNER JOIN sp.provider p WHERE p.corporation = :corporation"),
        @NamedQuery(name = ServicePoint.FIND_BY_INDUSTRY, query = "SELECT sp FROM ServicePoint sp INNER JOIN sp.provider p WHERE :industry MEMBER OF p.industries"),
        @NamedQuery(name = ServicePoint.COUNT_BY_PROVIDER, query = "SELECT COUNT(sp) FROM ServicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = ServicePoint.DELETE_BY_PROVIDER, query = "DELETE FROM ServicePoint sp WHERE sp.provider = :provider"),
        @NamedQuery(name = ServicePoint.DELETE_BY_ID, query = "DELETE FROM ServicePoint sp WHERE sp.provider.userId = :userId AND sp.servicePointNumber = :servicePointNumber"),
})
public class ServicePoint implements Serializable {

    public static final String FIND_ALL_EAGERLY = "ServicePoint.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "ServicePoint.findByIdEagerly";
    public static final String FIND_BY_PROVIDER = "ServicePoint.findByProvider";
    public static final String FIND_BY_PROVIDER_EAGERLY = "ServicePoint.findByProviderEagerly";
    public static final String FIND_BY_ADDRESS = "ServicePoint.findByAddress";
    public static final String FIND_BY_COORDINATES_SQUARE = "ServicePoint.findByCoordinatesSquare";
    public static final String FIND_BY_COORDINATES_CIRCLE = "ServicePoint.findByCoordinatesCircle";
    public static final String FIND_BY_PROVIDER_AND_ADDRESS = "ServicePoint.findByProviderAndAddress";
    public static final String FIND_BY_PROVIDER_AND_COORDINATES_SQUARE = "ServicePoint.findByProviderAndCoordinatesSquare";
    public static final String FIND_BY_PROVIDER_AND_COORDINATES_CIRCLE = "ServicePoint.findByProviderAndCoordinatesCircle";
    public static final String FIND_BY_SERVICE_AND_ADDRESS = "ServicePoint.findByServiceAndAddress";
    public static final String FIND_BY_SERVICE_AND_COORDINATES_SQUARE = "ServicePoint.findByServiceAndCoordinatesSquare";
    public static final String FIND_BY_SERVICE_AND_COORDINATES_CIRCLE = "ServicePoint.findByServiceAndCoordinatesCircle";
    public static final String FIND_BY_SERVICE = "ServicePoint.findByService";
    public static final String FIND_BY_EMPLOYEE = "ServicePoint.findByEmployee";
    public static final String FIND_BY_PROVIDER_SERVICE = "ServicePoint.findByProviderService";
    public static final String FIND_BY_CORPORATION = "ServicePoint.findByCorporation";
    public static final String FIND_BY_INDUSTRY = "ServicePoint.findByIndustry";
    public static final String COUNT_BY_PROVIDER = "ServicePoint.countByProvider";
    public static final String DELETE_BY_PROVIDER = "ServicePoint.deleteByProvider";
    public static final String DELETE_BY_ID = "ServicePoint.deleteById";

    private Integer servicePointNumber; // PK
    private Provider provider; // PK, FK

    private Address address;
    private String phoneNumber;
    private String skypeName;

    private Float latitudeWGS84;
    private Float longitudeWGS84;

    /* ServicePoint can be assigned by many photos and tours */
    private Set<ServicePointPhoto> photos;
    private Set<VirtualTour> virtualTours;

    /* ServicePoint can have many WorkStations */
    private Set<WorkStation> workStations;

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    /* constructors */

    public ServicePoint() {}

    public ServicePoint(Provider provider, Integer servicePointNumber, Address address) {
        this.provider = provider;
        this.servicePointNumber = servicePointNumber;
        this.address = address;
    }

    /* getters and setters */

    @Id
    @Basic(optional = false)
    @Column(name = "service_point_no", nullable = false, columnDefinition = "INT UNSIGNED")
    public Integer getServicePointNumber() {
        return servicePointNumber;
    }

    public void setServicePointNumber(Integer servicePointNumber) {
        this.servicePointNumber = servicePointNumber;
    }

    /* many-to-one relationship to provider, provider id is FK and part of composite PK */

    @Id
    @NotNull
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED",
        foreignKey = @ForeignKey(name = "fk_service_point_provider"))
    @ManyToOne(fetch = FetchType.EAGER)
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    @Valid
    @NotNull
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "street",
                    column = @Column(name = "address_street", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "houseNumber",
                    column = @Column(name = "address_building_no", nullable = false, columnDefinition = "VARCHAR(6)")),
            @AttributeOverride(name = "flatNumber",
                    column = @Column(name = "address_shop_no")),
            @AttributeOverride(name = "zipCode",
                    column = @Column(name = "address_zip_code", nullable = false, columnDefinition = "VARCHAR(10)")),
            @AttributeOverride(name = "city",
                    column = @Column(name = "address_city", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "state",
                    column = @Column(name = "address_state", nullable = false, columnDefinition = "VARCHAR(255)")),
            @AttributeOverride(name = "country",
                    column = @Column(name = "address_country", nullable = false, columnDefinition = "VARCHAR(45)"))
    })
    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @PhoneNumber
    @Column(name = "phone_number", length = 20, columnDefinition = "VARCHAR(20) DEFAULT NULL")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @SkypeName
    @Column(name = "skype_name", length = 32, columnDefinition = "VARCHAR(32) DEFAULT NULL")
    public String getSkypeName() {
        return skypeName;
    }

    public void setSkypeName(String skypeName) {
        this.skypeName = skypeName;
    }

    // TODO some WGS84 constraint
    @Column(name = "latitude_wgs84", columnDefinition = "FLOAT(10,6) DEFAULT NULL")
    public Float getLatitudeWGS84() {
        return latitudeWGS84;
    }

    public void setLatitudeWGS84(Float latitudeWGS84) {
        this.latitudeWGS84 = latitudeWGS84;
    }

    // TODO some WGS84 constraint
    @Column(name = "longitude_wgs84", columnDefinition = "FLOAT(10,6) DEFAULT NULL")
    public Float getLongitudeWGS84() {
        return longitudeWGS84;
    }

    public void setLongitudeWGS84(Float longitudeWGS84) {
        this.longitudeWGS84 = longitudeWGS84;
    }

    @XmlTransient
    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<ServicePointPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ServicePointPhoto> photos) {
        this.photos = photos;
    }

    @XmlTransient
    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<VirtualTour> getVirtualTours() {
        return virtualTours;
    }

    public void setVirtualTours(Set<VirtualTour> virtualTours) {
        this.virtualTours = virtualTours;
    }

    @XmlTransient
    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<WorkStation> getWorkStations() {
        return workStations;
    }

    public void setWorkStations(Set<WorkStation> workStations) {
        this.workStations = workStations;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31) // two randomly chosen prime numbers
                // if deriving: .appendSuper(super.hashCode()).
                .append(getProvider())
                .append(getServicePointNumber())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ServicePoint))
            return false;
        if (obj == this)
            return true;

        ServicePoint rhs = (ServicePoint) obj;
        return new EqualsBuilder()
                // if deriving: .appendSuper(super.equals(obj)).
                .append(getProvider(), rhs.getProvider())
                .append(getServicePointNumber(), rhs.getServicePointNumber())
                .isEquals();
    }

    @Transient
    @XmlElementWrapper(name = "links")
    @XmlElement(name = "link")
    public List<Link> getLinks() {
        return links;
    }

    public void setLinks(List<Link> links) {
        this.links = links;
    }
}

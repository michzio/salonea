package pl.salonea.entities;

import pl.salonea.constraints.PhoneNumber;
import pl.salonea.constraints.SkypeName;
import pl.salonea.embeddables.Address;
import pl.salonea.entities.idclass.ServicePointId;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Entity
@Table(name = "service_point")
@Access(AccessType.PROPERTY)
@IdClass(ServicePointId.class)
public class ServicePoint {

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

    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY)
    public Set<ServicePointPhoto> getPhotos() {
        return photos;
    }

    public void setPhotos(Set<ServicePointPhoto> photos) {
        this.photos = photos;
    }

    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY)
    public Set<VirtualTour> getVirtualTours() {
        return virtualTours;
    }

    public void setVirtualTours(Set<VirtualTour> virtualTours) {
        this.virtualTours = virtualTours;
    }

    @OneToMany(mappedBy = "servicePoint", fetch = FetchType.LAZY)
    public Set<WorkStation> getWorkStations() {
        return workStations;
    }

    public void setWorkStations(Set<WorkStation> workStations) {
        this.workStations = workStations;
    }
}

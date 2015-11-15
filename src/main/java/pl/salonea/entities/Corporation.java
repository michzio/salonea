package pl.salonea.entities;

import pl.salonea.constraints.ImageName;
import pl.salonea.constraints.PhoneNumber;
import pl.salonea.constraints.SkypeName;
import pl.salonea.embeddables.Address;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.mapped_superclasses.UUIDEntity;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.*;

@XmlRootElement(name = "corporation")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"corporationId", "corporationName", "logo", "description", "openingDate", "history", "address", "phoneNumber", "skypeName", "links" })

@Entity
@Table(name = "corporation")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Corporation.FIND_ALL_EAGERLY, query = "SELECT DISTINCT c FROM Corporation c LEFT JOIN FETCH c.providers"),
        @NamedQuery(name = Corporation.FIND_BY_ID_EAGERLY, query = "SELECT c FROM Corporation c LEFT JOIN FETCH c.providers WHERE c.corporationId = :corporationId"),
        @NamedQuery(name = Corporation.FIND_BY_ADDRESS,  query = "SELECT c FROM Corporation c WHERE c.address.city LIKE :city AND c.address.state LIKE :state " +
                "AND c.address.country LIKE :country AND c.address.street LIKE :street AND c.address.zipCode LIKE :zip_code"),
        @NamedQuery(name = Corporation.FIND_BY_NAME, query = "SELECT c FROM Corporation c WHERE c.corporationName LIKE :name"),
        @NamedQuery(name = Corporation.FIND_BY_DESCRIPTION, query = "SELECT c FROM Corporation c WHERE c.description LIKE :description"),
        @NamedQuery(name = Corporation.FIND_BY_HISTORY, query = "SELECT c FROM Corporation c WHERE c.history LIKE :history"),
        @NamedQuery(name = Corporation.FIND_BY_KEYWORD, query = "SELECT c FROM Corporation c WHERE c.corporationName LIKE :keyword OR c.description LIKE :keyword OR c.history LIKE :keyword"),
        @NamedQuery(name = Corporation.FIND_OPEN_AFTER, query = "SELECT c FROM Corporation c WHERE c.openingDate >= :date"),
        @NamedQuery(name = Corporation.FIND_OPEN_BEFORE, query = "SELECT c FROM Corporation c WHERE c.openingDate <= :date"),
        @NamedQuery(name = Corporation.FIND_OPEN_BETWEEN, query = "SELECT c FROM Corporation c WHERE c.openingDate >= :startDate AND c.openingDate <= :endDate"),
        @NamedQuery(name = Corporation.FIND_FOR_PROVIDER, query = "SELECT c FROM Corporation c WHERE :provider MEMBER OF c.providers"),
        @NamedQuery(name = Corporation.FIND_FOR_PROVIDER_EAGERLY, query = "SELECT c FROM Corporation c INNER JOIN FETCH c.providers p WHERE p = :provider"),
})
public class Corporation extends UUIDEntity implements Serializable {

    public static final String FIND_ALL_EAGERLY = "Corporation.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Corporation.findByIdEagerly";
    public static final String FIND_BY_ADDRESS = "Corporation.findByAddress";
    public static final String FIND_BY_NAME = "Corporation.findByName";
    public static final String FIND_BY_DESCRIPTION = "Corporation.findByDescription";
    public static final String FIND_BY_HISTORY = "Corporation.findByHistory";
    public static final String FIND_BY_KEYWORD = "Corporation.findByKeyword";
    public static final String FIND_OPEN_AFTER = "Corporation.findOpenAfter";
    public static final String FIND_OPEN_BEFORE = "Corporation.findOpenBefore";
    public static final String FIND_OPEN_BETWEEN = "Corporation.findOpenBetween";
    public static final String FIND_FOR_PROVIDER = "Corporation.findForProvider";
    public static final String FIND_FOR_PROVIDER_EAGERLY = "Corporation.findForProviderEagerly";

    private Long corporationId;
    private String corporationName;
    private String logo;
    private String description;
    private Date openingDate;
    private String history;

    private Address address;

    private String phoneNumber;
    private String skypeName;

    /* one-to-many relationship with providers */
    private Set<Provider> providers = new HashSet<>();

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    /* constructors */

    public Corporation() {
    }

    public Corporation(String corporationName, String logo, Address address) {
        this.address = address;
        this.corporationName = corporationName;
        this.logo = logo;
    }

    public Corporation(String corporationName, String logo, String description, Date openingDate, String history, Address address, String phoneNumber, String skypeName) {

        this.corporationName = corporationName;
        this.logo = logo;
        this.description = description;
        this.openingDate = openingDate;
        this.history = history;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.skypeName = skypeName;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "corporation_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED" */)
    public Long getCorporationId() {
        return corporationId;
    }

    public void setCorporationId(Long corporationId) {
        this.corporationId = corporationId;
    }

    @NotNull(message = "Corporation name can not be null.")
    @Size(min=2, max = 45)
    @Column(name = "corporation_name", nullable = false, length = 45)
    public String getCorporationName() {
        return corporationName;
    }

    public void setCorporationName(String corporationName) {
        this.corporationName = corporationName;
    }

    @NotNull
    @ImageName
    @Column(name = "logo", nullable = false, length = 45)
    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Past
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "opening_date", columnDefinition = "DATETIME default NULL")
    public Date getOpeningDate() {
        return openingDate;
    }

    public void setOpeningDate(Date openingDate) {
        this.openingDate = openingDate;
    }

    @Lob
    @Column(name = "history", columnDefinition = "LONGTEXT default NULL")
    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
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
                    column = @Column(name = "address_office_no")),
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

    /* one-to-many relationship with providers */

    @XmlTransient
    // TODO: Corporation should have on update CASCADE_UPDATE, on delete SET_NULL
    @OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
    // @OrderBy("providerName ASC") Hibernate + MySQL error
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
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

package pl.salonea.entities;

import pl.salonea.constraints.ImageName;
import pl.salonea.constraints.PhoneNumber;
import pl.salonea.constraints.SkypeName;
import pl.salonea.embeddables.Address;

import javax.persistence.*;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Date;
import java.util.Set;

@Entity
@Table(name = "corporation")
@Access(AccessType.PROPERTY)
public class Corporation implements Serializable {


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
    private Set<Provider> providers;

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
    @Column(name = "corporation_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
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

    // TODO: Corporation should have on update CASCADE_UPDATE, on delete SET_NULL
    @OneToMany(mappedBy = "corporation", fetch = FetchType.LAZY)
    // @OrderBy("providerName ASC") Hibernate + MySQL error
    public Set<Provider> getProviders() {
        return providers;
    }

    public void setProviders(Set<Provider> providers) {
        this.providers = providers;
    }
}

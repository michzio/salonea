package pl.salonea.entities;

import pl.salonea.constraints.NaturalPersonOrFirm;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="client")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Client.FIND_BY_FIRST_NAME, query = "SELECT c FROM Client c WHERE c.naturalPerson.firstName LIKE :fname"),
        @NamedQuery(name = Client.FIND_BY_LAST_NAME, query = "SELECT c FROM Client c WHERE c.naturalPerson.lastName LIKE :lname"),
        @NamedQuery(name = Client.FIND_BY_NAMES, query = "SELECT c FROM Client c WHERE c.naturalPerson.firstName LIKE :fname AND c.naturalPerson.lastName LIKE :lname"),
        @NamedQuery(name = Client.FIND_BORN_AFTER, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate >= :date"),
        @NamedQuery(name = Client.FIND_BORN_BEFORE, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate <= :date"),
        @NamedQuery(name = Client.FIND_BORN_BETWEEN, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate >= :start_date AND c.naturalPerson.birthDate <= :end_date"),
        @NamedQuery(name = Client.FIND_BY_LOCATION, query = "SELECT c FROM Client c WHERE c.naturalPerson.homeAddress.city LIKE :city AND c.naturalPerson.homeAddress.state LIKE :state " +
                "AND c.naturalPerson.homeAddress.country LIKE :country AND c.naturalPerson.homeAddress.street LIKE :street AND c.naturalPerson.homeAddress.zipCode LIKE :zip_code"),
        @NamedQuery(name = Client.FIND_BY_DELIVERY, query = "SELECT c FROM Client c WHERE c.naturalPerson.deliveryAddress.city LIKE :city AND c.naturalPerson.deliveryAddress.state LIKE :state " +
        "AND c.naturalPerson.deliveryAddress.country LIKE :country AND c.naturalPerson.deliveryAddress.street LIKE :street AND c.naturalPerson.deliveryAddress.zipCode LIKE :zip_code")
})
@NaturalPersonOrFirm
public class Client implements Serializable{

    public final static String FIND_BY_FIRST_NAME = "Client.findByFirstName";
    public final static String FIND_BY_LAST_NAME = "Client.findByLastName";
    public final static String FIND_BY_NAMES = "Client.findByNames";
    public final static String FIND_BORN_AFTER = "Client.findBornAfter";
    public final static String FIND_BORN_BEFORE = "Client.findBornBefore";
    public final static String FIND_BORN_BETWEEN = "Client.findBornBetween";
    public final static String FIND_BY_LOCATION = "Client.findByLocation";
    public final static String FIND_BY_DELIVERY = "Client.findByDelivery";

    private Long clientId;
    private String description;

    // one-to-one relationships with:
    private NaturalPerson naturalPerson;
    private Firm firm;

    // one-to-many relationships with:
    private Set<CreditCard> creditCards;
    private Set<ProviderRating> providerRatings;
    private Set<EmployeeRating> employeeRatings;

    /* constructor */

    public Client() { }

    public Client(String description) {
        this.description = description;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER)
    public NaturalPerson getNaturalPerson() {
        return naturalPerson;
    }

    public void setNaturalPerson(NaturalPerson naturalPerson) {
        this.naturalPerson = naturalPerson;
    }

    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER)
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    public Set<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(Set<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    public Set<ProviderRating> getProviderRatings() {
        return providerRatings;
    }

    public void setProviderRatings(Set<ProviderRating> providerRatings) {
        this.providerRatings = providerRatings;
    }

    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY)
    public Set<EmployeeRating> getEmployeeRatings() {
        return employeeRatings;
    }

    public void setEmployeeRatings(Set<EmployeeRating> employeeRatings) {
        this.employeeRatings = employeeRatings;
    }
}
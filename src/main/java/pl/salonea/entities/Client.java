package pl.salonea.entities;

import pl.salonea.constraints.NaturalPersonOrFirm;
import pl.salonea.jaxrs.utils.hateoas.Link;
import pl.salonea.mapped_superclasses.UUIDEntity;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@XmlRootElement(name = "client")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = {"clientId", "description", "naturalPersonId", "firmId"})

@Entity
@Table(name="client")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Client.FIND_ALL_EAGERLY, query = "SELECT DISTINCT c FROM Client c LEFT JOIN FETCH c.creditCards LEFT JOIN FETCH c.providerRatings LEFT JOIN FETCH c.employeeRatings"),
        @NamedQuery(name = Client.FIND_BY_ID_EAGERLY, query = "SELECT c FROM Client c LEFT JOIN FETCH c.creditCards LEFT JOIN FETCH c.providerRatings LEFT JOIN FETCH c.employeeRatings WHERE c.clientId = :clientId"),
        @NamedQuery(name = Client.FIND_BY_FIRST_NAME, query = "SELECT c FROM Client c WHERE c.naturalPerson.firstName LIKE :fname"),
        @NamedQuery(name = Client.FIND_BY_LAST_NAME, query = "SELECT c FROM Client c WHERE c.naturalPerson.lastName LIKE :lname"),
        @NamedQuery(name = Client.FIND_BY_PERSON_NAMES, query = "SELECT c FROM Client c WHERE c.naturalPerson.firstName LIKE :fname AND c.naturalPerson.lastName LIKE :lname"),
        @NamedQuery(name = Client.FIND_BY_FIRM_NAME, query = "SELECT c FROM Client c WHERE c.firm.name LIKE :firm_name"),
        @NamedQuery(name = Client.FIND_BY_NAME, query = "SELECT c FROM Client c LEFT JOIN c.naturalPerson np LEFT JOIN c.firm f WHERE np.firstName LIKE :name OR np.lastName LIKE :name OR f.name LIKE :name"),
        @NamedQuery(name = Client.FIND_BY_DESCRIPTION, query = "SELECT c FROM Client c WHERE c.description LIKE :description"),
        @NamedQuery(name = Client.FIND_BORN_AFTER, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate >= :date"),
        @NamedQuery(name = Client.FIND_BORN_BEFORE, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate <= :date"),
        @NamedQuery(name = Client.FIND_BORN_BETWEEN, query = "SELECT c FROM Client c WHERE c.naturalPerson.birthDate >= :start_date AND c.naturalPerson.birthDate <= :end_date"),
        @NamedQuery(name = Client.FIND_BY_LOCATION, query = "SELECT c FROM Client c LEFT JOIN c.naturalPerson np LEFT JOIN c.firm f WHERE (np.homeAddress.city LIKE :city AND np.homeAddress.state LIKE :state " +
                "AND np.homeAddress.country LIKE :country AND np.homeAddress.street LIKE :street AND np.homeAddress.zipCode LIKE :zip_code) OR " +
                "(f.address.city LIKE :city AND f.address.state LIKE :state AND f.address.country LIKE :country AND f.address.street LIKE :street AND f.address.zipCode LIKE :zip_code)"),
        @NamedQuery(name = Client.FIND_BY_DELIVERY, query = "SELECT c FROM Client c LEFT JOIN c.naturalPerson np LEFT JOIN c.firm f WHERE (np.deliveryAddress.city LIKE :city AND np.deliveryAddress.state LIKE :state " +
                "AND np.deliveryAddress.country LIKE :country AND np.deliveryAddress.street LIKE :street AND np.deliveryAddress.zipCode LIKE :zip_code) OR " +
                "(f.address.city LIKE :city AND f.address.state LIKE :state AND f.address.country LIKE :country AND f.address.street LIKE :street AND f.address.zipCode LIKE :zip_code)"),
        @NamedQuery(name = Client.FIND_BY_GENDER, query = "SELECT c FROM Client c WHERE c.naturalPerson.gender = :gender"),
        @NamedQuery(name = Client.FIND_RATING_PROVIDER, query = "SELECT c FROM Client c INNER JOIN c.providerRatings pr WHERE pr.provider = :provider"),
        @NamedQuery(name = Client.FIND_RATING_PROVIDER_EAGERLY, query = "SELECT c FROM Client c LEFT JOIN FETCH c.creditCards LEFT JOIN FETCH c.providerRatings pr LEFT JOIN FETCH c.employeeRatings WHERE pr.provider = :provider"),
        @NamedQuery(name = Client.FIND_RATING_EMPLOYEE, query = "SELECT c FROM Client c INNER JOIN c.employeeRatings er WHERE er.employee = :employee"),
        @NamedQuery(name = Client.FIND_RATING_EMPLOYEE_EAGERLY, query = "SELECT c FROM Client c LEFT JOIN FETCH c.creditCards LEFT JOIN FETCH c.providerRatings LEFT JOIN FETCH c.employeeRatings er WHERE er.employee = :employee"),
        @NamedQuery(name = Client.FIND_ONLY_FIRMS, query = "SELECT c FROM Client c WHERE c.firm IS NOT NULL AND c.naturalPerson IS NULL"),
        @NamedQuery(name = Client.FIND_ONLY_NATURAL_PERSONS, query = "SELECT c FROM Client c WHERE c.firm IS NULL AND c.naturalPerson IS NOT NULL"),
        @NamedQuery(name = Client.FIND_NOT_ASSIGNED, query = "SELECT c FROM Client c WHERE c.firm IS NULL AND c.naturalPerson IS NULL"),
})
@NaturalPersonOrFirm
public class Client extends UUIDEntity implements Serializable{

    public static final String FIND_ALL_EAGERLY = "Client.findAllEagerly";
    public static final String FIND_BY_ID_EAGERLY = "Client.findByIdEagerly";
    public static final String FIND_BY_FIRST_NAME = "Client.findByFirstName";
    public static final String FIND_BY_LAST_NAME = "Client.findByLastName";
    public static final String FIND_BY_PERSON_NAMES = "Client.findByPersonNames";
    public static final String FIND_BY_FIRM_NAME = "Client.findByFirmName";
    public static final String FIND_BY_NAME = "Client.findByName";
    public static final String FIND_BY_DESCRIPTION = "Client.findByDescription";
    public static final String FIND_BORN_AFTER = "Client.findBornAfter";
    public static final String FIND_BORN_BEFORE = "Client.findBornBefore";
    public static final String FIND_BORN_BETWEEN = "Client.findBornBetween";
    public static final String FIND_BY_LOCATION = "Client.findByLocation";
    public static final String FIND_BY_DELIVERY = "Client.findByDelivery";
    public static final String FIND_BY_GENDER = "Client.findByGender";
    public static final String FIND_RATING_PROVIDER = "Client.findRatingProvider";
    public static final String FIND_RATING_PROVIDER_EAGERLY = "Client.findRatingProviderEagerly";
    public static final String FIND_RATING_EMPLOYEE = "Client.findRatingEmployee";
    public static final String FIND_RATING_EMPLOYEE_EAGERLY = "Client.findRatingEmployeeEagerly";
    public static final String FIND_ONLY_FIRMS = "Client.findOnlyFirms";
    public static final String FIND_ONLY_NATURAL_PERSONS = "Client.findOnlyNaturalPersons";
    public static final String FIND_NOT_ASSIGNED = "Client.findNotAssigned";

    private Long clientId;
    private String description;

    // one-to-one relationships with:
    private NaturalPerson naturalPerson;
    private Firm firm;

    // one-to-many relationships with:
    private Set<CreditCard> creditCards = new HashSet<>();
    private Set<ProviderRating> providerRatings = new HashSet<>();
    private Set<EmployeeRating> employeeRatings = new HashSet<>();

    // transient fields - are used by jaxb to output in xml/json ids of associated natural person or firm entity
    private Long naturalPersonId;
    private Long firmId;

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    /* constructor */

    public Client() {
    }

    public Client(String description) {
        this.description = description;
    }

    /* getters and setters */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false) // runtime scoped not null, whereas nullable references only to table
    @Column(name = "client_id", nullable = false /*, columnDefinition = "BIGINT UNSIGNED" */ )
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

    @XmlTransient
    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
    public NaturalPerson getNaturalPerson() {
        return naturalPerson;
    }

    public void setNaturalPerson(NaturalPerson naturalPerson) {
        this.naturalPerson = naturalPerson;
    }

    @XmlTransient
    @OneToOne(mappedBy = "client", fetch = FetchType.EAGER, cascade = { CascadeType.REMOVE })
    public Firm getFirm() {
        return firm;
    }

    public void setFirm(Firm firm) {
        this.firm = firm;
    }

    @XmlTransient
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = { CascadeType.REMOVE })
    public Set<CreditCard> getCreditCards() {
        return creditCards;
    }

    public void setCreditCards(Set<CreditCard> creditCards) {
        this.creditCards = creditCards;
    }

    @XmlTransient
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public Set<ProviderRating> getProviderRatings() {
        return providerRatings;
    }

    public void setProviderRatings(Set<ProviderRating> providerRatings) {
        this.providerRatings = providerRatings;
    }

    @XmlTransient
    @OneToMany(mappedBy = "client", fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.REMOVE })
    public Set<EmployeeRating> getEmployeeRatings() {
        return employeeRatings;
    }

    public void setEmployeeRatings(Set<EmployeeRating> employeeRatings) {
        this.employeeRatings = employeeRatings;
    }

    @Transient
    public Long getNaturalPersonId() {
        return naturalPersonId;
    }

    public void setNaturalPersonId(Long naturalPersonId) {
        this.naturalPersonId = naturalPersonId;
    }

    @Transient
    public Long getFirmId() {
        return firmId;
    }

    public void setFirmId(Long firmId) {
        this.firmId = firmId;
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

    /* entity life cycle callback methods */
    @PostPersist
    @PostLoad
    @PostUpdate
    public void onManagedEntityLoaded() {
        populateTransientFields();
    }

    private void populateTransientFields() {

        // populate naturalPersonId when client is associated with natural person
        if(naturalPerson != null) {
            naturalPersonId = naturalPerson.getUserId();
        } else {
            naturalPersonId = null;
        }

        // populate firmId when client is associated with firm
        if(firm != null) {
            firmId = firm.getUserId();
        } else {
            firmId= null;
        }

    }

   /* public void addCreditCard(CreditCard creditCard) {
        this.creditCards.add(creditCard);
        if(creditCard.getClient() != this) {
            creditCard.setClient(this);
        }
    } */

}
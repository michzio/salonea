package pl.salonea.entities;

import pl.salonea.constraints.CorporateOwner;
import pl.salonea.embeddables.Address;
import pl.salonea.enums.ProviderType;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.util.Set;

@Entity
@DiscriminatorValue("provider")
@Table(name = "provider")
@PrimaryKeyJoinColumn(name = "provider_id")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = Provider.FIND_BY_CORPORATION, query = "SELECT p FROM Provider p WHERE p.corporation = :corporation"),
        @NamedQuery(name = Provider.FIND_BY_TYPE, query = "SELECT p FROM Provider p WHERE p.type = :provider_type"),
        @NamedQuery(name = Provider.FIND_BY_INDUSTRY, query = "SELECT p FROM Provider p WHERE :industry MEMBER OF p.industries"),
        @NamedQuery(name = Provider.FIND_BY_PAYMENT_METHOD, query = "SELECT p FROM Provider p WHERE :payment_method MEMBER OF p.acceptedPaymentMethods"),
        @NamedQuery(name = Provider.FIND_BY_SUPPLIED_SERVICE, query = "SELECT p FROM Provider p INNER JOIN p.suppliedServiceOffers ps WHERE ps.service = :service"),
        @NamedQuery(name = Provider.FIND_RATED, query = "SELECT p FROM Provider p WHERE p.receivedRatings.size > 0"),
        @NamedQuery(name = Provider.FIND_UNRATED, query = "SELECT p FROM Provider p WHERE p.receivedRatings.size = 0"),
        @NamedQuery(name = Provider.FIND_ON_AVG_RATED_ABOVE, query = "SELECT p FROM Provider p INNER JOIN p.receivedRatings pr GROUP BY p HAVING AVG(pr.clientRating) >= :avg_rating"),
        @NamedQuery(name = Provider.FIND_ON_AVG_RATED_BELOW, query = "SELECT p FROM Provider p INNER JOIN p.receivedRatings pr GROUP BY p HAVING AVG(pr.clientRating) <= :avg_rating"),
        @NamedQuery(name = Provider.FIND_RATED_BY_CLIENT, query = "SELECT p FROM Provider p INNER JOIN p.receivedRatings pr WHERE pr.client = :client"),
})
@CorporateOwner
public class Provider extends Firm {

    public static final String FIND_BY_CORPORATION = "Provider.findByCorporation";
    public static final String FIND_BY_TYPE = "Provider.findByType";
    public static final String FIND_BY_INDUSTRY = "Provider.findByIndustry";
    public static final String FIND_BY_PAYMENT_METHOD = "Provider.findByPaymentMethod";
    public static final String FIND_BY_SUPPLIED_SERVICE = "Provider.findBySuppliedService";
    public static final String FIND_RATED = "Provider.findRated";
    public static final String FIND_UNRATED = "Provider.findUnrated";
    public static final String FIND_ON_AVG_RATED_ABOVE = "Provider.findOnAvgRatedAbove";
    public static final String FIND_ON_AVG_RATED_BELOW = "Provider.findOnAvgRatedBelow";
    public static final String FIND_RATED_BY_CLIENT = "Provider.findRatedByClient";

    private String providerName;
    private String description;
    private ProviderType type;

    // Provider can belong to Corporation
    private Corporation corporation;

    // Provider can function in many Industries
    private Set<Industry> industries;

    // Provider can accept many PaymentMethods
    private Set<PaymentMethod> acceptedPaymentMethods;

    // Provider can have many ServicePoints
    private Set<ServicePoint> servicePoints;

    // Provider supplies service offers
    private Set<ProviderService> suppliedServiceOffers;

    // Provider is rated by clients
    private Set<ProviderRating> receivedRatings;

    /* constructors */

    public Provider() { }

    public Provider(String email, String login, String password, String name, String vatin , String companyNumber, Address address, String providerName, ProviderType type) {
        super(email, login, password, vatin, name, companyNumber, address);
        this.providerName = providerName;
        this.type = type;
    }

    /* getter and setters */

    @NotNull(message = "Provider name can not be null.")
    @Size(min = 2, max = 45)
    @Column(name = "name", nullable = false, length = 45)
    public String getProviderName() {
        return providerName;
    }

    public void setProviderName(String providerName) {
        this.providerName = providerName;
    }

    @Lob
    @Column(name = "description", columnDefinition = "LONGTEXT default NULL")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, columnDefinition = "ENUM('SIMPLE', 'CORPORATE', 'FRANCHISE') DEFAULT 'SIMPLE'")
    public ProviderType getType() {
        return type;
    }

    public void setType(ProviderType type) {
        this.type = type;
    }

    /* relationships mapping */

    @ManyToOne
    @JoinColumn(name = "corporation_id", columnDefinition = "BIGINT UNSIGNED default NULL",
            foreignKey = @ForeignKey(name = "fk_provider_corporation") )
    public Corporation getCorporation() {
        return corporation;
    }

    public void setCorporation(Corporation corporation) {
        this.corporation = corporation;
    }

    @ManyToMany(mappedBy = "providers")
    public Set<Industry> getIndustries() {
        return industries;
    }

    public void setIndustries(Set<Industry> industries) {
        this.industries = industries;
    }

    @ManyToMany
    @JoinTable(name = "accepted_payment_method",
        joinColumns = @JoinColumn(name = "provider_id"),
        inverseJoinColumns = @JoinColumn(name = "payment_method_id")
    )
    public Set<PaymentMethod> getAcceptedPaymentMethods() {
        return acceptedPaymentMethods;
    }

    public void setAcceptedPaymentMethods(Set<PaymentMethod> acceptedPaymentMethods) {
        this.acceptedPaymentMethods = acceptedPaymentMethods;
    }

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    public Set<ServicePoint> getServicePoints() {
        return servicePoints;
    }

    public void setServicePoints(Set<ServicePoint> servicePoints) {
        this.servicePoints = servicePoints;
    }

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    public Set<ProviderService> getSuppliedServiceOffers() {
        return suppliedServiceOffers;
    }

    public void setSuppliedServiceOffers(Set<ProviderService> suppliedServiceOffers) {
        this.suppliedServiceOffers = suppliedServiceOffers;
    }

    @OneToMany(mappedBy = "provider", fetch = FetchType.LAZY)
    public Set<ProviderRating> getReceivedRatings() {
        return receivedRatings;
    }

    public void setReceivedRatings(Set<ProviderRating> receivedRatings) {
        this.receivedRatings = receivedRatings;
    }
}

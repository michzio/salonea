package pl.salonea.entities;

import pl.salonea.constraints.NaturalPersonOrFirm;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Set;

@Entity
@Table(name="client")
@Access(AccessType.PROPERTY)
@NaturalPersonOrFirm
public class Client implements Serializable{

    private Long clientId;
    private String description;

    // one-to-one relationships with:
    private NaturalPerson naturalPerson;
    private Firm firm;

    private Set<CreditCard> creditCards;
    private Set<ProviderRating> providerRatings;

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
}
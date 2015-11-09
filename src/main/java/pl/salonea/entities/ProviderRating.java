package pl.salonea.entities;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import pl.salonea.entities.idclass.ProviderRatingId;
import pl.salonea.jaxrs.utils.hateoas.Link;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "provider-rating")
@XmlAccessorType(XmlAccessType.PROPERTY)
@XmlType(propOrder = { "client", "provider", "clientRating", "clientComment", "providerDementi", "links"  })

@Entity
@IdClass(ProviderRatingId.class)
@Table(name = "provider_rating")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ProviderRating.FIND_BY_CLIENT, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client"),
        @NamedQuery(name = ProviderRating.FIND_BY_PROVIDER, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.FIND_FOR_PROVIDER_BY_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider AND pr.clientRating = :rating"),
        @NamedQuery(name = ProviderRating.FIND_FOR_PROVIDER_ABOVE_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider AND pr.clientRating >= :min_rating"),
        @NamedQuery(name = ProviderRating.FIND_FOR_PROVIDER_BELOW_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider AND pr.clientRating <= :max_rating"),
        @NamedQuery(name = ProviderRating.FIND_FROM_CLIENT_BY_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client AND pr.clientRating = :rating"),
        @NamedQuery(name = ProviderRating.FIND_FROM_CLIENT_ABOVE_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client AND pr.clientRating >= :min_rating"),
        @NamedQuery(name = ProviderRating.FIND_FROM_CLIENT_BELOW_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client AND pr.clientRating <= :max_rating"),
        @NamedQuery(name = ProviderRating.FIND_PROVIDER_AVG_RATING, query = "SELECT AVG(pr.clientRating) FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.COUNT_PROVIDER_RATINGS, query = "SELECT COUNT(pr) FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.COUNT_CLIENT_RATINGS, query = "SELECT COUNT(pr) FROM ProviderRating pr WHERE pr.client = :client"),
        @NamedQuery(name = ProviderRating.DELETE_BY_CLIENT, query = "DELETE FROM ProviderRating pr WHERE pr.client = :client"),
        @NamedQuery(name = ProviderRating.DELETE_BY_PROVIDER, query = "DELETE FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.DELETE_BY_ID, query = "DELETE FROM ProviderRating pr WHERE pr.provider.userId = :providerId AND pr.client.clientId = :clientId"),
})
public class ProviderRating {

    public static final String FIND_BY_CLIENT = "ProviderRating.findByClient";
    public static final String FIND_BY_PROVIDER = "ProviderRating.findByProvider";
    public static final String FIND_FOR_PROVIDER_BY_RATING = "ProviderRating.findForProviderByRating";
    public static final String FIND_FOR_PROVIDER_ABOVE_RATING = "ProviderRating.findForProviderAboveRating";
    public static final String FIND_FOR_PROVIDER_BELOW_RATING = "ProviderRating.findForProviderBelowRating";
    public static final String FIND_FROM_CLIENT_BY_RATING = "ProviderRating.findFromClientByRating";
    public static final String FIND_FROM_CLIENT_ABOVE_RATING = "ProviderRating.findFromClientAboveRating";
    public static final String FIND_FROM_CLIENT_BELOW_RATING = "ProviderRating.findFromClientBelowRating";
    public static final String FIND_PROVIDER_AVG_RATING = "ProviderRating.findProviderAvgRating";
    public static final String COUNT_PROVIDER_RATINGS = "ProviderRating.countProviderRatings";
    public static final String COUNT_CLIENT_RATINGS = "ProviderRating.countClientRatings";
    public static final String DELETE_BY_CLIENT = "ProviderRating.deleteByClient";
    public static final String DELETE_BY_PROVIDER = "ProviderRating.deleteByProvider";
    public static final String DELETE_BY_ID = "ProviderRating.deleteById";

    private Client client; // PK, FK
    private Provider provider; // PK, FK

    private Short clientRating;
    private String clientComment;
    private String providerDementi;

    // HATEOAS support for RESTFul web service in JAX-RS
    private List<Link> links = new ArrayList<>();

    /* constructors */

    public ProviderRating() { }

    public ProviderRating(Provider provider, Client client) {
        this.client = client;
        this.provider = provider;
    }

    public ProviderRating(Provider provider, Client client, Short clientRating) {
        this.client = client;
        this.provider = provider;
        this.clientRating = clientRating;
    }

    /* PK getters and setters */

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Id
    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "provider_id", referencedColumnName = "provider_id", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    /* other getters and setters */

    @NotNull
    @Min(0) @Max(10)
    @Column(name = "client_rating", nullable = false, columnDefinition = "TINYINT UNSIGNED DEFAULT 0")
    public Short getClientRating() {
        return clientRating;
    }

    public void setClientRating(Short clientRating) {
        this.clientRating = clientRating;
    }

    @Lob
    @Column(name = "client_comment", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getClientComment() {
        return clientComment;
    }

    public void setClientComment(String clientComment) {
        this.clientComment = clientComment;
    }

    @Lob
    @Column(name = "provider_dementi", columnDefinition = "LONGTEXT DEFAULT NULL")
    public String getProviderDementi() {
        return providerDementi;
    }

    public void setProviderDementi(String providerDementi) {
        this.providerDementi = providerDementi;
    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder(17, 31). // two randomly chosen prime numbers
                // if deriving: appendSuper(super.hashCode()).
                 append(getClient())
                .append(getProvider())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ProviderRating))
            return false;
        if (obj == this)
            return true;

        ProviderRating rhs = (ProviderRating) obj;
        return new EqualsBuilder().
                // if deriving: appendSuper(super.equals(obj)).
                 append(getClient(), rhs.getClient())
                .append(getProvider(), rhs.getProvider())
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

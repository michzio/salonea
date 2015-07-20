package pl.salonea.entities;

import pl.salonea.entities.idclass.ProviderRatingId;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@IdClass(ProviderRatingId.class)
@Table(name = "provider_rating")
@Access(AccessType.PROPERTY)
@NamedQueries({
        @NamedQuery(name = ProviderRating.FIND_BY_CLIENT, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client"),
        @NamedQuery(name = ProviderRating.FIND_BY_PROVIDER, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.FIND_FOR_PROVIDER_BY_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.provider = :provider AND pr.clientRating = :rating"),
        @NamedQuery(name = ProviderRating.FIND_FROM_CLIENT_BY_RATING, query = "SELECT pr FROM ProviderRating pr WHERE pr.client = :client AND pr.clientRating = :rating"),
        @NamedQuery(name = ProviderRating.FIND_PROVIDER_AVG_RATING, query = "SELECT AVG(pr.clientRating) FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.COUNT_PROVIDER_RATINGS, query = "SELECT COUNT(pr) FROM ProviderRating pr WHERE pr.provider = :provider"),
        @NamedQuery(name = ProviderRating.COUNT_CLIENT_RATINGS, query = "SELECT COUNT(pr) FROM ProviderRating pr WHERE pr.client = :client")
})
public class ProviderRating {

    public static final String FIND_BY_CLIENT = "ProviderRating.findByClient";
    public static final String FIND_BY_PROVIDER = "ProviderRating.findByProvider";
    public static final String FIND_FOR_PROVIDER_BY_RATING = "ProviderRating.findForProviderByRating";
    public static final String FIND_FROM_CLIENT_BY_RATING = "ProviderRating.findFromClientByRating";
    public static final String FIND_PROVIDER_AVG_RATING = "ProviderRating.findProviderAvgRating";
    public static final String COUNT_PROVIDER_RATINGS = "ProviderRating.countProviderRatings";
    public static final String COUNT_CLIENT_RATINGS = "ProviderRating.countClientRatings";

    private Client client; // PK, FK
    private Provider provider; // PK, FK

    private Short clientRating;
    private String clientComment;
    private String providerDementi;

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


}

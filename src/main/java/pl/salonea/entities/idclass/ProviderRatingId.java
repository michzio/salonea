package pl.salonea.entities.idclass;


import java.io.Serializable;

public class ProviderRatingId implements Serializable {

    private Long provider;
    private Long client;

    /* constructors */

    public ProviderRatingId() { }

    public ProviderRatingId(Long providerId, Long clientId) {
        this.provider = providerId;
        this.client = clientId;
    }

    /* getters and setters */

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long providerId) {
        this.provider = providerId;
    }

    public Long getClient() {
        return client;
    }

    public void setClient(Long clientId) {
        this.client = clientId;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProviderRatingId that = (ProviderRatingId) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        return !(client != null ? !client.equals(that.client) : that.client != null);

    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (client != null ? client.hashCode() : 0);
        return result;
    }
}

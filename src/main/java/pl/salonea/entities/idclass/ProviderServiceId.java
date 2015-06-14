package pl.salonea.entities.idclass;

import java.io.Serializable;

public class ProviderServiceId implements Serializable {

    private Long provider;
    private Integer service;

    /* constructors */

    public ProviderServiceId() { }

    public ProviderServiceId(Long providerId, Integer serviceId) {
        this.provider = providerId;
        this.service = serviceId;
    }

    /* getters and setters */

    public Long getProvider() {
        return provider;
    }

    public void setProvider(Long providerId) {
        this.provider = providerId;
    }

    public Integer getService() {
        return service;
    }

    public void setService(Integer serviceId) {
        this.service = serviceId;
    }

    /* equals() and hashCode() */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProviderServiceId that = (ProviderServiceId) o;

        if (provider != null ? !provider.equals(that.provider) : that.provider != null) return false;
        return !(service != null ? !service.equals(that.service) : that.service != null);

    }

    @Override
    public int hashCode() {
        int result = provider != null ? provider.hashCode() : 0;
        result = 31 * result + (service != null ? service.hashCode() : 0);
        return result;
    }
}
